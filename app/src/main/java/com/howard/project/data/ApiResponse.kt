package com.howard.project.data

data class ApiResponse<DATA> (
    /**
     * Response code
     */
    val code: String? = null,

    /**
     * Response message
     */
    val message: String? = null,

    /**
     * Response data
     */
    val data: DATA? = null,
)