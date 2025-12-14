package com.bigotitech.rokub10000.ui.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bigotitech.rokub10000.R
import com.bigotitech.rokub10000.ui.common.theme.ButtonShape
import com.bigotitech.rokub10000.ui.common.theme.LocalDimensions
import com.bigotitech.rokub10000.ui.common.theme.Primary
import com.bigotitech.rokub10000.ui.common.theme.Secondary

/**
 * Fila con los botones de acción del juego: Lanzar dados y Guardar/Pasar.
 */
@Composable
fun GameActionButtons(
    canRoll: Boolean,
    canBank: Boolean,
    isGameOver: Boolean,
    scoreExceeded: Boolean,
    minimumPointsNeeded: Boolean,
    onRollClick: () -> Unit,
    onBankClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    val buttonColor = when {
        isGameOver -> MaterialTheme.colorScheme.tertiary
        scoreExceeded -> MaterialTheme.colorScheme.error
        else -> Secondary
    }

    val buttonText = when {
        isGameOver -> stringResource(R.string.end_game)
        scoreExceeded -> stringResource(R.string.pass_turn)
        minimumPointsNeeded -> stringResource(R.string.minimum_points_required)
        else -> stringResource(R.string.bank_score)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensions.spaceSmall),
        horizontalArrangement = Arrangement.spacedBy(dimensions.spaceSmall)
    ) {
        RollDiceButton(
            canRoll = canRoll,
            onClick = onRollClick,
            modifier = Modifier.weight(1f)
        )

        BankScoreButton(
            canBank = canBank,
            buttonColor = buttonColor,
            buttonText = buttonText,
            onClick = onBankClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RollDiceButton(
    canRoll: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Button(
        onClick = onClick,
        modifier = modifier
            .height(dimensions.buttonHeight)
            .shadow(
                elevation = if (canRoll) 8.dp else 2.dp,
                shape = ButtonShape,
                spotColor = Primary.copy(alpha = 0.4f)
            ),
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            disabledContainerColor = Primary.copy(alpha = 0.3f)
        ),
        enabled = canRoll
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_dice),
                contentDescription = null,
                modifier = Modifier.size(dimensions.iconSizeSmall),
                tint = Color.White
            )
            Spacer(modifier = Modifier.size(dimensions.spaceSmall))
            Text(
                text = stringResource(R.string.roll_dice),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun BankScoreButton(
    canBank: Boolean,
    buttonColor: Color,
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Button(
        onClick = onClick,
        modifier = modifier
            .height(dimensions.buttonHeight)
            .shadow(
                elevation = if (canBank) 8.dp else 2.dp,
                shape = ButtonShape,
                spotColor = buttonColor.copy(alpha = 0.4f)
            ),
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            disabledContainerColor = buttonColor.copy(alpha = 0.3f)
        ),
        enabled = canBank
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(dimensions.iconSizeSmall),
                tint = Color.White
            )
            Spacer(modifier = Modifier.size(dimensions.spaceSmall))
            Text(
                text = buttonText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}
