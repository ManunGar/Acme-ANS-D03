
package acme.entities.Legs;

import java.time.Duration;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidLegs;
import acme.entities.Aircrafts.Aircraft;
import acme.entities.Airports.Airport;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidLegs
public class Legs extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{3}\\d{4}$")
	@Column(unique = true)
	private String				flightNumber;

	@Mandatory
	@ValidMoment(past = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				departure;

	@Mandatory
	@ValidMoment(past = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				arrival;

	@Mandatory
	@Valid
	@Automapped
	private LegsStatus			status;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport				departureAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport				arrivalAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Aircraft			aircraft;


	@Transient
	private Double getDuration() {
		double duration = Duration.between(this.getDeparture().toInstant(), this.getArrival().toInstant()).toMinutes() / 60.0;
		return duration;
	}

}
