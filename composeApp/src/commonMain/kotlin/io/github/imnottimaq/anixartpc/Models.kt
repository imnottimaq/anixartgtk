package io.github.imnottimaq.anixartpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

public class Models {
    @Serializable
    data class ApiResponse<Target>(
        val code: Int,
        val content: Target? = null,
        val related: Target? = null, // TODO: Показ франшиз в поиске
        val release: Target? = null,
        val releases: Target? = null,
        val types: Target? = null,
        val episodes: Target? = null,
    )
    @Serializable
    data class Release(
        val id: Int,
        @SerialName("poster") val posterCacheName: String,
        val posterUrl: String = "$alternativeApiUrl/posters/$posterCacheName.jpg",
        val description: String? = null,
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
    @Serializable
    data class DubProviderInfo (
        val id: Int,
        val name: String,
        @SerialName("workers") val cast: String? = null,
        @SerialName("episodes_count") val episodesCount: Int,
        @SerialName("view_count") val viewCount: Int,
    )
    @Serializable
    data class EpisodeInfo (
        val name: String,
        val url: String,
        @SerialName("iframe") val isIframe: Boolean,
    )
    @Serializable
    data class SearchRequest(
        val query: String,
    )
}