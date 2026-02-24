package tables

import org.jetbrains.exposed.sql.Table

// these objects act as boxes which help us reference the tables

object AirportTable : Table("airport") {
    val id = integer("airport_id").autoIncrement()
    val iataCode = varchar("iata_code", 10).uniqueIndex()
    val name = varchar("name", 255).nullable()
    val city = varchar("city", 255).nullable()
    val country = varchar("country", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}

object FlightTable : Table("flight") {
    val id = integer("flight_id").autoIncrement()
    val flightNumber = integer("flight_number").nullable()
    val originAirport = integer("origin_airport").references(AirportTable.id)
    val destinationAirport = integer("destination_airport").references(AirportTable.id)
    val scheduledDepartureTime = varchar("scheduled_departure_time", 255).nullable()
    val scheduledArrivalTime = varchar("scheduled_arrival_time", 255).nullable()
    val status = varchar("status", 50).default("scheduled")
    val capacity = integer("capacity").nullable()

    override val primaryKey = PrimaryKey(id)
}

object FareClassTable : Table("fare_class") {
    val id = integer("fare_class_id").autoIncrement()
    val classCode = varchar("class_code", 10).uniqueIndex()
    val cabinClass = varchar("cabin_class", 50).nullable()
    val displayName = varchar("display_name", 255).nullable()
    val refundable = integer("refundable").default(0)
    val cancelProtocol = varchar("cancel_protocol", 255).default("free cancellation")
    val advanceSeatSelection = integer("advance_seat_selection").default(0)
    val priorityCheckin = integer("priority_checkin").default(0)
    val priorityBoarding = integer("priority_boarding").default(0)
    val loungeAccess = integer("lounge_access").default(0)
    val carryOnAllowed = integer("carry_on_allowed").default(1)
    val carryOnWeightKg = integer("carry_on_weight_kg").default(7)
    val checkedBaggagePieces = integer("checked_baggage_pieces").default(0)
    val checkedBaggageWeightKg = integer("checked_baggage_weight_kg").default(0)
    val milesEarnRate = double("miles_earn_rate").default(1.0)
    val minimumMilesForBooking = integer("minimum_miles_for_booking").nullable()
    val description = text("description").nullable()
    val createdAt = varchar("created_at", 255)
    val updatedAt = varchar("updated_at", 255)

    override val primaryKey = PrimaryKey(id)
}

object FlightFareTable : Table("flight_fare") {
    val id = integer("flight_fare_id").autoIncrement()
    val flightId = integer("flight_id").references(FlightTable.id)
    val fareClassId = integer("fare_class_id").references(FareClassTable.id)
    val price = double("price")
    val currency = varchar("currency", 10).default("GBP")
    val seatsAvailable = integer("seats_available")
    val saleStart = varchar("sale_start", 255).nullable()
    val saleEnd = varchar("sale_end", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}

object UserTable : Table("user") {
    val id = integer("user_id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255).nullable()
    val firstName = varchar("first_name", 255).nullable()
    val lastName = varchar("last_name", 255).nullable()
    val phoneNumber = varchar("phone_number", 50).nullable()
    val dateOfBirth = varchar("date_of_birth", 20).nullable()
    val createdAt = varchar("created_at", 255)
    val accountStatus = varchar("account_status", 50).default("active")

    override val primaryKey = PrimaryKey(id)
}

object BookingTable : Table("booking") {
    val id = integer("booking_id").autoIncrement()
    val userId = integer("user_id").references(UserTable.id).nullable()
    val bookingReference = varchar("booking_reference", 50).uniqueIndex()
    val paymentId = integer("payment_id").uniqueIndex().nullable()
    val createdAt = varchar("created_at", 255)
    val bookingStatus = varchar("booking_status", 50).default("pending")
    val cancelledAt = varchar("cancelled_at", 255).nullable()
    val amendable = integer("amendable").default(1)

    override val primaryKey = PrimaryKey(id)
}

object PaymentTable : Table("payment") {
    val id = integer("payment_id").autoIncrement()
    val bookingId = integer("booking_id").references(BookingTable.id).uniqueIndex()
    val amount = double("amount").nullable()
    val paymentMethod = varchar("payment_method", 50).nullable()
    val paymentStatus = varchar("payment_status", 50).default("pending")
    val paidAt = varchar("paid_at", 255).nullable()
    val providerReference = varchar("provider_reference", 255).nullable()
    val currency = varchar("currency", 10).default("GBP")

    override val primaryKey = PrimaryKey(id)
}

object PassengerTable : Table("passenger") {
    val id = integer("passenger_id").autoIncrement()
    val bookingId = integer("booking_id").references(BookingTable.id).nullable()
    val email = varchar("email", 255).nullable()
    val checkedIn = integer("checked_in").default(0)
    val title = varchar("title", 20).nullable()
    val firstName = varchar("first_name", 255).nullable()
    val lastName = varchar("last_name", 255).nullable()
    val dateOfBirth = varchar("date_of_birth", 20).nullable()
    val gender = varchar("gender", 10).nullable()
    val nationality = varchar("nationality", 10).nullable()
    val documentType = varchar("document_type", 50).nullable()
    val documentNumber = varchar("document_number", 100).nullable()
    val documentCountry = varchar("document_country", 10).nullable()
    val documentExpiry = varchar("document_expiry", 20).nullable()

    override val primaryKey = PrimaryKey(id)
}

object BookingSegmentTable : Table("booking_segment") {
    val id = integer("booking_segment_id").autoIncrement()
    val bookingId = integer("booking_id").references(BookingTable.id)
    val flightId = integer("flight_id").references(FlightTable.id)
    val flightFareId = integer("flight_fare_id").references(FlightFareTable.id)

    override val primaryKey = PrimaryKey(id)
}

object SeatTable : Table("seat") {
    val id = integer("seat_id").autoIncrement()
    val flightId = integer("flight_id").references(FlightTable.id)
    val seatCode = varchar("seat_code", 10)
    val cabinClass = varchar("cabin_class", 50).nullable()
    val position = varchar("position", 20).nullable()
    val extraLegroom = integer("extra_legroom").default(0)
    val exitRow = integer("exit_row").default(0)
    val reducedMobility = integer("reduced_mobility").default(0)
    val status = varchar("status", 20).default("available")

    override val primaryKey = PrimaryKey(id)
}

object SeatAssignmentTable : Table("seat_assignment") {
    val id = integer("seat_assignment_id").autoIncrement()
    val passengerId = integer("passenger_id").references(PassengerTable.id).uniqueIndex()
    val bookingSegmentId = integer("booking_segment_id").references(BookingSegmentTable.id).uniqueIndex()
    val seatId = integer("seat_id").references(SeatTable.id).nullable()

    override val primaryKey = PrimaryKey(id)
}

object StaffTable : Table("staff") {
    val id = integer("staff_id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255).nullable()
    val firstName = varchar("first_name", 255).nullable()
    val lastName = varchar("last_name", 255).nullable()
    val phoneNumber = varchar("phone_number", 50).nullable()
    val role = varchar("role", 50).nullable()
    val createdAt = varchar("created_at", 255)

    override val primaryKey = PrimaryKey(id)
}

object ComplaintTable : Table("complaint") {
    val id = integer("complaint_id").autoIncrement()
    val userId = integer("user_id").references(UserTable.id).nullable()
    val type = varchar("type", 100).nullable()
    val message = text("message").nullable()
    val createdAt = varchar("created_at", 255)
    val status = varchar("status", 50).default("open")
    val handledByStaffId = integer("handled_by_staff_id").references(StaffTable.id).nullable()

    override val primaryKey = PrimaryKey(id)
}

object NotificationTable : Table("notification") {
    val id = integer("notification_id").autoIncrement()
    val userId = integer("user_id").references(UserTable.id).nullable()
    val type = varchar("type", 100).nullable()
    val message = text("message").nullable()
    val createdAt = varchar("created_at", 255)
    val readAt = varchar("read_at", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}
