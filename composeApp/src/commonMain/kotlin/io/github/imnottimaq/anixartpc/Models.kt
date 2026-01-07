package io.github.imnottimaq.anixartpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

public class Models {
    @Serializable
    data class ApiResponse<Target>(
        val code: Int,
        val content: Target? = null,
        val release: Target? = null,
    )
    @Serializable
    data class Release(
        val id: Int,
        @SerialName("poster") val posterCacheName: String,
        val year: String? = null,
        val grade: Float? = null,
        @SerialName("title_ru") val title: String,
    )
    @Serializable
    data class ReleaseDetailed (
        val id: Int,
        val country: String? = null,
        val year: String? = null,
        val duration: Int? = null,
        val source: String? = null,
        val studio: String? = null,
        val author: String? = null,
        val description: String? = null,
        @SerialName("broadcast") val releaseDay: Int? = null,
        @SerialName("title_ru") val title: String? = null,
        @SerialName("title_original") val titleOriginal: String? = null,
        @SerialName("poster") val posterCacheName: String,
        @SerialName("episodes_released") val episodesReleased: Int? = null,
        @SerialName("episodes_total") val episodesTotal: Int? = null,
        )
}