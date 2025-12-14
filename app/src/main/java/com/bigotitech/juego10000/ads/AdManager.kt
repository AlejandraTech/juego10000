package com.bigotitech.juego10000.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor centralizado de anuncios de AdMob.
 * Maneja la inicialización del SDK y la carga/visualización de interstitials.
 */
@Singleton
class AdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    companion object {
        private const val TAG = "AdManager"
    }

    /**
     * Inicializa el SDK de Google Mobile Ads.
     * Debe llamarse una vez al inicio de la aplicación.
     */
    fun initialize() {
        MobileAds.initialize(context) { initializationStatus ->
            Log.d(TAG, "AdMob initialized: $initializationStatus")
            loadInterstitial()
        }
    }

    /**
     * Carga un anuncio interstitial.
     * Se llama automáticamente después de la inicialización y después de mostrar un anuncio.
     */
    fun loadInterstitial() {
        if (isLoading || interstitialAd != null) {
            return
        }

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            AdConstants.INTERSTITIAL_VICTORY,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded")
                    interstitialAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                    interstitialAd = null
                    isLoading = false
                }
            }
        )
    }

    /**
     * Muestra el anuncio interstitial si está cargado.
     *
     * @param activity La actividad desde la cual mostrar el anuncio
     * @param onAdDismissed Callback que se ejecuta cuando el anuncio se cierra
     */
    fun showInterstitial(activity: Activity, onAdDismissed: () -> Unit) {
        val ad = interstitialAd

        if (ad == null) {
            Log.d(TAG, "Interstitial ad not ready, proceeding without ad")
            onAdDismissed()
            loadInterstitial()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial ad dismissed")
                interstitialAd = null
                loadInterstitial()
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                interstitialAd = null
                loadInterstitial()
                onAdDismissed()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Interstitial ad showed")
            }
        }

        ad.show(activity)
    }

    /**
     * Verifica si hay un anuncio interstitial listo para mostrar.
     */
    fun isInterstitialReady(): Boolean = interstitialAd != null
}
