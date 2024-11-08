package com.rohkee.welight.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rohkee.feat.storage.StorageScreen

@Composable
fun BottomTabSubNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onNavigateToDisplayDetail: (id: Long) -> Unit,
    onNavigateToCreateNewDisplay: () -> Unit,
) {
    NavHost(navController = navController, startDestination = Storage) {
        composable<Storage> {
            StorageScreen(
                onNavigateToDisplayDetail = onNavigateToDisplayDetail,
                onNavigateToCreateNewDisplay = onNavigateToCreateNewDisplay,
            )
        }
        composable<Group> {
        }
        composable<Board> {
        }
        composable<MyPage> {
        }
    }
}
