
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Claims.Claim;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("select j from Claim j where j.assistanceAgent.id = :assistanceAgentId")
	Collection<Claim> findClaimsByAssistanceAgentId(int assistanceAgentId);

	@Query("select j from Claim j where j.assistanceAgent.id = :assistanceAgentId and j.accepted <> acme.entities.Claims.AcceptedIndicator.PENDING")
	Collection<Claim> findClaimsByAssistanceAgentIdResolved(int assistanceAgentId);

	@Query("select j from Claim j where j.assistanceAgent.id = :assistanceAgentId and j.accepted = acme.entities.Claims.AcceptedIndicator.PENDING")
	Collection<Claim> findClaimsByAssistanceAgentIdNotResolved(int assistanceAgentId);

}
