package io.realm.example

import com.jakewharton.fliptables.FlipTableConverters
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val realmConfiguration = RealmConfiguration.create(schema = setOf(Author::class))
    val realm = Realm.open(realmConfiguration)
    AuthorsREPL(realm).start()
    realm.close()
    exitProcess(0)
}

class Author : RealmObject {
    var firstName: String? = ""
    var lastName: String? = ""
    var age: Int? = 0
}

class AuthorsREPL(private val realm: Realm) {

    fun start() {
        displayAuthors()
        print("Add a new author? (yes/no)\t: ")
        var continueAdding = readLine() ?: "no"

        var firstName: String? = null
        var lastName: String? = null
        var age: Int? = null
        while (continueAdding.equals("yes", ignoreCase = true)) {
            print("First Name\t: ")
            firstName = readLine()
            print("Last Name\t: ")
            lastName = readLine()
            print("Age\t: ")
            age = readLine()?.toInt()

            addAuthor(firstName, lastName, age)
            displayAuthors()

            // continue yes/no
            println("Add a new author? (yes/no)")
            continueAdding = readLine() ?: "no"
        }
    }

    private fun addAuthor(firstName: String?, lastName: String?, age: Int?) {
        realm.writeBlocking {
            copyToRealm(Author().apply {
                this.firstName = firstName
                this.lastName = lastName
                this.age = age
            })
        }
    }

    private fun displayAuthors() {
        val headers = arrayOf("First Name", "Last Name", "Age")
        val authors: RealmResults<Author> = realm.query<Author>().find()
        if (authors.isNotEmpty()) {
           val persistedAuthors =  mutableListOf<Array<String>>().also { data ->
                authors.map { author ->
                    arrayOf(author.firstName ?: "N/A", author.lastName ?: "N/A", author.age?.toString() ?: "N/A").also {
                        data.add(it)
                    }
                }
            }
            println(FlipTableConverters.fromObjects(headers, persistedAuthors.toTypedArray()))
        }
    }
}

