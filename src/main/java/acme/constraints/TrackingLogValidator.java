
package acme.constraints;

import java.util.Collection;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.Claims.AcceptedIndicator;
import acme.entities.Claims.ClaimRepository;
import acme.entities.TrackingLogs.TrackingLog;
import acme.entities.TrackingLogs.TrackingLogRepository;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	@Autowired
	private ClaimRepository			claimRepository;

	@Autowired
	private TrackingLogRepository	trackingLogRepository;

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
				if (trackingLog.getClaim() != null) {
					boolean maximumNumberOfTrackingLogsCompleted;
					List<TrackingLog> trackingLogs = this.claimRepository.findAllByClaimId(trackingLog.getClaim().getId());
					trackingLogs = trackingLogs.stream().filter(x -> x.getResolutionPercentage() == 100.00).filter(x -> x.getId() != trackingLog.getId()).toList();
					maximumNumberOfTrackingLogsCompleted = trackingLog.getResolutionPercentage() != 100.00 ? true : trackingLogs.size() <= 1 ? true : false;

					super.state(context, maximumNumberOfTrackingLogsCompleted, "claim", "acme.validation.trackingLog.numberOfTrackingLogsCompleted.message");
				}

			}

			//Validation of attribute draftMode is logical with its claim
			{
				if (trackingLog.getClaim() != null) {
					boolean draftModeLogical;

					draftModeLogical = trackingLog.isDraftMode() || !trackingLog.getClaim().isDraftMode();

					super.state(context, draftModeLogical, "draftMode", "acme.validation.trackingLog.draftModeLogical.message");
				}

			}

			//Validation of attribute resolutionPercentage is always higher than the last created
			{
				if (trackingLog.getClaim() != null) {

					boolean resolutionPercentageHigher;

					TrackingLog existingTrackingLog = this.trackingLogRepository.findTrackingLogById(trackingLog.getId());
					if (existingTrackingLog == null) {

						Collection<TrackingLog> trackingLogs = this.trackingLogRepository.findTrackingLogsOrderedByLastUpdateMoment(trackingLog.getClaim().getId());
						TrackingLog lastTrackingLog = trackingLogs.size() > 0 ? trackingLogs.stream().toList().get(0) : null;
						resolutionPercentageHigher = lastTrackingLog == null ? true : lastTrackingLog.getResolutionPercentage() <= trackingLog.getResolutionPercentage();

					} else {

						Collection<TrackingLog> trackingLogs = this.trackingLogRepository.findTrackingLogsOrderedByLastUpdateMoment(trackingLog.getClaim().getId());
						List<TrackingLog> listTrackingLogs = trackingLogs.stream().toList();
						int indexOfLastTrackingLog = listTrackingLogs.indexOf(trackingLog) + 1;
						if (indexOfLastTrackingLog >= listTrackingLogs.size())
							resolutionPercentageHigher = true;
						else {
							TrackingLog lastTrackingLog = listTrackingLogs.size() > 0 ? listTrackingLogs.get(indexOfLastTrackingLog) : null;
							boolean resolutionPercentageHigherThanTheLast = lastTrackingLog == null ? true : lastTrackingLog.getResolutionPercentage() <= trackingLog.getResolutionPercentage();
							TrackingLog thisTrackingLogBeforeUpdate = listTrackingLogs.size() > 0 ? listTrackingLogs.get(indexOfLastTrackingLog - 1) : null;
							boolean resolutionPercentageHigherThanThisBeforeUpdate = thisTrackingLogBeforeUpdate == null ? true : thisTrackingLogBeforeUpdate.getResolutionPercentage() <= trackingLog.getResolutionPercentage();
							resolutionPercentageHigher = resolutionPercentageHigherThanTheLast && resolutionPercentageHigherThanThisBeforeUpdate;
						}

					}

					super.state(context, resolutionPercentageHigher, "resolutionPercentage", "acme.validation.trackingLog.resolutionPercentage.message");

				}

			}
		}

		result = !super.hasErrors(context);

		return result;
	}

}
