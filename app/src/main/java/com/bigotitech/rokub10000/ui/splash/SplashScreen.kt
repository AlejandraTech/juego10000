package com.bigotitech.rokub10000.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ui.common.components.backgrounds.AnimatedBackground
import com.bigotitech.rokub10000.ui.common.components.backgrounds.BackgroundConfig
import com.bigotitech.rokub10000.ui.common.components.loading.LoadingIndicator
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.common.theme.Primary
import kotlinx.coroutines.delay

private const val LOGO_ANIMATION_DURATION = 600
private const val FADE_ANIMATION_DURATION = 400
private const val SPLASH_DISPLAY_DURATION = 800L

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val dimensions = LocalDimensions.current

    val logoScale = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(LOGO_ANIMATION_DURATION, easing = FastOutSlowInEasing)
        )
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(FADE_ANIMATION_DURATION)
        )
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(FADE_ANIMATION_DURATION)
        )
        delay(SPLASH_DISPLAY_DURATION)
        onSplashFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedBackground(config = BackgroundConfig.splashConfig)

        SplashContent(
            logoScale = logoScale.value,
            logoAlpha = logoAlpha.value,
            textAlpha = textAlpha.value
        )
    }
}

@Composable
private fun SplashContent(
    logoScale: Float,
    logoAlpha: Float,
    textAlpha: Float
) {
    val dimensions = LocalDimensions.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LogoBox(
            scale = logoScale,
            alpha = logoAlpha,
            size = dimensions.avatarSizeLarge + dimensions.spaceLarge
        )

        Spacer(modifier = Modifier.height(dimensions.spaceLarge))

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Primary,
            modifier = Modifier.alpha(textAlpha)
        )

        Spacer(modifier = Modifier.height(dimensions.spaceSmall))

        LoadingIndicator(modifier = Modifier.alpha(textAlpha))
    }
}

@Composable
private fun LogoBox(
    scale: Float,
    alpha: Float,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .alpha(alpha)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Primary,
                        Primary.copy(alpha = 0.8f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(size)
        )
    }
}
