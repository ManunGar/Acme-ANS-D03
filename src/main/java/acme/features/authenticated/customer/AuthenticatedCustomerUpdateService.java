
package acme.features.authenticated.customer;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.realms.Customer;

@GuiService
public class AuthenticatedCustomerUpdateService extends AbstractGuiService<Authenticated, Customer> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedCustomerRepository repository;

	// AbstractService interface ----------------------------------------------ç


	@Override
	public void authorise() {
		boolean status;
		int userAccountId;
		Customer object;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		object = this.repository.findCustomerByUserAccountId(userAccountId);
		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class) && super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId() == object.getUserAccount().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Customer object;
		int userAccountId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		object = this.repository.findCustomerByUserAccountId(userAccountId);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Customer object) {
		assert object != null;

		super.bindObject(object, "identifier", "phoneNumber", "physicalAddress", "city", "country", "earnedPoints");
	}

	@Override
	public void validate(final Customer object) {
		assert object != null;
	}

	@Override
	public void perform(final Customer object) {
		assert object != null;
		int userAccountId;
		Customer c;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		c = this.repository.findCustomerByUserAccountId(userAccountId);
		c.setIdentifier(object.getIdentifier());
		c.setPhoneNumber(object.getPhoneNumber());
		c.setPhysicalAddress(object.getPhysicalAddress());
		c.setCity(object.getCity());
		c.setCountry(object.getCountry());
		this.repository.save(c);
	}

	@Override
	public void unbind(final Customer object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbindObject(object, "identifier", "phoneNumber", "physicalAddress", "city", "country", "earnedPoints");
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}
