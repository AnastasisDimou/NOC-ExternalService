package gr.hua.dit.noc.gov.api;

import java.time.Instant;

public record GovLoginResponse(
      String token,
      Instant expiresAt,
      CitizenDto citizen) {
}
