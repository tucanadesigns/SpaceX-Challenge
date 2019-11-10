package com.tucanadesigns.spacex.launchlist

/**
 * Simple value object for holding launch list entries
 */
data class LaunchListDataModel(val flightNumber: Int,
                               val missionName: String,
                               val launchDate: Long,
                               val launchYear: String,
                               val launchSuccess: Boolean
)
