package pl.przyczepki.email.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReservationConfirmationData(
    val reservationId: String,
    val recipientName: String,
    val email: String,
    val trailerName: String,
    val trailerSize: String? = null,
    val startDate: String,
    val endDate: String,
    val daysNumber: Long? = null,
    val reservationFee: Double? = null,
    val rentalAmount: Double? = null,
    val totalAmount: Double? = null,
    val paidAmount: String? = null,
    val orderId: String? = null,
) {
    fun toTemplateModel(): Map<String, Any?> = buildMap {
        put("reservationId", reservationId)
        put("recipientName", recipientName)
        put("email", email)
        put("trailerName", trailerName)
        put("trailerSize", trailerSize)
        put("startDate", startDate)
        put("endDate", endDate)
        put("daysNumber", daysNumber)
        put("reservationFee", reservationFee)
        put("rentalAmount", rentalAmount)
        put("totalAmount", totalAmount)
        put("paidAmount", paidAmount)
        put("orderId", orderId)
    }
}
