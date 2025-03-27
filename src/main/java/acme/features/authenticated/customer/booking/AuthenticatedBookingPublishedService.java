
package acme.features.authenticated.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Bookings.Booking;
import acme.entities.Bookings.TravelClass;
import acme.entities.Flight.Flight;
import acme.entities.Flight.FlightRepository;
import acme.entities.Passengers.Passenger;
import acme.realms.Customer;

@GuiService
public class AuthenticatedBookingPublishedService extends AbstractGuiService<Customer, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedBookingRepository	repository;

	@Autowired
	private FlightRepository				flightRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Booking booking;
		int id;

		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		super.bindObject(booking, "locatorCode", "purchaseMoment", "price", "lastNibble", "passengers", "travelClass");

	}

	@Override
	public void validate(final Booking booking) {
		if (booking.getLastNibble() == null || booking.getLastNibble().isBlank() || booking.getLastNibble().isEmpty()) {
			String lastNibbleStored = this.repository.findBookingById(booking.getId()).getLastNibble();
			if (lastNibbleStored == null || lastNibbleStored.isBlank() || lastNibbleStored.isEmpty())
				super.state(false, "lastNibble", "acme.validation.confirmation.message.lastNibble");
		}
	}

	@Override
	public void perform(final Booking booking) {
		if (booking.getLastNibble() == null || booking.getLastNibble().isBlank() || booking.getLastNibble().isEmpty())
			booking.setLastNibble(this.repository.findBookingById(booking.getId()).getLastNibble());
		booking.setDraftMode(false);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices choices;
		SelectChoices flightChoices;

		Collection<Flight> flights = this.flightRepository.findAllFlight();
		flightChoices = SelectChoices.from(flights, "description", booking.getFlight());
		choices = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		Collection<Passenger> passengersNumber = this.repository.findPassengersByBooking(booking.getId());
		Collection<String> passengers = passengersNumber.stream().map(x -> x.getFullName()).toList();

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "price", "lastNibble", "draftMode");
		dataset.put("travelClass", choices);
		dataset.put("passengers", passengers);
		dataset.put("flight", flightChoices.getSelected().getKey());
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}
}
