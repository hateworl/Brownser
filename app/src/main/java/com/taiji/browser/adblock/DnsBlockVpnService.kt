package com.taiji.browser.adblock

import android.content.Context
import android.net.VpnService
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * VpnService 100% local: não envia tráfego para nenhum servidor remoto.
 * Atua como um "DNS sinkhole" — resolve domínios de anúncios/tracking conhecidos
 * para 0.0.0.0, deixando o resto do tráfego passar normalmente pelo túnel.
 *
 * Baseado no mesmo princípio usado por DNS66 / Blokada (open source).
 * Lista de domínios: formato "hosts" estilo StevenBlack.
 */
class DnsBlockVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private val blockedHosts = mutableSetOf<String>()

    override fun onCreate() {
        super.onCreate()
        loadHostsList()
    }

    override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
        startVpn()
        return START_STICKY
    }

    private fun loadHostsList() {
        try {
            assets.open("filters/hosts-lite.txt").bufferedReader().useLines { lines ->
                lines.filter { it.isNotBlank() && !it.startsWith("#") }
                    .forEach { line ->
                        val parts = line.trim().split(Regex("\\s+"))
                        if (parts.size >= 2) blockedHosts.add(parts[1])
                    }
            }
        } catch (e: Exception) {
            // Lista ausente — segue sem bloqueio de DNS até o download inicial completar
        }
    }

    private fun startVpn() {
        val builder = Builder()
            .setSession("Taiji Browser - Bloqueio de Anúncios")
            .addAddress("10.111.222.1", 24)
            .addDnsServer("10.111.222.2") // resolver local, ver DnsResolverThread
            .addRoute("10.111.222.2", 32)
            .setMtu(1500)

        vpnInterface = builder.establish()

        CoroutineScope(Dispatchers.IO).launch {
            // TODO: loop de leitura/escrita do ParcelFileDescriptor implementando
            // um resolver DNS mínimo que consulta blockedHosts antes de repassar
            // a consulta para o DNS real do sistema (ex: 1.1.1.1) via socket protegido
            // com protect() para evitar loop de roteamento.
        }
    }

    override fun onDestroy() {
        vpnInterface?.close()
        super.onDestroy()
    }

    companion object {
        fun start(context: Context) {
            val intent = android.content.Intent(context, DnsBlockVpnService::class.java)
            context.startService(intent)
        }

        fun prepareAndStart(context: Context): android.content.Intent? {
            val prepareIntent = prepare(context)
            if (prepareIntent == null) {
                start(context)
            }
            return prepareIntent
        }
    }
}
