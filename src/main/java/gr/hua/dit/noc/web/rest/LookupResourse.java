package gr.hua.dit.noc.web.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gr.hua.dit.noc.core.LookupService;
import gr.hua.dit.noc.core.model.UserLookupResult;

@RestController
@RequestMapping("/api/v1/lookups")
public class LookupResourse {

   private final LookupService lookupService;

   public LookupResourse(LookupService lookupService) {
      if (lookupService == null) {
         throw new NullPointerException("lookupService cannot be null");
      }
      this.lookupService = lookupService;
   }

   @GetMapping("/{govId}")
   public ResponseEntity<?> lookups(@PathVariable String govId) {
      final Optional<UserLookupResult> userLookupResultOptional = this.lookupService.lookupByGovId(govId);
      if (userLookupResultOptional.isEmpty()) {
         final Map<String, String> message = new HashMap<>(2);
         message.put("message", "User not found");
         message.put("govId", govId);
         return ResponseEntity.status(404).body(message);
      }
      return ResponseEntity.ok(userLookupResultOptional.get());
   }
}
