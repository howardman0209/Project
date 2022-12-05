package com.howard.project.ui.viewModel

import androidx.databinding.ObservableField
import com.howard.project.ui.base.BaseViewModel

class FirstViewModel : BaseViewModel() {
    val text: ObservableField<String> = ObservableField()
}