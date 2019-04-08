package com.example.logan.promdate

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class HintAdapter(context: Context, objects: Array<String>, layoutResId: Int) :
    ArrayAdapter<String>(context, layoutResId, objects) {

    override fun getCount(): Int {
        // don't display last item. It is used as hint.
        val count = super.getCount()
        return if (count > 0) count - 1 else count
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getView(position, convertView, parent)
        if (position == count) {
            (v.findViewById<View>(android.R.id.text1) as TextView).text = ""
            (v.findViewById<View>(android.R.id.text1) as TextView).hint = getItem(count) //Hint to be displayed
        }

        return v
    }
}