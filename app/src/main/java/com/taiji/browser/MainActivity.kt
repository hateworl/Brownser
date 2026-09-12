package com.taiji.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    private val errorMessage = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = TaijiApp.from(application)
        integrationManager = AppIntegrationManager(this)

        geckoSession = GeckoSession(GeckoSessionSettings.Builder().usePrivateMode(false).build())
        geckoSession.open(app.geckoRuntime)
        geckoSession.setActive(true)
        FilterListBlocker(this).attachTo(geckoSession) { msg ->
            errorMessage.value = msg
        }

        setContent {
            TaijiBrowserTheme {
                var currentUrl by remember { mutableStateOf("https://www.google.com") }
                var isLoading by remember { mutableStateOf(false) }
                val error by errorMessage

                Column(modifier = Modifier.fillMaxSize()) {
                    BrowserBar(
                        url = currentUrl,
                        isLoading = isLoading,
                        blockedCount = 0,
                        onUrlSubmit = { newUrl ->
                            currentUrl = newUrl
                            errorMessage.value = null
                            if (!integrationManager.tryOpenInNativeApp(newUrl)) {
                                geckoSession.loadUri(newUrl)
                            }
                        },
                        onShare = { integrationManager.shareUrl(currentUrl) }
                    )

                    if (error != null) {
                        Text(
                            text = error ?: "",
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Red)
                                .padding(16.dp)
                        )
                    } else {
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
