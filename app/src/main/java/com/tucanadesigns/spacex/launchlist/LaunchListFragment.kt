package com.tucanadesigns.spacex.launchlist

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tucanadesigns.spacex.AbstractBaseFragment
import com.tucanadesigns.spacex.R
import kotlinx.android.synthetic.main.fragment_launch_list.*

/**
 * UI fragment to display the list of mission launches
 */
class LaunchListFragment : AbstractBaseFragment() {

    private lateinit var viewModel: LaunchListViewModel
    private var savedListState: Parcelable? = null
    private var groupBy = GroupBy.YEAR


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        // Bind to view model
        viewModel = ViewModelProviders.of(this).get(LaunchListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_launch_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe data changes on the launch list
        viewModel.launchList().observe(viewLifecycleOwner, Observer { launchList ->
            initLaunchListView(launchList)
            fragmentListener.hideProgressBar()
        })

        // Request a grouped hash map from the view model
        fragmentListener.showProgressBar()
        viewModel.getLaunchList(groupBy)
    }

    // Update the UI with the latest launch list details
    private fun initLaunchListView(launchList: HashMap<String, List<LaunchListDataModel>>) {
        val context = context as Context
        val groupHeadings = ArrayList(launchList.keys)
        groupHeadings.sort()

        // Bind the list to the adapter and reinstate any saved list position
        launchListView.setAdapter(LaunchListAdapter(context, groupHeadings, launchList))
        if (savedListState != null) {
            launchListView.onRestoreInstanceState(savedListState)
        }

        // Handle list item selections and call back to parent activity to navigate to the details screen
        launchListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->

            // Save the list position for when we are popped off the back stack
            savedListState = launchListView.onSaveInstanceState()
            fragmentListener.onLaunchSelected(launchList[groupHeadings[groupPosition]]!![childPosition])
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu, menu)

        // Shaw all flights by default
        menu.findItem(R.id.menu_successful).isChecked = false
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Update the grouping and filtering of the expandable list view
        when (item.itemId) {
            R.id.menu_mission -> {
                groupBy = GroupBy.ALPHABETICALLY
                viewModel.getLaunchList(groupBy)
            }
            R.id.menu_year -> {
                groupBy = GroupBy.YEAR
                viewModel.getLaunchList(groupBy)
            }
            R.id.menu_successful -> {
                item.isChecked = !item.isChecked
                viewModel.filterBySuccess(item.isChecked)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
