
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.helpers.MomentHelper;
import acme.entities.Claims.AcceptedIndicator;
import acme.entities.Claims.Claim;
import acme.entities.Claims.ClaimRepository;
import acme.entities.Legs.Legs;
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
				correctIndicator = claim.getAccepted().equals(AcceptedIndicator.PENDING);
			else if (trackingLogs.size() == 1)
				correctIndicator = trackingLogs.get(0).getAccepted().equals(claim.getAccepted()) && !claim.getAccepted().equals(AcceptedIndicator.PENDING);
			else {
				Integer moment = MomentHelper.compare(trackingLogs.get(0).getLastUpdateMoment(), trackingLogs.get(1).getLastUpdateMoment());
				TrackingLog lastTrackingLogUpdated = moment < 0 ? trackingLogs.get(0) : trackingLogs.get(1);
				correctIndicator = lastTrackingLogUpdated.getAccepted().equals(claim.getAccepted()) && !claim.getAccepted().equals(AcceptedIndicator.PENDING);
			}

			super.state(context, correctIndicator, "indicator", "acme.validation.claim.indicator.message");

			boolean correctLeg;
			Legs leg = claim.getLegWhichRequestOrComplain();
			correctLeg = MomentHelper.compare(MomentHelper.getCurrentMoment(), leg.getArrival()) >= 0 ? true : false;

			super.state(context, correctLeg, "leg", "acme.validation.claim.leg.message");
		}

		result = !super.hasErrors(context);

		return result;
	}

}
