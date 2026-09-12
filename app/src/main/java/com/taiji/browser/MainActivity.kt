package com.taiji.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.taiji.browser.adblock.FilterListBlocker
import com.taiji.browser.browser.BrowserBar
import com.taiji.browser.integrations.AppIntegrationManager
import com.taiji.browser.ui.theme.TaijiBrowserTheme
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSessionSettings
import org.mozilla.geckoview.GeckoView

class MainActivity : ComponentActivity() {

    private lateinit var geckoSession: GeckoSession
    private lateinit var integrationManager: AppIntegrationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = TaijiApp.from(application)
        integrationManager = AppIntegrationManager(this)

        geckoSession = GeckoSession(GeckoSessionSettings.Builder().usePrivateMode(false).build())
        geckoSession.open(app.geckoRuntime)
        geckoSession.setActive(true) // <-- sem isso o GeckoView não desenha nada na tela
        FilterListBlocker(this).attachTo(geckoSession)

        setContent {
            TaijiBrowserTheme {
                var currentUrl by remember { mutableStateOf("https://www.google.com") }
                var isLoading by remember { mutableStateOf(false) }

                Column(modifier = Modifier.fillMaxSize()) {
                    BrowserBar(
                        url = currentUrl,
                        isLoading = isLoading,
                        blockedCount = 0, // TODO: contador vindo do FilterListBlocker
                        onUrlSubmit = { newUrl ->
                            currentUrl = newUrl
                            if (!integrationManager.tryOpenInNativeApp(newUrl)) {
                                geckoSession.loadUri(newUrl)
                            }
                        },
                        onShare = { integrationManager.shareUrl(currentUrl) }
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        AndroidView(
                            factory = { context ->
                                GeckoView(context).apply {
                                    setSession(geckoSession)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        geckoSession.loadUri("https://www.google.com")
    }

    override fun onResume() {
        super.onResume()
        if (::geckoSession.isInitialized) geckoSession.setActive(true)
    }

    override fun onPause() {
        if (::geckoSession.isInitialized) geckoSession.setActive(false)
        super.onPause()
    }

    override fun onDestroy() {
        geckoSession.close()
        super.onDestroy()
    }
}
