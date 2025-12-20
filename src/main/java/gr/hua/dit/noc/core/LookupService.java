package gr.hua.dit.noc.core;

import gr.hua.dit.noc.core.model.UserLookupResult;
import java.util.Optional;

public interface LookupService {
   Optional<UserLookupResult> lookupByGovId(String govId);
}
