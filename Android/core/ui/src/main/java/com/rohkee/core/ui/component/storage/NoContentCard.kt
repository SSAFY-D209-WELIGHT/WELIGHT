package com.rohkee.core.ui.component.storage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.R
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Composable
fun NoContentCard(modifier: Modifier = Modifier) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(color = AppColor.Surface),
    ) {
        Column(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.no_content),
                contentDescription = null,
            )
            Text(
                "나만의 응원봉을 만들어\n" +
                    "응원해보세요!",
                style = Pretendard.SemiBold20,
                textAlign = TextAlign.Center,
                color = AppColor.OnBackground
            )
        }
    }
}

@Preview
@Composable
fun NoContentCardPreview() {
    NoContentCard()
}
