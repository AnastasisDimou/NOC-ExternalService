package gr.hua.dit.noc.gov.registry;

import gr.hua.dit.noc.gov.model.Citizen;
import java.util.Optional;

public interface CitizenRegistry {

   Optional<Citizen> findByAfm(String afm);

   Optional<Citizen> findById(String id);
}
