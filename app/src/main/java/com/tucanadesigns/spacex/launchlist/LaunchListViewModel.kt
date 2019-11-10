package com.tucanadesigns.spacex.launchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tucanadesigns.spacex.repository.Repository
import com.tucanadesigns.spacex.repository.Repository.LaunchesRequestListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Lifecycle-aware view model for the launch list UI fragment
 */
class LaunchListViewModel : ViewModel(), LaunchesRequestListener {
    private var groupBy: GroupBy = GroupBy.YEAR
    private var showOnlySuccessful: Boolean = false
    private var launchListDetails: MutableLiveData<HashMap<String, List<LaunchListDataModel>>> = MutableLiveData()

    /**
     * Set the grouping and make a request on the repository for the full launch list
     */
    fun getLaunchList(groupBy: GroupBy) {
        this.groupBy = groupBy
        Repository.requestLaunchList(this)
    }

    /**
     * Set the success filter and make a request on the repository for the full launch list
     */
    fun filterBySuccess(showOnlySuccessful: Boolean) {
        this.showOnlySuccessful = showOnlySuccessful
        Repository.requestLaunchList(this)
    }

    /**
     * Observable data structure for the launch list fragment
     */
    fun launchList(): LiveData<HashMap<String, List<LaunchListDataModel>>> {
        return launchListDetails
    }

    /**
     * Call back from the repository with launch list details.
     * Applies logic to filter by success and group the launch records
     */
    override fun onGetLaunchList(launchList: List<LaunchListDataModel>) {
        var filteredLaunchList = ArrayList<LaunchListDataModel>()

        // Filter only successful launches
        if (showOnlySuccessful) {
            for (launch in launchList) {
                if (launch.launchSuccess) {
                    filteredLaunchList.add(launch)
                }
            }
        }
        else {
            filteredLaunchList = launchList as ArrayList<LaunchListDataModel>
        }

        // Group by year or mission name
        when (groupBy) {
            GroupBy.ALPHABETICALLY -> groupByName(filteredLaunchList)
            GroupBy.YEAR -> groupByYear(filteredLaunchList)
        }
    }

    // Group the hash map by mission name
    private fun groupByName(launchList: List<LaunchListDataModel>) {
        val launchMap = HashMap<String, List<LaunchListDataModel>>()

        val unGroupedAlphabet = Array(launchList.size) { "" }

        for (i in launchList.indices) {
            unGroupedAlphabet[i] = launchList[i].missionName.take(1).toUpperCase(Locale.US)
        }

        val distinctAlphabet = unGroupedAlphabet.distinct()

        for (firstLetter in distinctAlphabet) {
            val alphabeticGroup = ArrayList<LaunchListDataModel>()
            for (launch in launchList) {
                if (launch.missionName.take(1).toUpperCase(Locale.US) == firstLetter) {
                    alphabeticGroup.add(launch)
                }
            }
            launchMap[firstLetter] = alphabeticGroup
        }

        // Update the value of the observable
        launchListDetails.value = launchMap
    }

    // Group the hash map by launch year
    private fun groupByYear(launchList: List<LaunchListDataModel>) {
        val launchMap = HashMap<String, List<LaunchListDataModel>>()

        val unGroupedYears = Array(launchList.size) { "" }

        for (i in launchList.indices) {
            unGroupedYears[i] = launchList[i].launchYear
        }

        val distinctYears = unGroupedYears.distinct()

        for (year in distinctYears) {
            val launchesInThatYear = ArrayList<LaunchListDataModel>()
            for (launch in launchList) {
                if (launch.launchYear == year) {
                    launchesInThatYear.add(launch)
                }
            }
            launchMap[year] = launchesInThatYear
        }

        // Update the value of the observable
        launchListDetails.value = launchMap
    }
}
