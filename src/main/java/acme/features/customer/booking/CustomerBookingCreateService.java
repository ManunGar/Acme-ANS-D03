
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.basis.AbstractRealm;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Bookings.Booking;
import acme.entities.Bookings.TravelClass;
import acme.entities.Flight.Flight;
import acme.entities.Flight.FlightRepository;
import acme.entities.Passengers.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerBookingRepository	repository;

	@Autowired
	private FlightRepository			flightRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean isCustomer = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(isCustomer);
	}

	@Override
	public void load() {
		Booking booking;
		AbstractRealm principal = super.getRequest().getPrincipal().getActiveRealm();
		int customerId = principal.getId();
		Customer customer = this.repository.findCustomerById(customerId);
		Date today = MomentHelper.getCurrentMoment();

		booking = new Booking();
		booking.setCustomer(customer);
		booking.setPurchaseMoment(today);
		booking.setDraftMode(true);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.flightRepository.findFlightById(flightId);

		super.bindObject(booking, "locatorCode", "purchaseMoment", "lastNibble", "price", "travelClass");
		booking.setFlight(flight);

	}

	@Override
	public void validate(final Booking booking) {
		;
	}

	@Override
	public void perform(final Booking booking) {
		Date today = MomentHelper.getCurrentMoment();
		booking.setPurchaseMoment(today);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices choices;
		SelectChoices flightChoices;

		Date today = MomentHelper.getCurrentMoment();
		Collection<Flight> flights = this.flightRepository.findAllFlight().stream().filter(f -> f.getDraftMode() == false && this.flightRepository.findDepartureByFlightId(f.getId()).get(0).after(today)).toList();
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
