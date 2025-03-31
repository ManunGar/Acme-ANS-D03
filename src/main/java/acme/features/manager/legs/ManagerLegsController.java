
package acme.features.manager.legs;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.Legs.Legs;
import acme.realms.AirlineManager;

@GuiController
public class ManagerLegsController extends AbstractGuiController<AirlineManager, Legs> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegsListService		listService;

	@Autowired
	private ManagerLegsShowService		showService;

	@Autowired
	private ManagerLegsUpdateService	updateService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("update", this.updateService);
	}

}
