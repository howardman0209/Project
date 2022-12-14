package com.howard.project.data

import java.io.Serializable

sealed class FcmRedirectData: Serializable {
    enum class NotificationType {
        TEST,
        UNKNOWN
    }

    data class TestData(
        val testString: String? = null,
    ): FcmRedirectData()
}