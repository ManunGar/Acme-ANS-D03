
package acme.features.assistanceAgent.TrackingLog;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Claims.Claim;
import acme.entities.TrackingLogs.TrackingLog;
import acme.realms.AssistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogListAllService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Claim> claims;
		Collection<TrackingLog> trackingLogs;
		int assistanceAgentId;

		assistanceAgentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		claims = this.repository.findClaimsByAssistanceAgentId(assistanceAgentId);

		trackingLogs = claims.stream().map(x -> this.repository.findAllTrackingLogsByclaimId(x.getId())).flatMap(Collection::stream).collect(Collectors.toList());
		trackingLogs.forEach(x -> {
			Claim claim = this.repository.findClaimByTrackingLogId(x.getId());
			x.setClaim(claim);
		});

		super.getBuffer().addData(trackingLogs);

	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "step", "resolutionPercentage", "accepted");

		dataset.put("claim", trackingLog.getClaim().getDescription());
		super.getResponse().addData(dataset);
	}

}
