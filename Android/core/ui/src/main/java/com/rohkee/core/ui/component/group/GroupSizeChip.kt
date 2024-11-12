package com.rohkee.core.ui.component.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.R
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard

@Composable
fun GroupSizeChip(
    modifier: Modifier = Modifier,
    number: Int,
) {
    Row(
        modifier =
            modifier
                .background(color = AppColor.Convex, shape = CircleShape)
                .padding(vertical = 2.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_group_filled),
            contentDescription = "icon",
            tint = AppColor.OnConvex,
        )
        Text(text = "${number}명", style = Pretendard.Medium12, color = AppColor.OnConvex)
    }
}

@Preview
@Composable
fun GroupSizeChipPreview() {
    GroupSizeChip(modifier = Modifier.width(60.dp), number = 3)
}
