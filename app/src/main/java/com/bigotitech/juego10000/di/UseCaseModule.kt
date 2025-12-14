package com.bigotitech.juego10000.di

import com.bigotitech.juego10000.domain.repository.GameRepository
import com.bigotitech.juego10000.domain.repository.PlayerRepository
import com.bigotitech.juego10000.domain.repository.ScoreRepository
import com.bigotitech.juego10000.domain.usecase.*
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // Casos de uso relacionados con la mecánica del juego
    @Provides
    fun provideCalculateScoreUseCase(): CalculateScoreUseCase = CalculateScoreUseCase()

    @Provides
    fun provideRollDiceUseCase(): RollDiceUseCase = RollDiceUseCase()

    @Provides
    fun provideValidateTurnUseCase(
        calculateScoreUseCase: CalculateScoreUseCase
    ): ValidateTurnUseCase = ValidateTurnUseCase(calculateScoreUseCase)

    @Provides
    fun provideBotTurnHandler(
        calculateScoreUseCase: CalculateScoreUseCase,
        validateTurnUseCase: ValidateTurnUseCase,
        rollDiceUseCase: RollDiceUseCase
    ): BotTurnHandler = BotTurnHandler(calculateScoreUseCase, validateTurnUseCase, rollDiceUseCase)

    // Casos de uso relacionados con la gestión de partidas
    @Provides
    fun provideCreateGameUseCase(
        gameRepository: GameRepository,
        playerRepository: PlayerRepository
    ): CreateGameUseCase = CreateGameUseCase(gameRepository, playerRepository)

    @Provides
    fun provideSaveGameUseCase(
        gameRepository: GameRepository,
        playerRepository: PlayerRepository,
        gson: Gson
    ): SaveGameUseCase = SaveGameUseCase(gameRepository, playerRepository, gson)

    @Provides
    fun provideGameStateUseCase(
        gameRepository: GameRepository,
        playerRepository: PlayerRepository,
        scoreRepository: ScoreRepository,
        gson: Gson
    ): GameStateUseCase = GameStateUseCase(gameRepository, playerRepository, scoreRepository, gson)

    @Provides
    fun provideGetGameHistoryUseCase(
        gameRepository: GameRepository,
        playerRepository: PlayerRepository
    ): GetGameHistoryUseCase = GetGameHistoryUseCase(gameRepository, playerRepository)

    // Casos de uso relacionados con la gestión de jugadores
    @Provides
    fun provideGetPlayersUseCase(
        playerRepository: PlayerRepository
    ): GetPlayersUseCase = GetPlayersUseCase(playerRepository)

    @Provides
    fun provideManagePlayersUseCase(
        playerRepository: PlayerRepository
    ): ManagePlayersUseCase = ManagePlayersUseCase(playerRepository)
}
