package io.github.imnottimaq.anixartpc

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.readBytes
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.sujanpoudel.utils.paths.appCacheDirectory
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

val apiUrl = "https://api-s.anixsekai.com"
val alternativeApiUrl = "https://mirror-s.anixmirai.com"
val cacheDir = appCacheDirectory("io.github.imnottimaq.anixartpc")

class Net {
    suspend fun getLatestReleases(client: HttpClient): Result<List<Models.Release>> {
        return runCatching {
            val resp: Models.ApiResponse<List<Models.Release>> = client.get(apiUrl + "/filter/0"){
                accept(ContentType.Application.Json)
            }.body()
            resp.content ?: emptyList()
        }
    }
    suspend fun getDetailedReleaseInfo(client: HttpClient, releaseId: Int): Result<Models.ReleaseDetailed?> {
        return runCatching {
            val resp: Models.ApiResponse<Models.ReleaseDetailed> = client.get("$apiUrl/release/$releaseId"){
                accept(ContentType.Application.Json)
            }.body()
            resp.release
        }
    }
    suspend fun getPosterImage(client: HttpClient, posterId: String): String{
        val bytes = client.get("$alternativeApiUrl/poster/$posterId.jpg").readRawBytes()
        FileSystem.SYSTEM.write("$cacheDir/$posterId".toPath()) {
            write(bytes)
        }
        return "$cacheDir/$posterId"
    }
}