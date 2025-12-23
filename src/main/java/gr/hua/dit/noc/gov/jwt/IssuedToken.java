package gr.hua.dit.noc.gov.jwt;

import java.time.Instant;

public record IssuedToken(String token, Instant expiresAt) {
}
