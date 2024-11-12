package com.rohkee.feature.group

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rohkee.core.ui.component.appbar.TitleAppBar
import com.rohkee.core.ui.component.group.CardButton
import com.rohkee.core.ui.component.group.CardListItem
import com.rohkee.core.ui.component.group.CardListItemState
import com.rohkee.core.ui.component.group.LoadingListItem
import com.rohkee.core.ui.theme.AppColor
import com.rohkee.core.ui.theme.Pretendard
import kotlinx.collections.immutable.persistentListOf

@Composable
fun GroupContent(
    modifier: Modifier = Modifier,
    state: GroupState,
    onIntent: (GroupIntent) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = { TitleAppBar(title = "단체 응원") },
        containerColor = AppColor.Background,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CardButton(
                icon = painterResource(R.drawable.ic_group_create),
                title = "응원 생성",
                description = "응원을 함께 할 사람들을 모집해보세요",
                onClick = { onIntent(GroupIntent.CreateGroup) },
            )
            Text(
                text = "응원 참가",
                style = Pretendard.SemiBold20,
                color = AppColor.OnBackground,
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                when (state) {
                    is GroupState.Loading -> {
                        items(3) {
                            LoadingListItem()
                        }
                    }

                    is GroupState.Loaded -> {
                        items(state.cardList) {
                            CardListItem(
                                state = it,
                                onJoinClick = { id -> onIntent(GroupIntent.GroupJoin(id)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun GroupContentPreview() {
    GroupContent(state = GroupState.Loading)
}

@Preview
@Composable
private fun GroupContentLoadedPreview() {
    GroupContent(
        state =
            GroupState.Loaded(
                persistentListOf(
                    CardListItemState(1, "title", "description", 10),
                    CardListItemState(2, "title", "description", 10),
                    CardListItemState(3, "title", "description", 10),
                ),
            ),
    )
}
