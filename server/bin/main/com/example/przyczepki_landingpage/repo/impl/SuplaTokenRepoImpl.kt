package com.example.przyczepki_landingpage.repo.impl

import com.example.przyczepki_landingpage.repo.SuplaStoredTokens
import com.example.przyczepki_landingpage.repo.SuplaTokenRepo
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Contextual
import org.bson.types.ObjectId

class SuplaTokenRepoImpl(
    private val collection: MongoCollection<SuplaTokenTable>,
) : SuplaTokenRepo {

    override suspend fun load(): SuplaStoredTokens? {
        return collection.find(eq("id", DOCUMENT_ID)).firstOrNull()?.toStoredTokens()
    }

    override suspend fun save(tokens: SuplaStoredTokens) {
        collection.replaceOne(
            eq("id", DOCUMENT_ID),
            SuplaTokenTable(
                id = DOCUMENT_ID,
                accessToken = tokens.accessToken,
                refreshToken = tokens.refreshToken,
                accessTokenExpiresAtEpochMs = tokens.accessTokenExpiresAtEpochMs,
            ),
            ReplaceOptions().upsert(true),
        )
    }

    companion object {
        const val DOCUMENT_ID = "supla_oauth"
    }
}

data class SuplaTokenTable(
    @Contextual
    val _id: ObjectId? = ObjectId(),
    val id: String = SuplaTokenRepoImpl.DOCUMENT_ID,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val accessTokenExpiresAtEpochMs: Long? = null,
) {
    fun toStoredTokens() = SuplaStoredTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
        accessTokenExpiresAtEpochMs = accessTokenExpiresAtEpochMs,
    )
}
