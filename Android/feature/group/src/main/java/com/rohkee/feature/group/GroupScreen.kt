package com.rohkee.feature.group

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohkee.core.ui.util.collectWithLifecycle

@Composable
fun GroupScreen(
    modifier: Modifier = Modifier,
    groupViewModel: GroupViewModel = hiltViewModel(),
    onPopBackStack: () -> Unit = {},
    onNavigateToGroupRoom: (Long) -> Unit = {},
    onNavigateToCreateGroupRoom: () -> Unit = {},
) {
    val groupUIState by groupViewModel.groupState.collectAsStateWithLifecycle()

    groupViewModel.groupEvent.collectWithLifecycle { event ->
        when (event) {
            is GroupEvent.OpenClient -> onNavigateToGroupRoom(event.roomId)
            GroupEvent.OpenRoomCreation -> onNavigateToCreateGroupRoom()
        }
    }

    GroupContent(
        modifier = modifier,
        state = groupUIState,
        onIntent = groupViewModel::onIntent,
    )
}
