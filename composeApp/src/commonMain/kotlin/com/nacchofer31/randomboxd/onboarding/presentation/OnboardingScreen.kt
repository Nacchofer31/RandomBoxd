package com.nacchofer31.randomboxd.onboarding.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nacchofer31.randomboxd.core.presentation.RandomBoxdColors
import com.nacchofer31.randomboxd.onboarding.presentation.components.OnboardingMultiUserPage
import com.nacchofer31.randomboxd.onboarding.presentation.components.OnboardingPageIndicator
import com.nacchofer31.randomboxd.onboarding.presentation.components.OnboardingRandomSelectionPage
import com.nacchofer31.randomboxd.onboarding.presentation.components.OnboardingSearchTagsPage
import com.nacchofer31.randomboxd.onboarding.presentation.components.OnboardingWelcomePage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import randomboxd.composeapp.generated.resources.Res
import randomboxd.composeapp.generated.resources.onboarding_get_started
import randomboxd.composeapp.generated.resources.onboarding_next
import randomboxd.composeapp.generated.resources.onboarding_skip
import randomboxd.composeapp.generated.resources.onboarding_start_exploring

private const val PAGE_COUNT = 4

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { PAGE_COUNT })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(RandomBoxdColors.BackgroundDarkColor),
    ) {
        // Skip button
        if (pagerState.currentPage < PAGE_COUNT - 1) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                TextButton(onClick = onFinish) {
                    Text(
                        text = stringResource(Res.string.onboarding_skip),
                        color = RandomBoxdColors.BackgroundLightColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier =
                        Modifier
                            .widthIn(max = 480.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp)
                            .padding(top = 40.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    when (page) {
                        0 -> OnboardingWelcomePage()
                        1 -> OnboardingRandomSelectionPage()
                        2 -> OnboardingSearchTagsPage()
                        3 -> OnboardingMultiUserPage()
                    }
                }
            }
        }

        // Bottom section: indicator + button
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(vertical = 12.dp)
                    .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OnboardingPageIndicator(
                pageCount = PAGE_COUNT,
                currentPage = pagerState.currentPage,
            )

            val buttonText =
                when (pagerState.currentPage) {
                    0 -> stringResource(Res.string.onboarding_get_started)
                    PAGE_COUNT - 1 -> stringResource(Res.string.onboarding_start_exploring)
                    else -> stringResource(Res.string.onboarding_next)
                }
            val buttonIcon: @Composable () -> Unit =
                when (pagerState.currentPage) {
                    PAGE_COUNT - 1 -> {
                        {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = RandomBoxdColors.BackgroundDarkColor,
                            )
                        }
                    }

                    else -> {
                        {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = RandomBoxdColors.BackgroundDarkColor,
                            )
                        }
                    }
                }

            Button(
                onClick = {
                    if (pagerState.currentPage < PAGE_COUNT - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onFinish()
                    }
                },
                modifier =
                    Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = RandomBoxdColors.GreenAccent,
                    ),
            ) {
                Text(
                    text = buttonText,
                    color = RandomBoxdColors.BackgroundDarkColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(8.dp))
                buttonIcon()
            }
        }
    }
}
