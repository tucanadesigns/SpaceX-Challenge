package com.tucanadesigns.spacex.repository

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.tucanadesigns.spacex.BuildConfig
import com.tucanadesigns.spacex.launchlist.LaunchListDataModel
import com.tucanadesigns.spacex.missiondetails.MissionDataModel
import com.tucanadesigns.spacex.missiondetails.RocketDataModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.Executors

private const val LOG_TAG = "REPOSITORY"

/**
 * Singleton repository for handling the retrieval of data from the network for the view models
 */
object Repository {

    // Executor for handling background async network requests
    private val executorService = Executors.newSingleThreadExecutor()
    private val uiHandler = Handler(Looper.getMainLooper())

    // Local holder for the returned list of launches, following the initial request
    private var launchList = ArrayList<LaunchListDataModel>()

    /**
     * Request the full list of launches
     */
    fun requestLaunchList(requestListener: LaunchesRequestListener) {

        // Reuse any previously returned list acquired during this session to reduce network calls
        if (launchList.isNotEmpty()) {
            requestListener.onGetLaunchList(launchList)
            return
        }

        // Make an async request for the launch list
        executorService.submit {
            buildLaunchList(URL(BuildConfig.SERVER_URL.plus("/v3/launches").plus("?sort=flight_number&order=asc")).readText())

            // Post the result back to the calling view model
            uiHandler.post {
                requestListener.onGetLaunchList(launchList)
            }
        }
    }

    // Deserialize the server response JSON into a list of launch objects
    private fun buildLaunchList(jsonResponse: String) {
        try {
            val responseArray = JSONArray(jsonResponse)

            for (i in 0 until responseArray.length()) {
                val flight = responseArray.getJSONObject(i)
                val flightNumber = flight.optInt("flight_number", 0)
                val missionName = flight.optString("mission_name", "No Name")
                val launchYear = flight.optString("launch_year", "No Year")
                val launchDate = flight.optLong("launch_date_unix", 0L)
                val launchSuccess = flight.optBoolean("launch_success", false)
                launchList.add(LaunchListDataModel(flightNumber, missionName, launchDate, launchYear, launchSuccess))
            }
        }
        catch (exception: JSONException) {
            Log.e(LOG_TAG, "JSON response error", exception)
        }
    }

    /**
     * Make network requests for the details from a single mission (flight number)
     */
    fun requestMissionDetails(requestListener: MissionRequestListener, flightNumber: Int) {
        executorService.submit {

            // Get the launch details from the first endpoint
            val launchResponse = URL(BuildConfig.SERVER_URL.plus("/v3/launches/").plus(flightNumber).plus("?id=false")).readText()
            val missionDetails = buildMissionDetails(launchResponse)

            // Get the rocket details from the second endpoint
            if (missionDetails != null) {
                val rocketResponse = URL(BuildConfig.SERVER_URL.plus("/v3/rockets/").plus(missionDetails.rocketId).plus("?id=false")).readText()
                missionDetails.rocketDetails = addRocketDetails(rocketResponse)
            }

            // Post the result back to calling view model
            uiHandler.post {
                requestListener.onGetMissionDetails(missionDetails)
            }
        }
    }


    // Build the mission details object from JSON returned from the first network call
    private fun buildMissionDetails(jsonResponse: String): MissionDataModel? {
        var missionDetails: MissionDataModel? = null

        try {
            val missionJson = JSONObject(jsonResponse)

            val flightNumber = missionJson.optInt("flight_number", 0)
            val missionName = missionJson.optString("mission_name", "No Name")
            val description = missionJson.optString("details", "No Description")
            val launchDate = missionJson.optString("launch_date_utc", "No Date")
            val launchSite = missionJson.optJSONObject("launch_site")?.optString("site_name_long", "No Launch Site") ?: "No Launch Site"
            val rocketId = missionJson.optJSONObject("rocket")?.optString("rocket_id", "") ?: ""
            val wikipedia = missionJson.optJSONObject("links")?.optString("wikipedia", "") ?: ""

            missionDetails = MissionDataModel(flightNumber, missionName, launchDate, launchSite, description, wikipedia, rocketId, null)
        }
        catch (exception: JSONException) {
            Log.e(LOG_TAG, "JSON response error", exception)
        }
        return missionDetails
    }

    // Add rocket details to the mission details object (from the second chained request)
    private fun addRocketDetails(jsonResponse: String): RocketDataModel? {
        var rocketDetails: RocketDataModel? = null

        try {
            val rocketJson = JSONObject(jsonResponse)

            val name = rocketJson.optString("rocket_name", "No Name")
            val description = rocketJson.optString("description", "No Description")
            val height = rocketJson.optJSONObject("height")?.optDouble("meters", 0.0) ?: 0.0
            val diameter = rocketJson.optJSONObject("diameter")?.optDouble("meters", 0.0) ?: 0.0
            val mass = rocketJson.optJSONObject("mass")?.optDouble("kg", 0.0) ?: 0.0
            val wikipedia = rocketJson.optString("wikipedia", "") ?: ""

            rocketDetails = RocketDataModel(name, description, height, mass, diameter, wikipedia)
        }
        catch (exception: JSONException) {
            Log.e(LOG_TAG, "JSON response error", exception)
        }
        return rocketDetails
    }


    /**
     * Callback to the launch list view model
     */
    interface LaunchesRequestListener {
        fun onGetLaunchList(launchList: List<LaunchListDataModel>)
    }

    /**
     * Callback to the mission details view model
     */
    interface MissionRequestListener {
        fun onGetMissionDetails(missionDetails: MissionDataModel?)
    }
}
