/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rohkee.feature.board.data

import com.rohkee.feat.board.R
import com.rohkee.feature.board.model.Board

/**
 * [BoardDataSource] generates a list of [Board]
 */
class BoardDataSource() {
    fun loadBoards(): List<Board> {
        return listOf<Board>(
            Board(R.string.board1, R.drawable.image1),
            Board(R.string.board2, R.drawable.image2),
            Board(R.string.board3, R.drawable.image3),
            Board(R.string.board4, R.drawable.image4),
            Board(R.string.board5, R.drawable.image5),
            Board(R.string.board6, R.drawable.image6),
            Board(R.string.board7, R.drawable.image7),
            Board(R.string.board8, R.drawable.image8),
            Board(R.string.board9, R.drawable.image9),
            Board(R.string.board10, R.drawable.image10))
    }
}