
package acme.features.anyPrincipals;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Services.Service;

@Repository
public interface AnyPrincipalsServiceRepository extends AbstractRepository {

	@Query("select s from Service s")
	Collection<Service> findAllServices();

	@Query("select s from Service s where s.id = :id")
	Service findServiceById(int id);

	@Query(value = "SELECT * FROM service ORDER BY RAND() LIMIT 1", nativeQuery = true)
	Service findRandomService();

}
