package com.rohkee.welight.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rohkee.core.ui.component.common.CommonSnackbar
import com.rohkee.feat.login.LoginRoute
import com.rohkee.feature.detail.DetailRoute
import com.rohkee.feature.detail.DetailScreen
import com.rohkee.feature.editor.EditorScreen
import com.rohkee.feature.editor.navigation.EditorRoute
import com.rohkee.feature.group.client.ClientRoute
import com.rohkee.feature.group.client.ClientScreen
import com.rohkee.feature.group.host.HostRoute
import com.rohkee.feature.group.host.HostScreen
import kotlinx.coroutines.launch

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun showSnackbar(message: String) {
        scope.launch { snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short) }
    }

    Scaffold(
        snackbarHost = { CommonSnackbar(snackbarHostState = snackbarHostState) },
    ) { innerPadding ->
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = Login,
        ) {
            composable<Login> {
                LoginRoute(
                    onLoginSuccess = {
                        navController.navigate(Home) {
                            popUpTo(Login) { inclusive = true }
                        }
                    },
                    onShowSnackbar = { showSnackbar(it) },
                )
            }

            composable<Home> {
                BottomTabSubNavigation(
                    onNavigateToDisplayDetail = { id -> navController.navigate(DetailRoute(displayId = id)) },
                    onNavigateToCreateNewDisplay = { navController.navigate(EditorRoute(null)) },
                    onNavigateToGroupRoom = { id -> navController.navigate(ClientRoute(id)) },
                    onNavigateToCreateGroupRoom = { navController.navigate(HostRoute) },
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
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                    onPopBackStack = { navController.popBackStack() },
                    onShowSnackBar = { showSnackbar(it) },
                )
            }

            composable<HostRoute> {
                HostScreen(
                    showSnackbar = { showSnackbar(it) },
                    onPopBackStack = { navController.popBackStack() },
                )
            }

            composable<ClientRoute> {
                ClientScreen(
                    onPopBackStack = { navController.popBackStack() },
                )
            }
        }
    }
}
