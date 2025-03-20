
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.entities.TrackingLogs.TrackingLog;

public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

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
			boolean resolutionMandatory;

			resolutionMandatory = trackingLog.getResolutionPercentage() != 100.00 || trackingLog.validResolution();	//I can do it with == because i use double and not Double

			super.state(context, resolutionMandatory, "resolution", "acme.validation.trackinLog.resolutionMandatory.message");

		}

		result = !super.hasErrors(context);

		return result;
	}

}
