package models // want to access all models from single package

// these models act as our records (rows) for the database
// instead of having to pass tuples in SQL queries
// we can just create a class instance (e.g new_airport = Airport(...)) 
// which we then pass to the access_logic files

data class Airport(
    val id: Int = 0,
    val iataCode: String,
    val name: String? = null,
    val city: String? = null,
    val country: String? = null
)

data class Flight(
    val id: Int = 0,
    val flightNumber: Int? = null,
    val originAirport: Int,
    val destinationAirport: Int,
    val scheduledDepartureTime: String? = null,
    val scheduledArrivalTime: String? = null,
    val status: String = "scheduled",
    val capacity: Int? = null
)

data class FareClass(
    val id: Int = 0,
    val classCode: String,
    val cabinClass: String? = null,
    val displayName: String? = null,
    val refundable: Int = 0,
    val cancelProtocol: String = "free cancellation",
    val advanceSeatSelection: Int = 0,
    val priorityCheckin: Int = 0,
    val priorityBoarding: Int = 0,
    val loungeAccess: Int = 0,
    val carryOnAllowed: Int = 1,
    val carryOnWeightKg: Int = 7,
    val checkedBaggagePieces: Int = 0,
    val checkedBaggageWeightKg: Int = 0,
    val milesEarnRate: Double = 1.0,
    val minimumMilesForBooking: Int? = null,
    val description: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class FlightFare(
    val id: Int = 0,
    val flightId: Int,
    val fareClassId: Int,
    val price: Double,
    val currency: String = "GBP",
    val seatsAvailable: Int,
    val saleStart: String? = null,
    val saleEnd: String? = null
)

data class User(
    val id: Int = 0,
    val email: String,
    val passwordHash: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val dateOfBirth: String? = null,
    val createdAt: String? = null,
    val accountStatus: String = "active"
)

data class Booking(
    val id: Int = 0,
    val userId: Int? = null,
    val bookingReference: String,
    val paymentId: Int? = null,
    val createdAt: String? = null,
    val bookingStatus: String = "pending",
    val cancelledAt: String? = null,
    val amendable: Int = 1
)

data class Payment(
    val id: Int = 0,
    val bookingId: Int,
    val amount: Double? = null,
    val paymentMethod: String? = null,
    val paymentStatus: String = "pending",
    val paidAt: String? = null,
    val providerReference: String? = null,
    val currency: String = "GBP"
)

data class Passenger(
    val id: Int = 0,
    val bookingId: Int? = null,
    val email: String? = null,
    val checkedIn: Int = 0,
    val title: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val nationality: String? = null,
    val documentType: String? = null,
    val documentNumber: String? = null,
    val documentCountry: String? = null,
    val documentExpiry: String? = null
)

data class BookingSegment(
    val id: Int = 0,
    val bookingId: Int,
    val flightId: Int,
    val flightFareId: Int
)

data class Seat(
    val id: Int = 0,
    val flightId: Int,
    val seatCode: String,
    val cabinClass: String? = null,
    val position: String? = null,
    val extraLegroom: Int = 0,
    val exitRow: Int = 0,
    val reducedMobility: Int = 0,
    val status: String = "available"
)

data class SeatAssignment(
    val id: Int = 0,
    val passengerId: Int,
    val bookingSegmentId: Int,
    val seatId: Int? = null
)

data class Staff(
    val id: Int = 0,
    val email: String,
    val passwordHash: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val role: String? = null,
    val createdAt: String? = null
)

data class Complaint(
    val id: Int = 0,
    val userId: Int? = null,
    val type: String? = null,
    val message: String? = null,
    val createdAt: String? = null,
    val status: String = "open",
    val handledByStaffId: Int? = null
)

data class Notification(
    val id: Int = 0,
    val userId: Int? = null,
    val type: String? = null,
    val message: String? = null,
    val createdAt: String? = null,
    val readAt: String? = null
)