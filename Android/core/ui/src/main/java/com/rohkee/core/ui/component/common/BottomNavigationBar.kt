package com.rohkee.core.ui.component.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.util.topBorder

data class BottomNavigationItemState<T>(
    val route: T,
    val label: String,
    val icon: Int,
    val selectedIcon: Int,
)

@Composable
fun <T : Any> BottomNavigationBar(
    modifier: Modifier = Modifier,
    items: List<BottomNavigationItemState<T>>,
    onSelected: (route: T) -> Unit,
    startDestination: String?,
) {
    var selectedRoute by remember(startDestination) { mutableStateOf(startDestination) }

    BottomAppBar(
        modifier = modifier.topBorder(color = AppColor.Surface, width = 2.dp),
        containerColor = AppColor.Background,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            for (item in items) {
                BottomNavigationItem(
                    isSelected = item.route::class.qualifiedName.toString() == selectedRoute,
                    label = item.label,
                    icon = item.icon,
                    selectedIcon = item.selectedIcon,
                    onClick = {
                        selectedRoute = item.route::class.qualifiedName.toString()
                        onSelected(item.route)
                    },
                )
            }
        }
    }
}

@Composable
fun BottomNavigationItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    label: String,
    icon: Int,
    selectedIcon: Int,
    onClick: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .width(80.dp)
                .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            tint = if (isSelected) AppColor.Active else AppColor.Inactive,
            painter = painterResource(id = if (isSelected) selectedIcon else icon),
            contentDescription = "홈",
        )
        Text(
            text = label,
            color = if (isSelected) AppColor.Active else AppColor.Inactive,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
