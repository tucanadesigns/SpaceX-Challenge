package com.tucanadesigns.spacex

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.tucanadesigns.spacex.launchlist.LaunchListDataModel
import com.tucanadesigns.spacex.launchlist.LaunchListFragment
import com.tucanadesigns.spacex.missiondetails.MissionDetailsFragment
import kotlinx.android.synthetic.main.activity_main.*

// Extension function for the fragment manager to reduce boilerplate code
private inline fun FragmentManager.doTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

// Utility to add a fragment to a frame
private fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.doTransaction {
        add(frameId, fragment)
    }
}

// Utility to replace a fragment and save transaction to the back stack
private fun AppCompatActivity.replaceToBackStack(fragment: Fragment, frameId: Int) {
    supportFragmentManager.doTransaction {
        replace(frameId, fragment)
        addToBackStack(fragment.javaClass.simpleName)
    }
}

/**
 * Application entry point
 * Hosts fragments and facilitates inter-fragment communications
 */
class MainActivity : AppCompatActivity(), AbstractBaseFragment.FragmentListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load the launch list fragment on initial start
        // Let the fragment manager handle configuration changes
        if (savedInstanceState == null) {
            addFragment(LaunchListFragment(), R.id.fragmentHolder)
        }
    }

    // Launch mission details fragment, with selected flight number
    override fun onLaunchSelected(launchList: LaunchListDataModel) {
        replaceToBackStack(MissionDetailsFragment.newInstance(launchList.flightNumber), R.id.fragmentHolder)
    }

    // Hide progress after network requests have completed
    override fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
    }

    // Show progress during long running network requests
    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }
}
