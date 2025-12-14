package com.bigotitech.rokub10000.di

import android.content.Context
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
    fun provideGson(): Gson {
        return Gson()
    }
}
