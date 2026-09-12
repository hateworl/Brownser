package com.taiji.browser.adblock

import android.content.Context
import org.mozilla.geckoview.GeckoResult
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.WebRequestError

/**
 * Bloqueador de anúncios baseado em listas de filtros (EasyList / EasyPrivacy).
 * Intercepta cada requisição de rede da GeckoSession e nega as que baterem
 * com os padrões carregados.
 *
 * As listas devem ser baixadas periodicamente (WorkManager) e cacheadas em
 * disco; aqui carregamos de um arquivo local em assets/filters/ como base inicial.
 */
class FilterListBlocker(context: Context) {

    // Conjunto de domínios/padrões compilados das listas de filtro.
    // Em produção: parsear regras completas no formato Adblock Plus (||domain^, etc.)
    private val blockedPatterns: Set<Regex> = loadDefaultFilters(context)

    fun attachTo(session: GeckoSession) {
        session.contentDelegate = object : GeckoSession.ContentDelegate {}
        session.navigationDelegate = object : GeckoSession.NavigationDelegate {
            override fun onLoadRequest(
                session: GeckoSession,
                request: GeckoSession.NavigationDelegate.LoadRequest
            ): GeckoResult<GeckoSession.NavigationDelegate.AllowOrDeny>? {
                val blocked = blockedPatterns.any { it.containsMatchIn(request.uri) }
                return if (blocked) {
                    GeckoResult.fromValue(GeckoSession.NavigationDelegate.AllowOrDeny.DENY)
                } else {
                    GeckoResult.fromValue(GeckoSession.NavigationDelegate.AllowOrDeny.ALLOW)
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
        // Conversão simplificada de sintaxe Adblock Plus (||domain^) para regex.
        val escaped = rule
            .replace("||", "")
            .replace("^", "")
            .replace(".", "\\.")
        return Regex(escaped, RegexOption.IGNORE_CASE)
    }
}
