
package acme.features.authenticatedcustomer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Authenticated;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.Bookings.Booking;

@GuiController
public class AuthenticatedBookingController extends AbstractGuiController<Authenticated, Booking> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedBookingListService		listService;

	@Autowired
	private AuthenticatedBookingShowService		showService;

	@Autowired
	private AuthenticatedBookingUpdateService	updateService;

	@Autowired
	private AuthenticatedBookingCreateService	createService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("create", this.createService);
	}

}
