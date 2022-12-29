package com.howard.project.ui.viewAdapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.howard.project.ui.viewHolder.ApduCommandAndResponseViewHolder

class ApduCommandAndResponseListAdapter() : RecyclerView.Adapter<ApduCommandAndResponseViewHolder>() {
    private var dataList: ArrayList<String> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApduCommandAndResponseViewHolder {
        return ApduCommandAndResponseViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ApduCommandAndResponseViewHolder, position: Int) {
        holder.setViewHolderData(position, dataList)
    }

    override fun getItemCount() = dataList.size

    fun setAdapterData(dataList: ArrayList<String>) {
        val diffResult = DiffUtil.calculateDiff(
            ApduCommandAndResponseResultCallback(
                this.dataList.toList(),
                dataList.toList()
            )
        )

        this.dataList.clear()
        this.dataList.addAll(dataList)

        diffResult.dispatchUpdatesTo(this)
    }
}

class ApduCommandAndResponseResultCallback(
    private val oldList: List<String>,
    private val newList: List<String>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition] && oldItemPosition == newItemPosition
    }
}