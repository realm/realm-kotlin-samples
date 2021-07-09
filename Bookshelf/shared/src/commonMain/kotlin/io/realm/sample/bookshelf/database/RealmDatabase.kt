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

import io.realm.Cancellable
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.delete
import io.realm.sample.bookshelf.model.Book
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RealmDatabase {

    val realm: Realm by lazy {
        RealmConfiguration.Builder()
            .schema(Book::class)
            .build()
            .let { Realm.open(it) }
    }

    fun getAllBooks(): List<Book> {
        return realm.objects(Book::class)
    }

    fun getAllBooksAsFlowable(): Flow<List<Book>> = callbackFlow {
        val cancellable: Cancellable = realm.objects<Book>().observe { result ->
            offer(result.toList()) // FIXME RealmResults is the same (equals) causing the compose to not re-compose (maybe define a hashcode/equals based on size or Core version/counter of the list)
        }

        awaitClose {
            cancellable.cancel()
        }
    }

    fun getAllBooksAsCallback(success: (List<Book>) -> Unit): Cancellable {
        return realm.objects<Book>().observe { result ->
            success(result.toList()) // FIXME RealmResults is the same (equals) causing the compose to not re-compose (maybe define a hashcode/equals based on size or Core version/counter of the list)
        }
    }

    fun getBooksByTitle(title: String): List<Book> {
        return realm.objects<Book>().query("title = $0", title)
    }

    // Missing insert as list
    fun addBook(book: Book) {
        realm.writeBlocking {
            copyToRealm(book)
        }
    }

    fun deleteBook(title: String) {
        realm.writeBlocking {
            objects<Book>().query("title = $0", title).first().delete()
        }
    }

    fun clearAllBooks() {
        realm.writeBlocking {
            objects<Book>().delete()
        }
    }

    fun onBookChange(block: () -> Unit): Cancellable {
        return realm.objects<Book>().observe { block() }
    }
}
