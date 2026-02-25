----------------------------------------
--       SQL QUERIES GO HERE          --
--      RUN WITH CTRL+SHIFT+Q         --
----------------------------------------

select * from flight;



----------------------------------------
--            TABLE NAMES             --
----------------------------------------

-- airport
-- flight
-- fare_class
-- flight_fare
-- user
-- booking
-- payment
-- passenger
-- booking_segment
-- seat
-- seat_assignment
-- staff
-- complaint
-- notification


----------------------------------------
--       TABLE SQL CONSTRUCTION       --
----------------------------------------

-- CREATE TABLE "airport" (
--   "airport_id" INTEGER PRIMARY KEY,
--   "iata_code" TEXT UNIQUE NOT NULL,
--   "name" TEXT,
--   "city" TEXT,
--   "country" TEXT
-- );

-- CREATE TABLE "flight" (
--   "flight_id" INTEGER PRIMARY KEY,
--   "flight_number" INTEGER,
--   "origin_airport" INTEGER,
--   "destination_airport" INTEGER,
--   "scheduled_departure_time" TEXT,
--   "scheduled_arrival_time" TEXT,
--   "status" TEXT DEFAULT 'scheduled',
--   "capacity" INTEGER,
--   FOREIGN KEY ("origin_airport") REFERENCES "airport" ("airport_id"),
--   FOREIGN KEY ("destination_airport") REFERENCES "airport" ("airport_id")
-- );

-- CREATE TABLE "fare_class" (
--   "fare_class_id" INTEGER PRIMARY KEY,
--   "class_code" TEXT UNIQUE,
--   "cabin_class" TEXT,
--   "display_name" TEXT,
--   "refundable" INTEGER DEFAULT 0,
--   "cancel_protocol" TEXT DEFAULT 'free cancellation',
--   "advance_seat_selection" INTEGER DEFAULT 0,
--   "priority_checkin" INTEGER DEFAULT 0,
--   "priority_boarding" INTEGER DEFAULT 0,
--   "lounge_access" INTEGER DEFAULT 0,
--   "carry_on_allowed" INTEGER DEFAULT 1,
--   "carry_on_weight_kg" INTEGER DEFAULT 7,
--   "checked_baggage_pieces" INTEGER DEFAULT 0,
--   "checked_baggage_weight_kg" INTEGER DEFAULT 0,
--   "miles_earn_rate" REAL DEFAULT 1.0,
--   "minimum_miles_for_booking" INTEGER,
--   "description" TEXT,
--   "created_at" TEXT DEFAULT (datetime('now')),
--   "updated_at" TEXT DEFAULT (datetime('now'))
-- );

-- CREATE TABLE "flight_fare" (
--   "flight_fare_id" INTEGER PRIMARY KEY,
--   "flight_id" INTEGER NOT NULL,
--   "fare_class_id" INTEGER NOT NULL,
--   "price" REAL NOT NULL,
--   "currency" TEXT DEFAULT 'GBP',
--   "seats_available" INTEGER NOT NULL,
--   "sale_start" TEXT,
--   "sale_end" TEXT,
--   FOREIGN KEY ("flight_id") REFERENCES "flight" ("flight_id"),
--   FOREIGN KEY ("fare_class_id") REFERENCES "fare_class" ("fare_class_id")
-- );

-- CREATE TABLE "user" (
--   "user_id" INTEGER PRIMARY KEY,
--   "email" TEXT UNIQUE,
--   "password_hash" TEXT,
--   "first_name" TEXT,
--   "last_name" TEXT,
--   "phone_number" TEXT,
--   "date_of_birth" TEXT,
--   "created_at" TEXT DEFAULT (datetime('now')),
--   "account_status" TEXT DEFAULT 'active'
-- );

-- CREATE TABLE "booking" (
--   "booking_id" INTEGER PRIMARY KEY,
--   "user_id" INTEGER,
--   "booking_reference" TEXT UNIQUE,
--   "payment_id" INTEGER UNIQUE,
--   "created_at" TEXT DEFAULT (datetime('now')),
--   "booking_status" TEXT DEFAULT 'pending',
--   "cancelled_at" TEXT,
--   "amendable" INTEGER DEFAULT 1,
--   FOREIGN KEY ("user_id") REFERENCES "user" ("user_id")
-- );

-- CREATE TABLE "payment" (
--   "payment_id" INTEGER PRIMARY KEY,
--   "booking_id" INTEGER UNIQUE,
--   "amount" REAL,
--   "payment_method" TEXT,
--   "payment_status" TEXT DEFAULT 'pending',
--   "paid_at" TEXT,
--   "provider_reference" TEXT,
--   "currency" TEXT DEFAULT 'GBP',
--   FOREIGN KEY ("booking_id") REFERENCES "booking" ("booking_id")
-- );

-- CREATE TABLE "passenger" (
--   "passenger_id" INTEGER PRIMARY KEY,
--   "booking_id" INTEGER,
--   "email" TEXT,
--   "checked_in" INTEGER DEFAULT 0,
--   "title" TEXT,
--   "first_name" TEXT,
--   "last_name" TEXT,
--   "date_of_birth" TEXT,
--   "gender" TEXT,
--   "nationality" TEXT,
--   "document_type" TEXT,
--   "document_number" TEXT,
--   "document_country" TEXT,
--   "document_expiry" TEXT,
--   FOREIGN KEY ("booking_id") REFERENCES "booking" ("booking_id")
-- );

-- CREATE TABLE "booking_segment" (
--   "booking_segment_id" INTEGER PRIMARY KEY,
--   "booking_id" INTEGER NOT NULL,
--   "flight_id" INTEGER NOT NULL,
--   "flight_fare_id" INTEGER NOT NULL,
--   FOREIGN KEY ("booking_id") REFERENCES "booking" ("booking_id"),
--   FOREIGN KEY ("flight_id") REFERENCES "flight" ("flight_id"),
--   FOREIGN KEY ("flight_fare_id") REFERENCES "flight_fare" ("flight_fare_id")
-- );

-- CREATE TABLE "seat" (
--   "seat_id" INTEGER PRIMARY KEY,
--   "flight_id" INTEGER NOT NULL,
--   "seat_code" TEXT NOT NULL,
--   "cabin_class" TEXT,
--   "position" TEXT,
--   "extra_legroom" INTEGER DEFAULT 0,
--   "exit_row" INTEGER DEFAULT 0,
--   "reduced_mobility" INTEGER DEFAULT 0,
--   "status" TEXT DEFAULT 'available',
--   FOREIGN KEY ("flight_id") REFERENCES "flight" ("flight_id")
-- );

-- CREATE TABLE "seat_assignment" (
--   "seat_assignment_id" INTEGER PRIMARY KEY,
--   "passenger_id" INTEGER UNIQUE,
--   "booking_segment_id" INTEGER UNIQUE,
--   "seat_id" INTEGER,
--   FOREIGN KEY ("passenger_id") REFERENCES "passenger" ("passenger_id"),
--   FOREIGN KEY ("booking_segment_id") REFERENCES "booking_segment" ("booking_segment_id"),
--   FOREIGN KEY ("seat_id") REFERENCES "seat" ("seat_id")
-- );

-- CREATE TABLE "staff" (
--   "staff_id" INTEGER PRIMARY KEY,
--   "email" TEXT UNIQUE,
--   "password_hash" TEXT,
--   "first_name" TEXT,
--   "last_name" TEXT,
--   "phone_number" TEXT,
--   "role" TEXT,
--   "created_at" TEXT DEFAULT (datetime('now'))
-- );

-- CREATE TABLE "complaint" (
--   "complaint_id" INTEGER PRIMARY KEY,
--   "user_id" INTEGER,
--   "type" TEXT,
--   "message" TEXT,
--   "created_at" TEXT DEFAULT (datetime('now')),
--   "status" TEXT DEFAULT 'open',
--   "handled_by_staff_id" INTEGER,
--   FOREIGN KEY ("user_id") REFERENCES "user" ("user_id"),
--   FOREIGN KEY ("handled_by_staff_id") REFERENCES "staff" ("staff_id")
-- );

-- CREATE TABLE "notification" (
--   "notification_id" INTEGER PRIMARY KEY,
--   "user_id" INTEGER,
--   "type" TEXT,
--   "message" TEXT,
--   "created_at" TEXT DEFAULT (datetime('now')),
--   "read_at" TEXT,
--   FOREIGN KEY ("user_id") REFERENCES "user" ("user_id")
-- );


----------------------------------------
--       INPUT SOME RANDOM DATA       --
----------------------------------------

-- GENERATED THIS DATA USING CLAUDE BECAUSE I AM LAZY

-- -- Airports
-- INSERT INTO "airport" VALUES (1, 'LHR', 'London Heathrow', 'London', 'GB');
-- INSERT INTO "airport" VALUES (2, 'JFK', 'John F. Kennedy International', 'New York', 'US');
-- INSERT INTO "airport" VALUES (3, 'CDG', 'Charles de Gaulle', 'Paris', 'FR');
-- INSERT INTO "airport" VALUES (4, 'MAN', 'Manchester Airport', 'Manchester', 'GB');
-- INSERT INTO "airport" VALUES (5, 'DXB', 'Dubai International', 'Dubai', 'AE');

-- -- Flights
-- INSERT INTO "flight" VALUES (1, 101, 1, 2, '2026-03-15 08:00:00', '2026-03-15 11:00:00', 'scheduled', 180);
-- INSERT INTO "flight" VALUES (2, 202, 1, 3, '2026-03-15 09:30:00', '2026-03-15 12:00:00', 'scheduled', 220);
-- INSERT INTO "flight" VALUES (3, 303, 4, 5, '2026-03-16 14:00:00', '2026-03-17 00:30:00', 'scheduled', 300);

-- -- Fare Classes
-- INSERT INTO "fare_class" VALUES (1, 'Y', 'Economy', 'Economy Standard', 0, 'no cancellation', 0, 0, 0, 0, 1, 7, 0, 0, 1.0, NULL, 'Basic economy fare', datetime('now'), datetime('now'));
-- INSERT INTO "fare_class" VALUES (2, 'W', 'Economy', 'Economy Flex', 1, 'free cancellation', 1, 0, 0, 0, 1, 7, 1, 23, 1.2, NULL, 'Flexible economy with one checked bag', datetime('now'), datetime('now'));
-- INSERT INTO "fare_class" VALUES (3, 'J', 'Business', 'Business Flex', 1, 'free cancellation', 1, 1, 1, 1, 1, 10, 2, 32, 2.0, NULL, 'Full business class experience', datetime('now'), datetime('now'));

-- -- Flight Fares
-- INSERT INTO "flight_fare" VALUES (1, 1, 1, 199.99, 'GBP', 120, '2026-01-01 00:00:00', '2026-03-14 23:59:59');
-- INSERT INTO "flight_fare" VALUES (2, 1, 2, 349.99, 'GBP', 40,  '2026-01-01 00:00:00', '2026-03-14 23:59:59');
-- INSERT INTO "flight_fare" VALUES (3, 1, 3, 899.99, 'GBP', 20,  '2026-01-01 00:00:00', '2026-03-14 23:59:59');
-- INSERT INTO "flight_fare" VALUES (4, 2, 1, 89.99,  'GBP', 150, '2026-01-01 00:00:00', '2026-03-14 23:59:59');
-- INSERT INTO "flight_fare" VALUES (5, 3, 2, 499.99, 'GBP', 60,  '2026-01-01 00:00:00', '2026-03-15 23:59:59');

-- -- Users
-- INSERT INTO "user" VALUES (1, 'james.walker@email.com', 'hashed_pw_1', 'James', 'Walker', '07700900001', '1990-04-12', datetime('now'), 'active');
-- INSERT INTO "user" VALUES (2, 'priya.sharma@email.com', 'hashed_pw_2', 'Priya', 'Sharma', '07700900002', '1985-11-23', datetime('now'), 'active');
-- INSERT INTO "user" VALUES (3, 'tom.nguyen@email.com',   'hashed_pw_3', 'Tom',   'Nguyen',  '07700900003', '2000-07-05', datetime('now'), 'active');

-- -- Bookings
-- INSERT INTO "booking" VALUES (1, 1, 'BOOK001', 1, datetime('now'), 'confirmed', NULL, 1);
-- INSERT INTO "booking" VALUES (2, 2, 'BOOK002', 2, datetime('now'), 'confirmed', NULL, 1);
-- INSERT INTO "booking" VALUES (3, 3, 'BOOK003', 3, datetime('now'), 'cancelled', datetime('now'), 0);

-- -- Payments
-- INSERT INTO "payment" VALUES (1, 1, 199.99, 'credit', 'paid',    datetime('now'), 'PAY-REF-001', 'GBP');
-- INSERT INTO "payment" VALUES (2, 2, 349.99, 'debit',  'paid',    datetime('now'), 'PAY-REF-002', 'GBP');
-- INSERT INTO "payment" VALUES (3, 3, 89.99,  'paypal', 'refunded',datetime('now'), 'PAY-REF-003', 'GBP');

-- -- Passengers
-- INSERT INTO "passenger" VALUES (1, 1, 'james.walker@email.com', 1, 'Mr', 'James', 'Walker', '1990-04-12', 'M', 'GB', 'passport', 'GB123456789', 'GB', '2031-01-01');
-- INSERT INTO "passenger" VALUES (2, 2, 'priya.sharma@email.com', 1, 'Ms', 'Priya', 'Sharma',  '1985-11-23', 'F', 'IN', 'passport', 'IN987654321', 'IN', '2029-06-15');
-- INSERT INTO "passenger" VALUES (3, 3, 'tom.nguyen@email.com',   0, 'Mr', 'Tom',   'Nguyen',  '2000-07-05', 'M', 'US', 'passport', 'US111222333', 'US', '2028-09-30');

-- -- Booking Segments
-- INSERT INTO "booking_segment" VALUES (1, 1, 1, 1);
-- INSERT INTO "booking_segment" VALUES (2, 2, 1, 2);
-- INSERT INTO "booking_segment" VALUES (3, 3, 2, 4);

-- -- Seats
-- INSERT INTO "seat" VALUES (1,  1, '1A',  'Business', 'window', 1, 0, 0, 'occupied');
-- INSERT INTO "seat" VALUES (2,  1, '14B', 'Economy',  'middle', 0, 0, 0, 'occupied');
-- INSERT INTO "seat" VALUES (3,  1, '22C', 'Economy',  'aisle',  0, 0, 0, 'occupied');
-- INSERT INTO "seat" VALUES (4,  1, '15A', 'Economy',  'window', 0, 0, 0, 'available');
-- INSERT INTO "seat" VALUES (5,  1, '20F', 'Economy',  'window', 0, 1, 0, 'available');
-- INSERT INTO "seat" VALUES (6,  2, '2A',  'Business', 'window', 1, 0, 0, 'available');
-- INSERT INTO "seat" VALUES (7,  2, '10D', 'Economy',  'aisle',  0, 0, 1, 'available');
-- INSERT INTO "seat" VALUES (8,  3, '5A',  'Business', 'window', 1, 0, 0, 'available');

-- -- Seat Assignments
-- INSERT INTO "seat_assignment" VALUES (1, 1, 1, 1);
-- INSERT INTO "seat_assignment" VALUES (2, 2, 2, 2);
-- INSERT INTO "seat_assignment" VALUES (3, 3, 3, 3);

-- -- Staff
-- INSERT INTO "staff" VALUES (1, 'sarah.jones@airline.com', 'hashed_staff_pw_1', 'Sarah', 'Jones', '07700800001', 'customer_service', datetime('now'));
-- INSERT INTO "staff" VALUES (2, 'david.chen@airline.com',  'hashed_staff_pw_2', 'David', 'Chen',  '07700800002', 'admin', datetime('now'));

-- -- Complaints
-- INSERT INTO "complaint" VALUES (1, 2, 'delay', 'My flight was delayed by 3 hours and I missed a connection.', datetime('now'), 'open', 1);
-- INSERT INTO "complaint" VALUES (2, 1, 'baggage', 'My checked bag arrived damaged.', datetime('now'), 'resolved', 1);

-- -- Notifications
-- INSERT INTO "notification" VALUES (1, 1, 'booking_confirmed', 'Your booking BOOK001 has been confirmed.', datetime('now'), datetime('now'));
-- INSERT INTO "notification" VALUES (2, 2, 'booking_confirmed', 'Your booking BOOK002 has been confirmed.', datetime('now'), datetime('now'));
-- INSERT INTO "notification" VALUES (3, 3, 'booking_cancelled', 'Your booking BOOK003 has been cancelled and a refund issued.', datetime('now'), NULL);