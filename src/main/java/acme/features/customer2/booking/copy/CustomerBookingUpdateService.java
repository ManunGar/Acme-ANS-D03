
package acme.features.customer2.booking.copy;

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
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository	repository;

	@Autowired
	private FlightRepository			flightRepository;

	// AbstractGuiService interfaced ------------------------------------------


	@Override
	public void authorise() {
		int id;
		Booking booking;
		int customerId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();

		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);
		boolean status = booking.getCustomer().getUserAccount().getId() == customerId && super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);
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

		super.bindObject(booking, "locatorCode", "purchaseMoment", "price", "lastNibble", "travelClass", "flight", "draftMode");
	}

	@Override
	public void validate(final Booking booking) {
		if (booking.isDraftMode() == false)
			super.state(false, "draftMode", "acme.validation.confirmation.message.update");
		Booking b = this.repository.findBookingByLocatorCode(booking.getLocatorCode());
		if (b != null && b.getId() != booking.getId())
			super.state(false, "locatorCode", "acme.validation.confirmation.message.booking.locator-code");

	}

	@Override
	public void perform(final Booking booking) {
		Booking b = this.repository.findBookingById(booking.getId());
		b.setFlight(booking.getFlight());
		b.setLocatorCode(booking.getLocatorCode());
		b.setTravelClass(booking.getTravelClass());
		b.setLastNibble(booking.getLastNibble());
		this.repository.save(b);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices choices;
		SelectChoices flightChoices;

		Collection<Flight> flights = this.flightRepository.findAllFlight().stream().filter(f -> f.getDraftMode() == false).toList();
		flightChoices = SelectChoices.from(flights, "Destination", booking.getFlight());
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
