
package acme.entities.TrackingLogs;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidScore;
import acme.constraints.ValidLongText;
import acme.constraints.ValidShortText;
import acme.constraints.ValidTrackingLog;
import acme.entities.Claims.AcceptedIndicator;
import acme.entities.Claims.Claim;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidTrackingLog
public class TrackingLog extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				lastUpdateMoment;

	@Mandatory
	@ValidShortText
	@Automapped
	private String				step;

	@Mandatory
	@ValidScore
	@Automapped
	private double				resolutionPercentage;

	@Mandatory
	@Valid
	@Automapped
	private AcceptedIndicator	accepted;	//Indicator

	@Mandatory
	@Automapped
	private boolean				draftMode;

	@Optional
	@ValidLongText
	@Automapped
	private String				resolution;

	// Derived attributes -----------------------------------------------------


	@Transient
	public boolean validResolution() {
		return this.resolution != null && !this.resolution.trim().isEmpty();
	}

	// Relationships


	@Mandatory
	@Valid
	@ManyToOne
	private Claim claim;

}
