package com.bigotitech.rokub10000.di

import android.content.Context
import com.bigotitech.rokub10000.core.audio.AudioService
import com.bigotitech.rokub10000.core.audio.BackgroundMusicManager
import com.bigotitech.rokub10000.core.vibration.VibrationService
import com.bigotitech.rokub10000.data.preferences.UserPreferencesManager
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideUserPreferencesManager(
        @ApplicationContext context: Context
    ): UserPreferencesManager {
        return UserPreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideBackgroundMusicManager(
        @ApplicationContext context: Context,
        userPreferencesManager: UserPreferencesManager
    ): BackgroundMusicManager {
        return BackgroundMusicManager(context, userPreferencesManager)
    }

    @Provides
    @Singleton
    fun provideAudioService(
        @ApplicationContext context: Context,
        userPreferencesManager: UserPreferencesManager
    ): AudioService {
        return AudioService(context, userPreferencesManager)
    }

    @Provides
    @Singleton
    fun provideVibrationService(
        @ApplicationContext context: Context,
        userPreferencesManager: UserPreferencesManager
    ): VibrationService {
        return VibrationService(context, userPreferencesManager)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
}
