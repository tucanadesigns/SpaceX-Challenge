package com.tucanadesigns.spacex.missiondetails

/**
 * Simple value object for holding rocket details
 */
data class RocketDataModel(val rocketName: String,
                           val rocketDescription: String,
                           val rocketHeight: Double,
                           val rocketMass: Double,
                           val rocketDiameter: Double,
                           val wikipediaLink: String
)
