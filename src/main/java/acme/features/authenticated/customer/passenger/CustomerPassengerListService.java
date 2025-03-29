
package acme.features.authenticated.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Passengers.Passenger;
import acme.features.authenticated.customer.booking.AuthenticatedBookingRepository;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerListService extends AbstractGuiService<Customer, Passenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedBookingRepository	bookingRepository;

	@Autowired
	private CustomerPassengerRepository		repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int customerId;
		Collection<Passenger> passengers;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();
		passengers = this.repository.findBookingByCustomer(customerId);
		status = passengers.stream().allMatch(b -> b.getCustomer().getUserAccount().getId() == customerId);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Passenger> passengers;
		int id;

		id = super.getRequest().getData("bookingId", int.class);
		passengers = this.bookingRepository.findPassengersByBooking(id);

		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email");

		super.getResponse().addData(dataset);
	}
}
