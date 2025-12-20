package gr.hua.dit.noc.core.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import gr.hua.dit.noc.core.LookupService;
import gr.hua.dit.noc.core.model.UserLookupResult;
import gr.hua.dit.noc.core.model.UserType;
import jakarta.annotation.PostConstruct;

@Service
public class inMemoryLookupServiceImpl implements LookupService {

   private final Map<String, UserType> inMemoryDataBase;

   public inMemoryLookupServiceImpl() {
      this.inMemoryDataBase = new ConcurrentHashMap<>();
   }

   @PostConstruct
   public void registerInitialData() {
      // Initialize with some dummy data
      inMemoryDataBase.put("123", UserType.CITIZEN);
      inMemoryDataBase.put("345", UserType.EMPLOYEE);
      inMemoryDataBase.put("567", UserType.ADMIN);
   }

   @Override
   public Optional<UserLookupResult> lookupByGovId(String govId) {
      if (govId == null) {
         throw new NullPointerException("govId cannot be null");
      }
      if (govId.isBlank()) {
         throw new IllegalArgumentException("govId cannot be blank");
      }

      final UserType type = this.inMemoryDataBase.get(govId);
      if (type == null) {
         return Optional.empty();
      }
      final UserLookupResult userLookupResult = new UserLookupResult(govId, type);
      return Optional.of(userLookupResult);
   }
}
