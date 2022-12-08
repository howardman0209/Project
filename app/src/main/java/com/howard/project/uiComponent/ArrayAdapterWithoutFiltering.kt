package com.howard.project.uiComponent

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Filter
import android.widget.TextView
import com.howard.project.R


class ArrayAdapterWithoutFiltering<T>(
    context: Context, var data: List<T>,
    private val itemClickListener: OnDeleteClickListener, private val itemClickListener02: OnSelectClickListener
) : ArrayAdapter<T>(context, 0, data) {

    interface OnDeleteClickListener {
        fun itemDelete(position: Int)
    }

    interface OnSelectClickListener {
        fun itemSelect(position: Int)
    }

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return FilterResults()
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_dropdown_menu_item_with_image, parent, false)
        }

        val tvEmail = view?.findViewById<TextView>(R.id.tvEmail)
        tvEmail?.text = data[position].toString()
        tvEmail?.setOnClickListener {
            Log.d("ArrayAdapterWithoutFiltering", "position: $position")
            itemClickListener02.itemSelect(position)
        }

        val deleteButton = view?.findViewById<Button>(R.id.btnDelete)
        deleteButton?.setOnClickListener {
            itemClickListener.itemDelete(position)
            val removeList = data.toMutableList()
            removeList.removeAt(position)
            remove(data[position])
            data = removeList
        }

        return view!!
    }
}