package com.howard.project.ui.viewModel

import androidx.lifecycle.MutableLiveData
import com.howard.project.ui.base.BaseViewModel

class ApduViewModel : BaseViewModel() {
    val apduCommandList: MutableList<String> = mutableListOf()
    val cardResponse: MutableLiveData<String> = MutableLiveData()
}