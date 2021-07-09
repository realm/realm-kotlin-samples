/*
 * Copyright 2021 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.sample.bookshelf.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.realm.sample.bookshelf.android.R
import io.realm.sample.bookshelf.android.theme.Typography
import io.realm.sample.bookshelf.android.theme.horizontalTextPadding
import io.realm.sample.bookshelf.model.Book

object DetailsScreen {
    enum class ScreenMode {
        ADD, REMOVE
    }
}

val DetailsScreen.name: String
    get() = "Details"

val DetailsScreen.argScreenMode: String
    get() = "ARG_SCREEN_MODE"

val DetailsScreen.argBook: String
    get() = "ARG_BOOK"

@Composable
fun DetailsScreen(
    navController: NavHostController? = null,
    screenMode: DetailsScreen.ScreenMode = DetailsScreen.ScreenMode.ADD,
    book: Book,
    addBook: (Book) -> Unit,
    removeBook: (Book) -> Unit
) {
    Column {
        Spacer(modifier = Modifier.padding(8.dp))
        DetailsHeader(text = stringResource(id = R.string.details_title))
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = book.title,
            modifier = Modifier
                .padding(
                    start = horizontalTextPadding,
                    end = horizontalTextPadding
                )
                .fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(
                    start = horizontalTextPadding,
                    end = horizontalTextPadding
                )
                .fillMaxWidth(),
            onClick = {
                when (screenMode) {
                    DetailsScreen.ScreenMode.ADD -> addBook(book)
                    DetailsScreen.ScreenMode.REMOVE -> removeBook(book)
                }
                requireNotNull(navController)
                    .also {
                        it.popBackStack()
                        it.navigate(NavigationScreen.Books.name)
                    }
            }
        ) {
            Text(
                text = when (screenMode) {
                    DetailsScreen.ScreenMode.ADD -> R.string.details_add_to_bookshelf
                    DetailsScreen.ScreenMode.REMOVE -> R.string.details_remove_from_bookshelf
                }.let {
                    stringResource(id = it).uppercase()
                },
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
    }
}

@Composable
private fun DetailsHeader(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(
            start = horizontalTextPadding,
            end = horizontalTextPadding
        ),
        style = TextStyle(fontSize = 24.sp)
    )
}

@Preview
@Composable
fun DetailsScreenPreview() {
    DetailsScreen(
        book = Book().apply {
            title = "The Pillars of the Earth"
        },
        addBook = {},
        removeBook = {}
    )
}
