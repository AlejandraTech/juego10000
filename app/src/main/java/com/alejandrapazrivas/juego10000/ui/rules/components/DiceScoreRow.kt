package com.alejandrapazrivas.juego10000.ui.rules.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandrapazrivas.juego10000.R
import com.alejandrapazrivas.juego10000.ui.common.theme.LocalDimensions
import com.alejandrapazrivas.juego10000.ui.common.theme.Primary
import com.alejandrapazrivas.juego10000.ui.common.util.getDiceDrawable
import com.alejandrapazrivas.juego10000.ui.rules.components.base.ScoreRow

@Composable
fun DiceScoreRow(diceValue: Int, points: Int) {
    val dimensions = LocalDimensions.current

    ScoreRow(
        points = points,
        leadingContent = {
            DiceScoreLeadingContent(
                diceValue = diceValue,
                dimensions = dimensions
            )
        }
    )
}

@Composable
private fun DiceScoreLeadingContent(
    diceValue: Int,
    dimensions: com.alejandrapazrivas.juego10000.ui.common.theme.Dimensions
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = getDiceDrawable(diceValue)),
                contentDescription = stringResource(R.string.dice_description, diceValue),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(dimensions.spaceSmall))

        Text(
            text = stringResource(R.string.dice_value, diceValue),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
