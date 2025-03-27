
package acme.features.assistanceAgent.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.Claims.Claim;
import acme.realms.AssistanceAgent.AssistanceAgent;

@GuiController
public class AssistanceAgentClaimController extends AbstractGuiController<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimListService				listService;

	@Autowired
	private AssistanceAgentClaimListResolvedService		listResolvedService;

	@Autowired
	private AssistanceAgentClaimListNotResolvedService	listNotResolvedService;


	@PostConstruct
	protected void initialise() {

		super.addBasicCommand("list", this.listService);

		super.addCustomCommand("listResolved", "list", this.listResolvedService);
		super.addCustomCommand("listNotResolved", "list", this.listNotResolvedService);

	}

}
