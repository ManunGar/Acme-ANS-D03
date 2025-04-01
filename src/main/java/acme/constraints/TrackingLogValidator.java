
package acme.constraints;

import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.Claims.AcceptedIndicator;
import acme.entities.Claims.ClaimRepository;
import acme.entities.TrackingLogs.TrackingLog;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	@Autowired
	private ClaimRepository claimRepository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {
		// HINT: job can be null
		assert context != null;

		boolean result;

		if (trackingLog == null)
			super.state(context, false, "*", "acme.validation.NotNull.message");
		else {

			//Validation for attribute resolution when resolutionPercentage == 100.
			{
				boolean resolutionMandatory;

				resolutionMandatory = trackingLog.getResolutionPercentage() != 100.00 || trackingLog.validResolution();	//I can do it with == because i use double and not Double

				super.state(context, resolutionMandatory, "resolution", "acme.validation.trackinLog.resolutionMandatory.message");
			}

			//Validation for attribute accepted is logical with resolutionPercentage
			{
				boolean acceptedPending;
				boolean isPending = trackingLog.getAccepted().equals(AcceptedIndicator.PENDING);
				boolean isComplete = trackingLog.getResolutionPercentage() == 100.0;

				acceptedPending = !isComplete && isPending || isComplete && !isPending;

				super.state(context, acceptedPending, "accepted", "acme.validation.trackingLog.acceptedPending.message");
			}

			//Validation of the maximum number of trackingLogs with resolutionPercentage == 100.
			{
				boolean maximumNumberOfTrackingLogsCompleted;
				List<TrackingLog> trackingLogs = this.claimRepository.findAllByClaimId(trackingLog.getClaim().getId());
				trackingLogs = trackingLogs.stream().filter(x -> x.getResolutionPercentage() == 100.00).toList();
				maximumNumberOfTrackingLogsCompleted = trackingLogs.size() <= 2 ? true : false;

				super.state(context, maximumNumberOfTrackingLogsCompleted, "numberOfTrackingLogsCompleted", "acme.validation.trackingLog.numberOfTrackingLogsCompleted.message");
			}

			//Validation of attribute draftMode is logical with its claim
			{
				boolean draftModeLogical;

				draftModeLogical = trackingLog.isDraftMode() || !trackingLog.getClaim().isDraftMode();

				super.state(context, draftModeLogical, "draftMode", "acme.validation.trackingLog.draftModeLogical.message");

			}
		}

		result = !super.hasErrors(context);

		return result;
	}

}
