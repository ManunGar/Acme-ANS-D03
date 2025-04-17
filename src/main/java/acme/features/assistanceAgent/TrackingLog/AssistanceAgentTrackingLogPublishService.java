
package acme.features.assistanceAgent.TrackingLog;

import java.util.Collection;

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
public class AssistanceAgentTrackingLogPublishService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Claim claim;
		int trackingLogId;
		TrackingLog trackingLog;

		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(trackingLogId);

		claim = this.repository.findClaimByTrackingLogId(trackingLogId);
		trackingLog.setClaim(claim);

		super.getBuffer().addData(trackingLog);

	}

	@Override
	public void bind(final TrackingLog trackingLog) {

		super.bindObject(trackingLog, "step", "resolutionPercentage", "accepted", "resolution");

	}

	@Override
	public void validate(final TrackingLog trackingLog) {

		if (trackingLog.getClaim().isDraftMode() == true)
			super.state(false, "draftMode", "acme.validation.confirmation.message.trackingLog.claim.notPublished");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		trackingLog.setDraftMode(false);

		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Collection<Claim> claimsOfThisAssistanceAgent;
		SelectChoices claimChoices;
		int assistanceAgentId;
		SelectChoices statusChoices;
		boolean claimDraftMode;
		Dataset dataset;

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		claimsOfThisAssistanceAgent = this.repository.findClaimsByAssistanceAgentId(assistanceAgentId);
		claimChoices = SelectChoices.from(claimsOfThisAssistanceAgent, "id", trackingLog.getClaim());

		statusChoices = SelectChoices.from(AcceptedIndicator.class, trackingLog.getAccepted());

		claimDraftMode = trackingLog.getClaim().isDraftMode();

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "accepted", "draftMode", "resolution", "createdMoment", "secondTrackingLog");
		dataset.put("claim", trackingLog.getClaim().getDescription());
		dataset.put("status", statusChoices);
		dataset.put("claims", claimChoices);
		dataset.put("readOnlyClaim", true);
		dataset.put("claimDraftMode", claimDraftMode);
		dataset.put("secondTrackingLogReadOnly", true);

		super.getResponse().addData(dataset);

	}

}
