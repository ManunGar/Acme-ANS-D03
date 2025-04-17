
package acme.features.assistanceAgent.TrackingLog;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Claims.AcceptedIndicator;
import acme.entities.Claims.Claim;
import acme.entities.TrackingLogs.TrackingLog;
import acme.realms.AssistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;

		trackingLog = new TrackingLog();
		trackingLog.setAccepted(AcceptedIndicator.PENDING);
		trackingLog.setDraftMode(true);
		trackingLog.setResolutionPercentage(0.);
		trackingLog.setSecondTrackingLog(false);

		super.getBuffer().addData(trackingLog);

	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		int claimId;
		Claim claim;
		AcceptedIndicator accepted;

		claimId = super.getRequest().getData("claim", int.class);
		claim = this.repository.findClaimById(claimId);

		accepted = super.getRequest().getData("accepted", AcceptedIndicator.class);

		accepted = accepted == null ? AcceptedIndicator.PENDING : accepted;

		super.bindObject(trackingLog, "step", "resolutionPercentage", "resolution", "secondTrackingLog");
		trackingLog.setAccepted(accepted);
		trackingLog.setClaim(claim);
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		trackingLog.setCreatedMoment(MomentHelper.getCurrentMoment());

	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		if (this.repository.findClaimById(super.getRequest().getData("claim", int.class)) == null)
			super.state(false, "claim", "acme.validation.confirmation.message.trackingLog.claim");

		if (trackingLog.getResolutionPercentage() == 100.00 && trackingLog.isSecondTrackingLog()) {

			List<TrackingLog> trackingLogs = this.repository.findAllTrackingLogsByclaimId(super.getRequest().getData("claim", int.class)).stream().filter(x -> x.getResolutionPercentage() == 100.00).filter(x -> x.isDraftMode() == false).toList();
			if (trackingLogs.isEmpty())
				super.state(false, "secondTrackingLog", "acme.validation.confirmation.message.trackingLog.condition");
		}

	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		trackingLog.setCreatedMoment(MomentHelper.getCurrentMoment());

		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Collection<Claim> claimsOfThisAssistanceAgent;
		SelectChoices claimChoices;
		int assistanceAgentId;
		SelectChoices statusChoices;
		Dataset dataset;

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		claimsOfThisAssistanceAgent = this.repository.findClaimsByAssistanceAgentId(assistanceAgentId);
		claimChoices = SelectChoices.from(claimsOfThisAssistanceAgent, "description", trackingLog.getClaim());

		statusChoices = SelectChoices.from(AcceptedIndicator.class, trackingLog.getAccepted());

		dataset = super.unbindObject(trackingLog, "step", "resolutionPercentage", "accepted", "resolution", "createdMoment", "secondTrackingLog", "claim");
		dataset.put("status", statusChoices);
		dataset.put("claims", claimChoices);
		dataset.put("secondTrackingLogReadOnly", false);

		super.getResponse().addData(dataset);

	}

}
