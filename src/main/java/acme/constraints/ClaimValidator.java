
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.Claims.Claim;
import acme.entities.Legs.Legs;

@Validator
public class ClaimValidator extends AbstractValidator<ValidClaim, Claim> {

	// Internal state ---------------------------------------------------------

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
		else //Validation that Leg associated is in the past
		{
			boolean correctLeg;
			Legs leg = claim.getLeg();
			correctLeg = MomentHelper.compare(MomentHelper.getCurrentMoment(), leg.getArrival()) > 0 ? true : false;

			super.state(context, correctLeg, "leg", "acme.validation.claim.leg.message");
		}

		result = !super.hasErrors(context);

		return result;
	}

}
