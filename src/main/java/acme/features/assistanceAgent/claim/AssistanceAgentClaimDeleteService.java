
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Claims.Claim;
import acme.entities.Claims.ClaimTypes;
import acme.entities.Legs.Legs;
import acme.entities.TrackingLogs.TrackingLog;
import acme.realms.AssistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimDeleteService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int claimId;
		Claim claim;
		AssistanceAgent assistanceAgent;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(claimId);
		assistanceAgent = claim == null ? null : claim.getAssistanceAgent();
		status = super.getRequest().getPrincipal().hasRealm(assistanceAgent) && claim != null;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		int claimId;
		Legs leg;

		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(claimId);

		leg = this.repository.findLegByClaimId(claimId);

		claim.setLeg(leg);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		int legId;
		Legs leg;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(claim, "passengerEmail", "description", "claimType");
		claim.setLeg(leg);

	}

	@Override
	public void validate(final Claim claim) {
		;
	}

	@Override
	public void perform(final Claim claim) {
		Collection<TrackingLog> trackingLogs;

		trackingLogs = this.repository.findTrackingLogsByClaimId(claim.getId());
		this.repository.deleteAll(trackingLogs);
		this.repository.delete(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Collection<Legs> legs;
		SelectChoices typesChoices;
		SelectChoices legsChoices;
		Dataset dataset;

		legs = this.repository.findAvailableLegs(MomentHelper.getCurrentMoment());
		legsChoices = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		typesChoices = SelectChoices.from(ClaimTypes.class, claim.getClaimType());

		dataset = super.unbindObject(claim, "passengerEmail", "description", "claimType");

		dataset.put("accepted", claim.accepted());
		dataset.put("leg", claim.getLeg());
		dataset.put("legs", legsChoices);
		dataset.put("claimTypes", typesChoices);

		super.getResponse().addData(dataset);
	}

}
