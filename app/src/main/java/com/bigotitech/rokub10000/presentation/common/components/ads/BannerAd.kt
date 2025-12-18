package com.bigotitech.rokub10000.presentation.common.components.ads

import android.util.Log
import android.view.View
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bigotitech.rokub10000.core.ads.AdConstants
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * Composable que muestra un banner de AdMob.
 * Solo muestra el espacio cuando el anuncio está cargado.
 *
 * @param adUnitId ID de la unidad de anuncio de AdMob
 * @param modifier Modificador para personalizar el layout
 */
@Composable
fun BannerAd(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    var isAdLoaded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .then(
                if (isAdLoaded) {
                    Modifier.height(AdConstants.BANNER_HEIGHT_DP.dp)
                } else {
                    Modifier.height(0.dp)
                }
            )
    ) {
        AndroidView(
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    setAdUnitId(adUnitId)

                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            Log.d("BannerAd", "Ad loaded successfully")
                            visibility = View.VISIBLE
                            isAdLoaded = true
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e("BannerAd", "Ad failed to load: ${error.message}")
                            visibility = View.GONE
                            isAdLoaded = false
                        }

                        override fun onAdClicked() {
                            Log.d("BannerAd", "Ad clicked")
                        }
                    }

                    loadAd(AdRequest.Builder().build())
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
