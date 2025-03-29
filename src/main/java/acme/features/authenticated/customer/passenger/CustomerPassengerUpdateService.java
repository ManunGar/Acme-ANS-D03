
package acme.features.authenticated.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Passengers.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerUpdateService extends AbstractGuiService<Customer, Passenger> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	// AbstractGuiService interfaced ------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int customerId;
		Passenger passenger;
		int id;

		id = super.getRequest().getData("id", int.class);
		customerId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();
		passenger = this.repository.findPassengerById(id);
		status = passenger.getCustomer().getUserAccount().getId() == customerId;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Passenger passenger;
		int id;

		id = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(id);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {

		super.bindObject(passenger, "fullName", "email", "passport", "dateOfBirth", "draftMode", "specialNeeds");
	}

	@Override
	public void validate(final Passenger passenger) {
		if (passenger.isDraftMode() == false)
			super.state(false, "draftMode", "acme.validation.confirmation.message.update");

	}

	@Override
	public void perform(final Passenger passenger) {
		this.repository.save(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "fullName", "email", "passport", "dateOfBirth", "draftMode", "specialNeeds");

		super.getResponse().addData(dataset);
	}
}
