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

package io.realm.sample.bookshelf.network

import io.realm.RealmList
import io.realm.sample.bookshelf.database.RealmBook
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiBook(
    val subtitle: String? = "",
    val title: String = "",
    @SerialName("cover_i") val imgId: String? = null,
    @SerialName("author_name") val authors: List<String> = emptyList()
)

fun ApiBook.toRealmBook(): RealmBook {
    return RealmBook().apply {
        subtitle = this@toRealmBook.subtitle
        title = this@toRealmBook.title
        imgId = this@toRealmBook.imgId
        authors = RealmList<String>().apply {
            addAll(this@toRealmBook.authors)
        }
    }
}
