
package acme.entities.Flight;

import java.beans.Transient;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidLongText;
import acme.constraints.ValidShortText;
import acme.entities.Legs.LegRepository;
import acme.realms.AirlineManager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flight extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	// Attributes

	@Mandatory
	@Automapped
	private Boolean				draftMode;

	@Mandatory
	@ValidShortText
	@Automapped
	private String				highlights;

	@Mandatory
	@Automapped
	private Boolean				selfTransfer;

	@Mandatory
	@ValidMoney(min = 0, max = 1000000)
	@Automapped
	private Money				cost;

	@Optional
	@ValidLongText
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private AirlineManager		manager;


	@Transient
	public Date getDeparture() {
		Date result;
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		result = repository.findDepartureByFlightId(this.getId());

		return result;
	}

	@Transient
	public Date getArrival() {
		Date result;
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		result = repository.findArrivalByFlightId(this.getId());

		return result;
	}

	@Transient
	public String getOrigin() {
		String result;
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		result = repository.findOriginCityByFlightId(this.getId()).get(0);

		return result;
	}

	@Transient
	public String getDestination() {
		String result;
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		result = repository.findDestinationCityByFlightId(this.getId()).get(-1);

		return result;
	}

	@Transient
	public Integer getLayovers() {
		Integer result;
		LegRepository repository = SpringHelper.getBean(LegRepository.class);
		result = repository.numberOfLayours(this.getId());

		return result;
	}

}
