
package acme.features.authenticatedcustomer;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Bookings.Booking;
import acme.entities.Bookings.TravelClass;
import acme.entities.Passengers.Passenger;

@GuiService
public class AuthenticatedBookingShowService extends AbstractGuiService<Authenticated, Booking> {
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
		int id;
		Booking booking;

		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);
		super.getBuffer().addData(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices choices;

		choices = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		Collection<Passenger> passengersNumber = this.repository.findPassengersByBooking(booking.getId());
		Collection<String> passengers = passengersNumber.stream().map(x -> x.getFullName()).toList();
		System.out.println(booking.isDraftMode());
		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "price", "lastNibble", "draftMode");
		dataset.put("travelClass", choices);
		dataset.put("passengers", passengers);

		super.getResponse().addData(dataset);
	}
}
