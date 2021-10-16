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

package io.realm.sample.bookshelf.database

import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import io.realm.delete
import io.realm.objects
import io.realm.sample.bookshelf.model.Book
import kotlinx.coroutines.flow.Flow

class RealmDatabase {

    val realm: Realm by lazy {
        val configuration = RealmConfiguration.with(schema = setOf(Book::class))
        Realm.open(configuration)
    }

    fun getAllBooks(): List<Book> {
        return realm.objects(Book::class)
    }

    fun getAllBooksAsFlow(): Flow<List<Book>> {
        return realm.objects<Book>().observe()
    }

    fun getAllBooksAsCommonFlow(): CFlow<RealmResults<Book>> {
        return realm.objects<Book>().observe().wrap()
    }

    fun getBooksByTitle(title: String): List<Book> {
        return realm.objects<Book>().query("title = $0", title)
    }

    fun getBooksByTitleAsFlow(title: String): Flow<List<Book>> {
        return realm.objects<Book>().query("title = $0", title).observe()
    }

    fun getBooksByTitleAsCommonFlow(title: String): CFlow<RealmResults<Book>> {
        return realm.objects<Book>().query("title = $0", title).observe().wrap()
    }

    fun addBook(book: Book) {
        realm.writeBlocking {
            copyToRealm(book)
        }
    }

    fun deleteBook(title: String) {
        realm.writeBlocking {
            objects<Book>().query("title = $0", title)
                .first()
                .let { findLatest(it) }
                ?.delete()
                ?: throw IllegalStateException("Book not found.")
        }
    }

    fun clearAllBooks() {
        realm.writeBlocking {
            objects<Book>().delete()
        }
    }
}
