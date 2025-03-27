
package acme.features.authenticated.customer.booking;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.Bookings.Booking;
import acme.realms.Customer;

@GuiController
public class AuthenticatedBookingController extends AbstractGuiController<Customer, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedBookingListService			listService;

	@Autowired
	private AuthenticatedBookingShowService			showService;

	@Autowired
	private AuthenticatedBookingUpdateService		updateService;

	@Autowired
	private AuthenticatedBookingCreateService		createService;

	@Autowired
	private AuthenticatedBookingPublishedService	publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("create", this.createService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
