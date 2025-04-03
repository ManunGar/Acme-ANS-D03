
package acme.features.technicians.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Aircrafts.Aircraft;
import acme.entities.MaintenanceRecords.MaintenanceRecord;
import acme.entities.MaintenanceRecords.MaintenanceRecordTask;
import acme.realms.Technician;

@Repository
public interface TechnicianMaintenanceRecordRepository extends AbstractRepository {

	@Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.draftMode = false")
	Collection<MaintenanceRecord> findPublishedMaintenanceRecords();

	@Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.technician.id = :id")
	Collection<MaintenanceRecord> findMaintenanceRecordsByTechnicianId(int id);

	@Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);

	@Query("SELECT a FROM Aircraft a WHERE a.id = :id")
	Aircraft findAircraftById(int id);

	@Query("SELECT a FROM Aircraft a")
	Collection<Aircraft> findAllAircrafts();

	@Query("SELECT mrt FROM MaintenanceRecordTask mrt WHERE mrt.maintenanceRecord.id = :id")
	Collection<MaintenanceRecordTask> findMaintenanceRecordTasksFromMaintenanceRecordId(int id);

	@Query("SELECT count(mrt.task) FROM MaintenanceRecordTask mrt WHERE mrt.maintenanceRecord.id = :id")
	int findTasksByMaintenanceRecordId(int id);

	@Query("SELECT count(mrt.task) FROM MaintenanceRecordTask mrt WHERE mrt.maintenanceRecord.id = :id AND mrt.task.draftMode = true")
	int findNotPublishedTasksByMaintenanceRecordId(int id);

	@Query("SELECT t FROM Technician t WHERE t.id = :id")
	Technician findTechnicianById(int id);

	@Query("SELECT t FROM Technician t")
	Collection<Technician> findAllTechnicians();
}
