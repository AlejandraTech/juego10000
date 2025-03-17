package com.alejandrapazrivas.juego10000.di

import com.alejandrapazrivas.juego10000.data.local.dao.GameDao
import com.alejandrapazrivas.juego10000.data.local.dao.PlayerDao
import com.alejandrapazrivas.juego10000.data.local.dao.ScoreDao
import com.alejandrapazrivas.juego10000.data.repository.GameRepositoryImpl
import com.alejandrapazrivas.juego10000.data.repository.PlayerRepositoryImpl
import com.alejandrapazrivas.juego10000.data.repository.ScoreRepositoryImpl
import com.alejandrapazrivas.juego10000.domain.repository.GameRepository
import com.alejandrapazrivas.juego10000.domain.repository.PlayerRepository
import com.alejandrapazrivas.juego10000.domain.repository.ScoreRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGameRepository(
        gameRepositoryImpl: GameRepositoryImpl
    ): GameRepository

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(
        playerRepositoryImpl: PlayerRepositoryImpl
    ): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindScoreRepository(
        scoreRepositoryImpl: ScoreRepositoryImpl
    ): ScoreRepository

    companion object {
        @Provides
        @Singleton
        fun provideGameRepositoryImpl(
            gameDao: GameDao,
            playerDao: PlayerDao,
            scoreDao: ScoreDao
        ): GameRepositoryImpl {
            return GameRepositoryImpl(gameDao, playerDao, scoreDao)
        }

        @Provides
        @Singleton
        fun providePlayerRepositoryImpl(
            playerDao: PlayerDao,
            scoreDao: ScoreDao
        ): PlayerRepositoryImpl {
            return PlayerRepositoryImpl(playerDao, scoreDao)
        }

        @Provides
        @Singleton
        fun provideScoreRepositoryImpl(
            scoreDao: ScoreDao
        ): ScoreRepositoryImpl {
            return ScoreRepositoryImpl(scoreDao)
        }
    }
}
