package com.taiji.browser.integrations

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object OEmbedFetcher {
    suspend fun fetch(endpoint: String): OEmbedResult? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(endpoint).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)

            OEmbedResult(
                title = json.optString("title", null),
                thumbnailUrl = json.optString("thumbnail_url", null),
                html = json.optString("html", null)
            )
        } catch (e: Exception) {
            null
        }
    }
}
