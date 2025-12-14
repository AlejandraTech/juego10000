package com.bigotitech.rokub10000.ads

import com.bigotitech.rokub10000.BuildConfig

/**
 * Constantes para los IDs de anuncios de AdMob.
 */
object AdConstants {

    // Banner Ad Unit ID (se lee desde local.properties via BuildConfig)
    @JvmField val BANNER_HOME: String = BuildConfig.ADMOB_BANNER_ID
    @JvmField val BANNER_GAME: String = BuildConfig.ADMOB_BANNER_ID
    @JvmField val BANNER_PLAYERS: String = BuildConfig.ADMOB_BANNER_ID
    @JvmField val BANNER_RULES: String = BuildConfig.ADMOB_BANNER_ID
    @JvmField val BANNER_SETTINGS: String = BuildConfig.ADMOB_BANNER_ID
    @JvmField val BANNER_STATS: String = BuildConfig.ADMOB_BANNER_ID

    // Interstitial Ad Unit ID (se lee desde local.properties via BuildConfig)
    @JvmField val INTERSTITIAL_VICTORY: String = BuildConfig.ADMOB_INTERSTITIAL_ID

    // Tamaño del banner estándar: 320x50 dp
    const val BANNER_HEIGHT_DP = 50

    // Privacy Policy URL
    const val PRIVACY_POLICY_URL = "https://bigotitech.github.io/juego10000-privacy/"
}
