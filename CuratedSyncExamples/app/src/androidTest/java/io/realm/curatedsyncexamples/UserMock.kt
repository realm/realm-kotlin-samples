package io.realm.curatedsyncexamples

import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.AuthenticationProvider
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.Functions
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.UserIdentity
import io.realm.kotlin.mongodb.auth.ApiKeyAuth

class UserMock(
    override val id: String
) : User {
    override val accessToken: String
        get() = TODO("Not yet implemented")
    override val apiKeyAuth: ApiKeyAuth
        get() = TODO("Not yet implemented")
    override val app: App
        get() = TODO("Not yet implemented")
    override val deviceId: String
        get() = TODO("Not yet implemented")
    override val functions: Functions
        get() = TODO("Not yet implemented")
    override val identities: List<UserIdentity>
        get() = TODO("Not yet implemented")
    override val identity: String
        get() = TODO("Not yet implemented")
    override val loggedIn: Boolean
        get() = TODO("Not yet implemented")
    override val provider: AuthenticationProvider
        get() = TODO("Not yet implemented")
    override val refreshToken: String
        get() = TODO("Not yet implemented")
    override val state: User.State
        get() = TODO("Not yet implemented")

    override suspend fun delete() {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun linkCredentials(credentials: Credentials): User {
        TODO("Not yet implemented")
    }

    override suspend fun logOut() {
        TODO("Not yet implemented")
    }

    override suspend fun refreshCustomData() {
        TODO("Not yet implemented")
    }

    override suspend fun remove(): User {
        TODO("Not yet implemented")
    }
}