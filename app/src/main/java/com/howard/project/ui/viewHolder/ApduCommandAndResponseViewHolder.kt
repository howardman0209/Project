package com.howard.project.ui.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.howard.project.databinding.ViewHolderApduCommandAndResponseBinding

class ApduCommandAndResponseViewHolder(private val binding: ViewHolderApduCommandAndResponseBinding) : RecyclerView.ViewHolder(binding.root) {


    fun setViewHolderData(index: Int, data: List<String>) {
        binding.tvApduCommandAndResponse.text = data[index]
    }

    companion object {
        fun from(parent: ViewGroup): ApduCommandAndResponseViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ViewHolderApduCommandAndResponseBinding.inflate(layoutInflater, parent, false)
            return ApduCommandAndResponseViewHolder(binding)
        }
    }
}