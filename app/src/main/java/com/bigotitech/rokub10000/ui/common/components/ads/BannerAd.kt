package com.bigotitech.rokub10000.ui.common.components.ads

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bigotitech.rokub10000.ads.AdConstants
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * Composable que muestra un banner de AdMob.
 *
 * @param adUnitId ID de la unidad de anuncio de AdMob
 * @param modifier Modificador para personalizar el layout
 */
@Composable
fun BannerAd(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                setAdUnitId(adUnitId)

                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d("BannerAd", "Ad loaded successfully")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.e("BannerAd", "Ad failed to load: ${error.message}")
                    }

                    override fun onAdClicked() {
                        Log.d("BannerAd", "Ad clicked")
                    }
                }

                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(AdConstants.BANNER_HEIGHT_DP.dp)
    )
}
