package com.tucanadesigns.spacex.missiondetails

/**
 * Simple value object for holding mission details (including rocket details)
 */
data class MissionDataModel(val flightNumber: Int,
                            val missionName: String,
                            val launchDate: String,
                            val launchSite: String,
                            val missionDetails: String,
                            val wikipediaLink: String,
                            val rocketId: String,
                            var rocketDetails: RocketDataModel?
)
