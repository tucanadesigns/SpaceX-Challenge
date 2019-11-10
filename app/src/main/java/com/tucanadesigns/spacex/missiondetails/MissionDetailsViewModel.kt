package com.tucanadesigns.spacex.missiondetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tucanadesigns.spacex.repository.Repository
import com.tucanadesigns.spacex.repository.Repository.MissionRequestListener

/**
 * Lifecycle-aware view model for the mission details UI fragment
 */
class MissionDetailsViewModel : ViewModel(), MissionRequestListener {
    private var missionDetails: MutableLiveData<MissionDataModel> = MutableLiveData()

    /**
     * Make a request on the repository to retrieve mission details for the provided flight number
     */
    fun getMissionDetails(flightNumber: Int) {
        Repository.requestMissionDetails(this, flightNumber)
    }

    /**
     * Observable data structure for the mission details fragment
     */
    fun missionDetails(): LiveData<MissionDataModel> {
        return missionDetails
    }

    /**
     * Call back from the repository with mission details.
     * Sets the value of the observable
     */
    override fun onGetMissionDetails(missionDetails: MissionDataModel?) {
        this.missionDetails.value = missionDetails
    }
}
