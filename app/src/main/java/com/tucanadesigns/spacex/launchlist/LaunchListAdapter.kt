package com.tucanadesigns.spacex.launchlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import androidx.appcompat.widget.AppCompatTextView
import com.tucanadesigns.spacex.R
import kotlinx.android.synthetic.main.list_child_layout.view.*
import kotlinx.android.synthetic.main.list_group_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Expandable list adapter to display the grouped list of launches
 */
class LaunchListAdapter(context: Context, private val groupList: List<String>,
                        private val launchList: HashMap<String, List<LaunchListDataModel>>) : BaseExpandableListAdapter() {

    private val layoutInflater = LayoutInflater.from(context)

    // Show just the launch date
    private val formatter: SimpleDateFormat = SimpleDateFormat("d MMM, yyyy", Locale.US)

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return launchList[groupList[groupPosition]]!![childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    // Launch summaries
    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        val childViewHolder: ChildViewHolder
        val childView: View

        if (convertView == null) {
            childView = layoutInflater.inflate(R.layout.list_child_layout, parent, false)
            childViewHolder = ChildViewHolder(childView)
            childView.tag = childViewHolder
        }
        else {
            childView = convertView
            childViewHolder = childView.tag as ChildViewHolder
        }

        val launchData = getChild(groupPosition, childPosition) as LaunchListDataModel
        childViewHolder.missionName.text = launchData.missionName
        childViewHolder.missionDate.text = formatter.format(Date(launchData.launchDate * 1000L))
        return childView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return this.launchList[this.groupList[groupPosition]]!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return this.groupList[groupPosition]
    }

    override fun getGroupCount(): Int {
        return this.groupList.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    // Group headings
    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        val groupViewHolder: GroupViewHolder
        val groupView: View

        if (convertView == null) {
            groupView = layoutInflater.inflate(R.layout.list_group_layout, parent, false)
            groupViewHolder = GroupViewHolder(groupView)
            groupView.tag = groupViewHolder
        }
        else {
            groupView = convertView
            groupViewHolder = groupView.tag as GroupViewHolder
        }

        groupViewHolder.groupHeading.text = getGroup(groupPosition) as String
        groupViewHolder.launchCount.text = "(%d)".format(getChildrenCount(groupPosition))
        return groupView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }


    // View holder for child layout
    private class ChildViewHolder(view: View) {
        val missionName = view.missionName as AppCompatTextView
        val missionDate = view.missionDate as AppCompatTextView
    }


    // View holder for group layout
    private class GroupViewHolder(view: View) {
        val groupHeading = view.groupHeading as AppCompatTextView
        val launchCount = view.launchCount as AppCompatTextView
    }
}
