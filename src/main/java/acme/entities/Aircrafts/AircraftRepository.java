
package acme.entities.Aircrafts;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import acme.client.repositories.AbstractRepository;

public interface AircraftRepository extends AbstractRepository {

	@Query("select a from Aircraft a")
	List<Aircraft> findAllAircarft();
}
