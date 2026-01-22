package io.github.imnottimaq.anixartpc.Net

import io.github.imnottimaq.anixartpc.Models
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.util.decodeBase64Bytes
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.sujanpoudel.utils.paths.appCacheDirectory
import kotlin.collections.iterator
import kotlin.text.iterator

private val json = Json { ignoreUnknownKeys = true }

val apiUrl = "https://api-s.anixsekai.com"
val alternativeApiUrl = "https://mirror-s.anixmirai.com"
/*
typealias KodikQuality = String
typealias KodikVideoLinks = Map<KodikQuality, List<KodikVideoSource>>

@Serializable
data class KodikVideoSource(
    var src: String,
    val type: String
)

@Serializable
data class KodikVast(
    val title_small: String,
    val src: String,
    val timer: Int? = null,
    val hide_interface: Boolean? = null,
    val async_load: Boolean? = null,
    val vpaid_target_event: String? = null,
    val vpaid_max_load_time: Int? = null,
    val vpaid_max_start_time: Int? = null,
    val vpaid_start_event: String? = null,
    val vpaid_timer_start_event: String? = null,
    val vpaid_ad_skippable_state: Boolean? = null,
    val advert_id: String? = null,
    val save_views: Boolean? = null,
    val start_muted: Boolean? = null,
    val max_length: Int? = null,
    val disable_advert_click: Int? = null,
    val send_stat_method: String? = null,
    val stop_timer_on_pause: Boolean? = null
)

@Serializable
data class KodikDirectLinkResponse(
    val advert_script: String,
    val domain: String,
    val default: Int,
    val links: KodikVideoLinks,
    val vast: List<KodikVast>,
    val reserve_vast: List<KodikVast>,
    val ip: String
)

object KodikParser {
    private const val baseKodikDomain = "kodik.info"
    private const val endpointUrl = "/ftor"

    private val endpointUrlRegex = Regex("url:atob\\(\"(?<encodedPath>[^\"]+)\"\\)", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val appPlayerPathRegex = Regex("src=\"(?<path>/assets/js/app\\.player_single\\..*?\\.js)\">", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val urlParamsRegex = Regex("var\\surlParams\\s=\\s'(?<params>.*?)';", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val videoInfoHashRegex = Regex("videoInfo\\.hash\\s=\\s'(?<hash>.*?)';", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val videoInfoIdRegex = Regex("videoInfo\\.id\\s=\\s'(?<id>.*?)';", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
    private val videoInfoTypeRegex = Regex("videoInfo\\.type\\s=\\s'(?<type>.*?)';", RegexOption.IGNORE_CASE)
    private val validKodikUrl = Regex("//(get|cloud)\\.kodik-storage\\.com/useruploads/.+?/.+?/(240|360|480|720|1080)\\.mp4:hls:manifest\\.m3u8")

    suspend fun getLatestLink(client: HttpClient, url: String): String? {
        val playerResponse = client.get(url).bodyAsText()
        val appPlayerPath = appPlayerPathRegex.find(playerResponse)?.groups?.get("path")?.value ?: return null

        val host = Url(url).host
        val appPlayerResponse = client.get("https://$host$appPlayerPath").bodyAsText()
        val latestEndpoint = endpointUrlRegex.find(appPlayerResponse)?.groups?.get("encodedPath")?.value ?: return null

        return decodeBase64ToString(latestEndpoint)
    }

    suspend fun getDirectLinks(client: HttpClient, url: String, endpointPath: String = endpointUrl): KodikVideoLinks? {
        val urlResponse = client.get(url).bodyAsText()

        val urlParamsRaw = urlParamsRegex.find(urlResponse)?.groups?.get("params")?.value ?: "{}"
        val urlParams = parseUrlParams(urlParamsRaw)
        val videoInfoHash = videoInfoHashRegex.find(urlResponse)?.groups?.get("hash")?.value
        val videoInfoId = videoInfoIdRegex.find(urlResponse)?.groups?.get("id")?.value
        val videoInfoType = videoInfoTypeRegex.find(urlResponse)?.groups?.get("type")?.value

        if (videoInfoHash == null || videoInfoId == null || videoInfoType == null) return null

        val requestParams = urlParams.toMutableMap().apply {
            put("type", videoInfoType)
            put("hash", videoInfoHash)
            put("id", videoInfoId)
        }

        val directLinksResponse = client.get(buildEndpointUrl(endpointPath, requestParams))
        val contentType = directLinksResponse.headers["Content-Type"] ?: return null
        if (!contentType.startsWith(ContentType.Application.Json.toString())) return null

        val directLinks = json.decodeFromString<KodikDirectLinkResponse>(directLinksResponse.bodyAsText())

        for ((_, sources) in directLinks.links) {
            for (source in sources) {
                if (validKodikUrl.containsMatchIn(source.src)) continue
                val decryptedBase64 = rotateBy18(source.src)
                source.src = decodeBase64ToString(decryptedBase64)
            }
        }

        return directLinks.links
    }

    private fun parseUrlParams(raw: String): Map<String, String> {
        val jsonObject = json.decodeFromString<JsonObject>(raw)
        return jsonObject.mapValues { it.value.jsonPrimitive.content }
    }

    private fun buildEndpointUrl(endpointPath: String, params: Map<String, String>): String {
        val builder = URLBuilder(protocol = URLProtocol.HTTPS, host = baseKodikDomain, encodedPath = endpointPath)
        params.forEach { (key, value) -> builder.parameters.append(key, value) }
        return builder.buildString()
    }

    private fun rotateBy18(value: String): String {
        val baseUpper = 'A'.code
        val baseLower = 'a'.code
        return buildString(value.length) {
            for (ch in value) {
                when {
                    ch in 'A'..'Z' -> {
                        val rotated = ((ch.code - baseUpper + 18) % 26) + baseUpper
                        append(rotated.toChar())
                    }
                    ch in 'a'..'z' -> {
                        val rotated = ((ch.code - baseLower + 18) % 26) + baseLower
                        append(rotated.toChar())
                    }
                    else -> append(ch)
                }
            }
        }
    }

    private fun decodeBase64ToString(value: String): String =
        value.decodeBase64Bytes().decodeToString()
}
*/
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

    suspend fun getSearchResults(client: HttpClient, searchQuery: String): Result<List<Models.Release>> {
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
