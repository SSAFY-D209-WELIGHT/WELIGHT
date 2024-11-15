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
import com.rohkee.feat.mypage.MypageRoute
import com.rohkee.feat.mypage.MypageScreen
import com.rohkee.feature.board.BoardRoute
import com.rohkee.feature.board.BoardScreen
import com.rohkee.feature.group.GroupRoute
import com.rohkee.feature.group.GroupScreen
import com.rohkee.feature.storage.StorageRoute
import com.rohkee.feature.storage.StorageScreen
import com.rohkee.welight.R

@Composable
fun BottomTabSubNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onNavigateToDisplayDetail: (id: Long) -> Unit,
    onNavigateToCreateNewDisplay: () -> Unit,
    onNavigateToGroupRoom: (id: Long) -> Unit,
    onNavigateToCreateGroupRoom: () -> Unit,
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
                        route = BoardRoute,
                        label = "게시판",
                        icon = painterResource(R.drawable.ic_board_unselected),
                        selectedIcon = painterResource(R.drawable.ic_board_selected),
                    ),
//                    BottomNavigationItemState(
//                        route = Unit,
//                        label = "게시판",
//                        icon = painterResource(R.drawable.ic_board_unselected),
//                        selectedIcon = painterResource(R.drawable.ic_board_selected),
//                    ),
                    BottomNavigationItemState(
                        route = MypageRoute,
                        label = "마이페이지",
                        icon = painterResource(R.drawable.ic_mypage_selected),
                        selectedIcon = painterResource(R.drawable.ic_mypage_unselected),
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

            composable<GroupRoute> {
                GroupScreen(
                    onPopBackStack = {},
                    onNavigateToGroupRoom = onNavigateToGroupRoom,
                    onNavigateToCreateGroupRoom = onNavigateToCreateGroupRoom,
                )
            }

            composable<BoardRoute> {
                BoardScreen(
                    onNavigateToDisplayDetail = onNavigateToDisplayDetail,
                )
            }

            composable<MypageRoute> {
                MypageScreen(
                    onNavigateToDisplayDetail = onNavigateToDisplayDetail,
                )
            }
        }
    }
}
