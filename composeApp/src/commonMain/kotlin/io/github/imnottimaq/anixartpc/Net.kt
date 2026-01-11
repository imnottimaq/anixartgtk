package io.github.imnottimaq.anixartpc

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.contentType
import me.sujanpoudel.utils.paths.appCacheDirectory
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.collections.emptyList
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val apiUrl = "https://api-s.anixsekai.com"
val alternativeApiUrl = "https://mirror-s.anixmirai.com"
val cacheDir = appCacheDirectory("io.github.imnottimaq.anixartpc")
private val json = Json { ignoreUnknownKeys = true }

class Net {
    suspend fun getLatestReleases(client: HttpClient): Result<List<Models.Release>> {
        return runCatching {
            val text = client.get(apiUrl + "/filter/0") {
                accept(ContentType.Application.Json)
            }.bodyAsText()
            val resp = json.decodeFromString<Models.ApiResponse<List<Models.Release>>>(text)
            resp.content ?: emptyList()
        }
    }
    suspend fun getDetailedReleaseInfo(client: HttpClient, releaseId: Int): Result<Models.ReleaseDetailed?> {
        return runCatching {
            val text = client.get("$apiUrl/release/$releaseId") {
                accept(ContentType.Application.Json)
            }.bodyAsText()
            val resp = json.decodeFromString<Models.ApiResponse<Models.ReleaseDetailed>>(text)
            resp.release
        }
    }
    suspend fun getDubProvidersForEpisode(client: HttpClient, episodeId: Int): Result<List<Models.DubProviderInfo>> {
        return runCatching {
            val text = client.get("$apiUrl/episode/$episodeId") {
                accept(ContentType.Application.Json)
            }.bodyAsText()
            val resp = json.decodeFromString<Models.ApiResponse<List<Models.DubProviderInfo>>>(text)
            resp.types ?: emptyList()
        }
    }
    suspend fun getSearchResults(client: HttpClient, searchQuery: String): Result<List<Models.Release>>{
        return runCatching {
            val text = client.post("$apiUrl/search/releases/0") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(json.encodeToString(Models.SearchRequest(searchQuery)))
            }.bodyAsText()
            val resp = json.decodeFromString<Models.ApiResponse<List<Models.Release>>>(text)
            resp.releases ?: resp.content ?: emptyList()
        }
    }
}
