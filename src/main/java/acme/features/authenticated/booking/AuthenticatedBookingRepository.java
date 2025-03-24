
package acme.features.authenticated.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.Bookings.Booking;
import acme.entities.Passengers.Passenger;
import acme.realms.Customer;

@Repository
public interface AuthenticatedBookingRepository extends AbstractRepository {

	@Query("select ua from UserAccount ua where ua.id = :id")
	UserAccount findUserAccountById(int id);

	@Query("select c from Customer c where c.userAccount.id = :id")
	Customer findConsumerByUserAccountId(int id);

	@Query("select b from Booking b")
	Collection<Booking> findAllBooking();

	@Query("select bk.passenger from BookingRecord bk where bk.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBooking(@Param("bookingId") Integer bookingId);
}
