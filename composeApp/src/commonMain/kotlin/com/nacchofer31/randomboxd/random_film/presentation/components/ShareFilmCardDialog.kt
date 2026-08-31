package com.nacchofer31.randomboxd.random_film.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.random_film.domain.model.Film
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.history_clear_cancel
import randomboxd.composeapp.generated.resources.share

@Composable
fun ShareFilmCardDialog(
    film: Film,
    onShare: (ImageBitmap, String) -> Unit,
    onDismiss: () -> Unit,
) {
    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    var sharing by remember { mutableStateOf(false) }
    var posterLoaded by remember { mutableStateOf(false) }
    var capturedBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val dismissDialog = {
        sharing = false
        onDismiss()
    }

    LaunchedEffect(posterLoaded) {
        if (posterLoaded) {
            while (graphicsLayer.size.width <= 0 || graphicsLayer.size.height <= 0) {
                withFrameNanos { }
            }
            capturedBitmap = graphicsLayer.toImageBitmap()
        }
    }

    Dialog(onDismissRequest = dismissDialog) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(graphicsLayer)
                        },
                contentAlignment = Alignment.Center,
            ) {
                ShareCard(
                    film = film,
                    onPosterLoaded = { posterLoaded = true },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    onClick = dismissDialog,
                    shape = RoundedCornerShape(100),
                    color = RandomBoxdColors.ElevatedBackgroundColor,
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                            tint = RandomBoxdColors.White,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.history_clear_cancel),
                            color = RandomBoxdColors.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                Surface(
                    onClick = {
                        val bitmap = capturedBitmap
                        if (!sharing && bitmap != null) {
                            sharing = true
                            scope.launch {
                                onShare(bitmap, film.slug)
                                dismissDialog()
                            }
                        }
                    },
                    shape = RoundedCornerShape(100),
                    color = RandomBoxdColors.GreenAccent,
                    modifier =
                        Modifier
                            .weight(1f)
                            .testTag("test-share-dialog-button"),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null,
                            tint = RandomBoxdColors.BackgroundDarkColor,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.share),
                            color = RandomBoxdColors.BackgroundDarkColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
