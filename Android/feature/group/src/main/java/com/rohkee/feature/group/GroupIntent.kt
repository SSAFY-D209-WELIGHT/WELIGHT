package com.rohkee.feature.group

sealed interface GroupIntent{
    data object CreateGroup : GroupIntent

    data object LoadGroupList : GroupIntent

    data class GroupJoin(
        val id: Long,
    ) : GroupIntent
}