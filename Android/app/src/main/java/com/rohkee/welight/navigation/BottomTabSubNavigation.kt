package com.rohkee.welight.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rohkee.core.ui.component.common.BottomNavigationBar
import com.rohkee.core.ui.component.common.BottomNavigationItemState
import com.rohkee.feature.group.GroupRoute
import com.rohkee.feature.storage.StorageRoute
import com.rohkee.feature.storage.StorageScreen
import com.rohkee.welight.R

@Composable
fun BottomTabSubNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onNavigateToDisplayDetail: (id: Long) -> Unit,
    onNavigateToCreateNewDisplay: () -> Unit,
) {
    val bottomRoute by navController.currentBackStackEntryAsState()

    Scaffold(modifier = modifier, bottomBar = {
        BottomNavigationBar(
            startDestination = bottomRoute?.destination?.route,
            items =
                listOf(
                    BottomNavigationItemState(
                        route = StorageRoute,
                        label = "보관함",
                        icon = painterResource(R.drawable.ic_storage_unselected),
                        selectedIcon = painterResource(R.drawable.ic_storage_selected),
                    ),
                    BottomNavigationItemState(
                        route = GroupRoute,
                        label = "단체 응원",
                        icon = painterResource(R.drawable.ic_group_unselected),
                        selectedIcon = painterResource(R.drawable.ic_group_selected),
                    ),
                    BottomNavigationItemState(
                        route = "board",
                        label = "게시판",
                        icon = painterResource(R.drawable.ic_board_unselected),
                        selectedIcon = painterResource(R.drawable.ic_board_selected),
                    ),
                ),
            onSelected = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
        )
    }) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = StorageRoute,
        ) {
            composable<StorageRoute> {
                StorageScreen(
                    onNavigateToDisplayDetail = onNavigateToDisplayDetail,
                    onNavigateToCreateNewDisplay = onNavigateToCreateNewDisplay,
                )
            }

            composable<GroupRoute> { }
//            composable<BoardRoute> {
        //      BoardScreen(
        //      onNavigateToDisplayDetail = onNavigateToDisplayDetail, // 다른화면이동용.. 제목다르게
//                    onNavigateToCreateNewDisplay = onNavigateToCreateNewDisplay, // 구멍뚫기
        //      )
        //            }
        }
    }
}
