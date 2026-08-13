package com.nacchofer31.randomboxd.history.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.history_footer_count

@Composable
fun HistoryFooter(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(Res.string.history_footer_count, count),
        color = RandomBoxdColors.TextMuted,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 20.dp),
    )
}
