package com.taiji.browser

import android.app.Application
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings

class TaijiApp : Application() {

    lateinit var geckoRuntime: GeckoRuntime
        private set

    override fun onCreate() {
        super.onCreate()

        val settings = GeckoRuntimeSettings.Builder()
            // Content blocking nativo do GeckoView (base do bloqueio de anúncios/trackers)
            .contentBlocking(
                org.mozilla.geckoview.ContentBlocking.Settings.Builder()
                    .antiTracking(org.mozilla.geckoview.ContentBlocking.AntiTracking.STRICT)
                    .cookieBehavior(org.mozilla.geckoview.ContentBlocking.CookieBehavior.ACCEPT_NON_TRACKERS)
                    .safeBrowsing(org.mozilla.geckoview.ContentBlocking.SafeBrowsing.DEFAULT)
                    .build()
            )
            .build()

        geckoRuntime = GeckoRuntime.create(this, settings)
    }

    companion object {
        fun from(app: Application): TaijiApp = app as TaijiApp
    }
}
