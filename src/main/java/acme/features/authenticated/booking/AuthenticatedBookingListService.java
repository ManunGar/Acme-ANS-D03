
package acme.features.authenticated.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Bookings.Booking;
import acme.entities.Passengers.Passenger;

@GuiService
public class AuthenticatedBookingListService extends AbstractGuiService<Authenticated, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Booking> bookings;
		bookings = this.repository.findAllBooking();

		super.getBuffer().addData(bookings);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		String price = "";

		Collection<Passenger> namesPassengers = this.repository.findPassengersByBooking(booking.getId());
		price += booking.getPrice().getAmount().toString() + " ";
		price += booking.getPrice().getCurrency();

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "travelClass", "draftMode", "lastNibble");
		dataset.put("price", price);
		dataset.put("passengers", namesPassengers);

		super.getResponse().addData(dataset);
	}
}
