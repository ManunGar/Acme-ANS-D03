
package acme.features.customer2;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Bookings.BookingRecord;

@Repository
public interface CustomerBookingRecordRepository extends AbstractRepository {

	@Query("SELECT br FROM BookingRecord br WHERE br.booking.id = :bookingId AND br.passenger.id = :passengerId")
	BookingRecord findBookingRecordBybookingIdpassengerId(@Param("bookingId") Integer bookingId, @Param("passengerId") Integer passengerId);

	@Query("SELECT br FROM BookingRecord br WHERE br.id = :bookingRecordId")
	BookingRecord findBookingRecordById(@Param("bookingRecordId") Integer bookingRecordId);

	@Query("SELECT br FROM BookingRecord br WHERE br.booking.id = :bookingId")
	Collection<BookingRecord> findBookingRecordByBookingId(@Param("bookingId") Integer bookingId);
}
