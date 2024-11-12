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
    onOpenRoom: (Long) -> Unit = {},
    onRoomCreation: () -> Unit = {},
) {
    val groupUIState by groupViewModel.groupState.collectAsStateWithLifecycle()

    groupViewModel.groupEvent.collectWithLifecycle { event ->
        when (event) {
            is GroupEvent.OpenClient -> onOpenRoom(event.roomId)
            GroupEvent.OpenRoomCreation -> onRoomCreation()
        }
    }

    GroupContent(
        modifier = modifier,
        state = groupUIState,
        onIntent = groupViewModel::onIntent,
    )
}
