
package acme.features.authenticated.customer.booking;

import java.util.Collection;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.basis.AbstractRealm;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Bookings.Booking;
import acme.entities.Bookings.TravelClass;
import acme.entities.Flight.Flight;
import acme.entities.Flight.FlightRepository;
import acme.realms.Customer;

@GuiService
public class AuthenticatedBookingCreateService extends AbstractGuiService<Customer, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedBookingRepository	repository;

	@Autowired
	private FlightRepository				flightRepository;

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

		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		int longitud = 6 + random.nextInt(3);
		StringBuilder sb = new StringBuilder(longitud);
		for (int i = 0; i < longitud; i++) {
			char c = chars.charAt(random.nextInt(chars.length()));
			sb.append(c);
		}

		booking = new Booking();
		booking.setCustomer(customer);
		booking.setPurchaseMoment(today);
		booking.setDraftMode(true);
		booking.setLocatorCode(sb.toString());

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		int flightId;
		Flight flight;

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.flightRepository.findFlightById(flightId);

		super.bindObject(booking, "locatorCode", "purchaseMoment", "lastNibble", "price", "travelClass", "draftMode");
		booking.setFlight(flight);

	}

	@Override
	public void validate(final Booking booking) {
		;
	}

	@Override
	public void perform(final Booking booking) {
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

		dataset = super.unbindObject(booking, "locatorCode", "purchaseMoment", "lastNibble", "price", "draftMode");
		dataset.put("travelClass", choices);
		dataset.put("flight", flightChoices.getSelected().getKey());
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}
