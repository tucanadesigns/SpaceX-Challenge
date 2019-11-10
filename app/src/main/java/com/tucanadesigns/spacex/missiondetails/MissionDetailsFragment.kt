package com.tucanadesigns.spacex.missiondetails


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tucanadesigns.spacex.AbstractBaseFragment
import com.tucanadesigns.spacex.R
import kotlinx.android.synthetic.main.fragment_mission_details.*
import java.text.DecimalFormat


private const val ARG_FLIGHT_NUMBER = "flight_number"

/**
 * UI fragment to display mission details
 */
class MissionDetailsFragment : AbstractBaseFragment() {
    private lateinit var viewModel: MissionDetailsViewModel
    private var flightNumber: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Flight number is passed to fragment through a static factory method
        arguments?.let {
            flightNumber = it.getInt(ARG_FLIGHT_NUMBER)
        }

        // Bind to view model
        viewModel = ViewModelProviders.of(this).get(MissionDetailsViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mission_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe data changes for mission details
        viewModel.missionDetails().observe(viewLifecycleOwner, Observer { missionDetails ->
            initMissionDetailsView(missionDetails)
            fragmentListener.hideProgressBar()
        })

        // Make request on view model for mission details
        fragmentListener.showProgressBar()
        viewModel.getMissionDetails(flightNumber ?: 1)
    }

    // Update the UI with latest mission details
    private fun initMissionDetailsView(missionDetails: MissionDataModel?) {
        if (missionDetails == null) {
            flightNumberView.text = getString(R.string.mission_details_error)
            return
        }

        flightNumberView.text = getString(R.string.details_flight_number).format(missionDetails.flightNumber)
        missionNameView.text = getString(R.string.details_mission_name).format(missionDetails.missionName)
        launchDateView.text = getString(R.string.details_launch_date).format(missionDetails.launchDate)
        launchSiteView.text = getString(R.string.details_launch_site).format(missionDetails.launchSite)
        missionDescriptionView.text = getString(R.string.details_mission_desc).format(missionDetails.missionDetails)
        rocketNameView.text = getString(R.string.details_rocket_name).format(missionDetails.rocketDetails?.rocketName)
        rocketHeightView.text = getString(R.string.details_rocket_height).format(missionDetails.rocketDetails?.rocketHeight)
        rocketMassView.text = getString(R.string.details_rocket_mass).format(numberFormatter(missionDetails.rocketDetails?.rocketMass))
        rocketDiameterView.text = getString(R.string.details_rocket_diameter).format(missionDetails.rocketDetails?.rocketDiameter)
        rocketDescriptionView.text = getString(R.string.details_rocket_desc).format(missionDetails.rocketDetails?.rocketDescription)

        missionWikiLinkButton.setOnClickListener {
            browseLink(missionDetails.wikipediaLink)
        }

        rocketWikiLinkButton.setOnClickListener {
            browseLink(missionDetails.rocketDetails?.wikipediaLink)
        }
    }

    // Utility method to provide basic formatting of large numbers
    private fun numberFormatter(number: Double?): String {
        return DecimalFormat("#,###.#").format(number ?: 0.0)
    }

    // Utility method to launch a browser intent with a web link
    private fun browseLink(link: String?) {
        if (link == null || link.isBlank()) {
            return
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(link)
        startActivity(intent)
    }

    // Factory method to create the fragment, persisting arguments across configuration changes
    companion object {
        @JvmStatic
        fun newInstance(flightNumber: Int) = MissionDetailsFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_FLIGHT_NUMBER, flightNumber)
            }
        }
    }
}
