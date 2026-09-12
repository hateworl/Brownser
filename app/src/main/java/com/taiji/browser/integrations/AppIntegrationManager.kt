package com.taiji.browser.integrations

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

/**
 * Centraliza as integrações do navegador com apps externos (Spotify, Instagram, etc).
 * Três mecanismos, do mais simples ao mais rico:
 *  1. Deep link  - abre o link direto no app instalado
 *  2. Share      - manda a URL atual para o app via Share Sheet
 *  3. Preview    - embute uma prévia (oEmbed) sem sair do navegador
 */
class AppIntegrationManager(private val context: Context) {

    private val knownApps = mapOf(
        "open.spotify.com" to "com.spotify.music",
        "spotify.com" to "com.spotify.music",
        "instagram.com" to "com.instagram.android",
        "www.instagram.com" to "com.instagram.android"
    )

    /** Tenta abrir a URL direto no app nativo instalado; retorna false se não instalado. */
    fun tryOpenInNativeApp(url: String): Boolean {
        val host = Uri.parse(url).host ?: return false
        val packageName = knownApps[host] ?: return false

        if (!isPackageInstalled(packageName)) return false

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            setPackage(packageName)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Compartilha a URL/conteúdo atual do navegador via Android Share Sheet. */
    fun shareUrl(url: String, title: String? = null) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
            title?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
        }
        context.startActivity(Intent.createChooser(intent, null))
    }

    /**
     * Busca dados de preview (oEmbed) para posts públicos do Instagram ou faixas do Spotify,
     * para exibir um mini-card/mini-player dentro do próprio navegador sem trocar de app.
     *
     * Spotify: usa o endpoint público oEmbed (https://open.spotify.com/oembed?url=...)
     * Instagram: oEmbed requer app registrado na Meta (Graph API) - ver README para setup.
     */
    suspend fun fetchOEmbedPreview(url: String): OEmbedResult? {
        val host = Uri.parse(url).host ?: return null
        val endpoint = when {
            host.contains("spotify.com") -> "https://open.spotify.com/oembed?url=$url"
            host.contains("instagram.com") -> null // requer token de app - ver README
            else -> null
        } ?: return null

        return OEmbedFetcher.fetch(endpoint)
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}

data class OEmbedResult(
    val title: String?,
    val thumbnailUrl: String?,
    val html: String?
)
