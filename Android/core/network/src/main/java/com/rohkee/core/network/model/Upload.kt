package com.rohkee.core.network.model

interface Upload {
    data class InProgress(
        val progress: Float,
    ) : Upload

    data class Completed(
        val uploadedFile: String,
    ) : Upload
}