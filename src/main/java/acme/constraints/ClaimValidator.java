
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.MomentHelper;
import acme.entities.Claims.Claim;
import acme.entities.Claims.ClaimRepository;
import acme.entities.TrackingLogs.TrackingLog;

public class ClaimValidator extends AbstractValidator<ValidClaim, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ClaimRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidClaim annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Claim claim, final ConstraintValidatorContext context) {
		// HINT: job can be null
		assert context != null;

		boolean result;

		if (claim == null)
			super.state(context, false, "*", "acme.validation.NotNull.message");
		else {
			boolean correctIndicator;
			List<TrackingLog> trackingLogs = this.repository.findAllByClaimId(claim.getId());
			//TrackingLogs with ResolutionPercentage = 100.
			trackingLogs = trackingLogs.stream().filter(x -> x.getResolutionPercentage() == 100.00).toList();
			if (trackingLogs.isEmpty())
				correctIndicator = true;
			else if (trackingLogs.size() == 1)
				correctIndicator = trackingLogs.get(0).isAccepted() == claim.isAccepted();
			else {
				Integer moment = MomentHelper.compare(trackingLogs.get(0).getLastUpdateMoment(), trackingLogs.get(1).getLastUpdateMoment());
				TrackingLog lastTrackingLogUpdated = moment < 0 ? trackingLogs.get(0) : trackingLogs.get(1);
				correctIndicator = lastTrackingLogUpdated.isAccepted() == claim.isAccepted();
			}

			super.state(context, correctIndicator, "indicator", "acme.validation.claim.indicator.message");
		}

		result = !super.hasErrors(context);

		return result;
	}

}
