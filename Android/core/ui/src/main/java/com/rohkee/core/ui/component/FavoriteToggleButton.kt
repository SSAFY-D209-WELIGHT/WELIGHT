package com.rohkee.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.R
import com.rohkee.core.ui.theme.AppColor

@Composable
fun FavoriteToggleButton(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (selected: Boolean) -> Unit = {},
) {
    val outlined = painterResource(R.drawable.favorite_star)
    val filled = painterResource(R.drawable.favorite_star_filled)
    val icon =
        remember(selected) { if (selected) filled else outlined }

    Box(
        modifier =
            modifier
                .background(color = AppColor.SurfaceTransparent, shape = CircleShape)
                .size(32.dp)
                .clickable { onClick(!selected) },
    ) {
        Icon(
            modifier = Modifier.size(28.dp).align(Alignment.Center),
            painter = icon,
            contentDescription = null,
            tint = AppColor.OnBackground,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoriteToggleButtonPreview() {
    FavoriteToggleButton(
        selected = false,
    )
}

@Preview(showBackground = true)
@Composable
private fun FavoriteToggleButtonSelectedPreview() {
    FavoriteToggleButton(
        selected = true,
    )
}
