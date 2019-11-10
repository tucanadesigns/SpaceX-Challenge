package com.tucanadesigns.spacex


import android.content.Context
import androidx.fragment.app.Fragment
import com.tucanadesigns.spacex.launchlist.LaunchListDataModel

/**
 * Base class for the UI fragments. Wires up a callback listener to the parent activity
 */
abstract class AbstractBaseFragment : Fragment() {

    lateinit var fragmentListener: FragmentListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentListener) {
            fragmentListener = context
        }
        else {
            throw ClassCastException("$context must implement FragmentListener")
        }
    }

    /**
     * Callbacks from the fragments to their parent activity
     */
    interface FragmentListener {
        fun onLaunchSelected(launchList: LaunchListDataModel)
        fun showProgressBar()
        fun hideProgressBar()
    }
}
