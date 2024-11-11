package com.rohkee.welight.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rohkee.feature.detail.DetailRoute
import com.rohkee.feature.detail.DetailScreen
import com.rohkee.feature.editor.EditorScreen
import com.rohkee.feature.editor.navigation.EditorRoute

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Home,
    ) {
        composable<Login> {
        }

        composable<Home> {
            BottomTabSubNavigation(
                onNavigateToDisplayDetail = { id -> navController.navigate(DetailRoute(displayId = id)) },
                onNavigateToCreateNewDisplay = { navController.navigate(EditorRoute(null)) },
            )
        }

        composable<DetailRoute> {
            val currentId = it.toRoute<DetailRoute>().displayId
            DetailScreen(
                onPopBackStack = { navController.popBackStack() },
                onEditDisplay = { id ->
                    navController.navigate(EditorRoute(displayId = id)) {
                        popUpTo(DetailRoute(currentId)) { inclusive = true }
                    }
                },
                onDuplicateDisplay = { id ->
                    navController.navigate(DetailRoute(displayId = id)) {
                        popUpTo(DetailRoute(currentId)) { inclusive = true }
                    }
                },
                onDownloadDisplay = { id ->
                    navController.navigate(DetailRoute(displayId = id)) {
                        popUpTo(DetailRoute(currentId)) { inclusive = true }
                    }
                },
            )
        }

        composable<EditorRoute> {
            EditorScreen(
                onNavigateToDisplayDetail = { id ->
                    navController.navigate(DetailRoute(displayId = id)) {
                        popUpTo(Home) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onPopBackStack = { navController.popBackStack() },
            )
        }
    }
}
