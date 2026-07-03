package pl.przyczepki.email.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccountConfirmationData(
    val confirmationLink: String,
    val recipientName: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val taxIdentifier: String,
    val taxIdentifierLabel: String,
    val linkExpiresAt: String? = null,
) {
    fun toTemplateModel(): Map<String, Any?> = buildMap {
        put("confirmationLink", confirmationLink)
        put("recipientName", recipientName)
        put("email", email)
        put("firstName", firstName)
        put("lastName", lastName)
        put("taxIdentifier", taxIdentifier)
        put("taxIdentifierLabel", taxIdentifierLabel)
        if (linkExpiresAt != null) {
            put("linkExpiresAt", linkExpiresAt)
        }
    }
}
