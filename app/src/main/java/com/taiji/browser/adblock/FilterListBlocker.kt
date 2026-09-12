package com.taiji.browser.adblock

import android.content.Context
import android.util.Log
import org.mozilla.geckoview.AllowOrDeny
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.WebRequestError

/**
 * Bloqueador de anúncios baseado em listas de filtros (EasyList / EasyPrivacy).
 * Também expõe erros de carregamento de página via callback, para diagnóstico.
 */
class FilterListBlocker(context: Context) {

    private val blockedPatterns: Set<Regex> = loadDefaultFilters(context)

    fun attachTo(session: GeckoSession, onPageError: ((String) -> Unit)? = null) {
        session.contentDelegate = object : GeckoSession.ContentDelegate {
            override fun onCrash(session: GeckoSession) {
                val msg = "O motor do navegador travou (crash na GeckoSession)."
                Log.e("TaijiBrowser", msg)
                onPageError?.invoke(msg)
            }
        }

        session.navigationDelegate = object : GeckoSession.NavigationDelegate {
            override fun onLoadRequest(
                session: GeckoSession,
                request: GeckoSession.NavigationDelegate.LoadRequest
            ): GeckoResult<AllowOrDeny>? {
                val blocked = blockedPatterns.any { it.containsMatchIn(request.uri) }
                return if (blocked) {
                    GeckoResult.fromValue(AllowOrDeny.DENY)
                } else {
                    GeckoResult.fromValue(AllowOrDeny.ALLOW)
                }
            }

            override fun onLoadError(
                session: GeckoSession,
                uri: String?,
                error: WebRequestError
            ): GeckoResult<String>? {
                val msg = "Erro ao carregar $uri — categoria=${error.category}, código=${error.code}"
                Log.e("TaijiBrowser", msg)
                onPageError?.invoke(msg)
                return GeckoResult.fromValue(null)
            }
        }

        session.progressDelegate = object : GeckoSession.ProgressDelegate {
            override fun onPageStart(session: GeckoSession, url: String) {
                Log.d("TaijiBrowser", "onPageStart: $url")
            }

            override fun onPageStop(session: GeckoSession, success: Boolean) {
                Log.d("TaijiBrowser", "onPageStop: success=$success")
                if (!success) {
                    onPageError?.invoke("A página parou de carregar sem sucesso (onPageStop success=false).")
                }
            }
        }
    }

    private fun loadDefaultFilters(context: Context): Set<Regex> {
        return try {
            context.assets.open("filters/easylist-lite.txt")
                .bufferedReader()
                .lineSequence()
                .filter { it.isNotBlank() && !it.startsWith("!") }
                .map { rulePatternToRegex(it) }
                .toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    private fun rulePatternToRegex(rule: String): Regex {
        val escaped = rule
            .replace("||", "")
            .replace("^", "")
            .replace(".", "\\.")
        return Regex(escaped, RegexOption.IGNORE_CASE)
    }
}
