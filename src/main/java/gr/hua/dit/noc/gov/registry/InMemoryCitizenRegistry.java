package gr.hua.dit.noc.gov.registry;

import gr.hua.dit.noc.gov.model.Citizen;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class InMemoryCitizenRegistry implements CitizenRegistry {

   private final Map<String, Citizen> citizensByAfm = new ConcurrentHashMap<>();
   private final Map<String, Citizen> citizensById = new ConcurrentHashMap<>();

   @PostConstruct
   public void registerInitialData() {
      register(new Citizen("1", "123456789", "01019912345", "Nikos", "Papadopoulos", "1111"));
      register(new Citizen("2", "987654321", "02028898765", "Eleni", "Georgiou", "2222"));
      register(new Citizen("3", "555555555", "15057855555", "Maria", "Ioannou", "3333"));
   }

   @Override
   public Optional<Citizen> findByAfm(String afm) {
      if (afm == null) {
         return Optional.empty();
      }
      return Optional.ofNullable(this.citizensByAfm.get(afm));
   }

   @Override
   public Optional<Citizen> findById(String id) {
      if (id == null) {
         return Optional.empty();
      }
      return Optional.ofNullable(this.citizensById.get(id));
   }

   private void register(Citizen citizen) {
      if (citizen == null) {
         return;
      }
      this.citizensByAfm.put(citizen.afm(), citizen);
      this.citizensById.put(citizen.id(), citizen);
   }
}
