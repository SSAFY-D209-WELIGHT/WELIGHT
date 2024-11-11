package com.rohkee.welight.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rohkee.core.ui.component.common.BottomNavigationBar
import com.rohkee.core.ui.component.common.BottomNavigationItemState
import com.rohkee.feature.storage.Storage
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
                        route = Storage,
                        label = "보관함",
                        icon = R.drawable.ic_storage_unselected,
                        selectedIcon = R.drawable.ic_storage_selected,
                    ),
                    BottomNavigationItemState(
                        route = "group",
                        label = "단체 응원",
                        icon = R.drawable.ic_group_unselected,
                        selectedIcon = R.drawable.ic_group_selected,
                    ),
                    BottomNavigationItemState(
                        route = "board",
                        label = "게시판",
                        icon = R.drawable.ic_board_unselected,
                        selectedIcon = R.drawable.ic_board_selected,
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
        NavHost(navController = navController, startDestination = Storage) {
            composable<Storage> {
                StorageScreen(
                    onNavigateToDisplayDetail = onNavigateToDisplayDetail,
                    onNavigateToCreateNewDisplay = onNavigateToCreateNewDisplay,
                )
            }
        }
    }
}
