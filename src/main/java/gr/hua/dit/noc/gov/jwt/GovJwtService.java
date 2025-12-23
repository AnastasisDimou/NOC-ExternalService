package gr.hua.dit.noc.gov.jwt;

import gr.hua.dit.noc.gov.error.GovAuthException;
import gr.hua.dit.noc.gov.model.Citizen;
import gr.hua.dit.noc.gov.registry.CitizenRegistry;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GovJwtService {

   private static final String SUBJECT_PREFIX = "citizen:";

   private final GovJwtProperties properties;
   private final CitizenRegistry citizenRegistry;
   private final Key signingKey;

   public GovJwtService(GovJwtProperties properties, CitizenRegistry citizenRegistry) {
      if (properties == null) {
         throw new NullPointerException("properties cannot be null");
      }
      if (citizenRegistry == null) {
         throw new NullPointerException("citizenRegistry cannot be null");
      }
      if (!StringUtils.hasText(properties.getSecret())) {
         throw new IllegalArgumentException("noc.gov.jwt.secret must not be blank");
      }

      this.properties = properties;
      this.citizenRegistry = citizenRegistry;
      this.signingKey = buildKey(properties.getSecret());
   }

   public IssuedToken issueToken(Citizen citizen) {
      if (citizen == null) {
         throw new NullPointerException("citizen cannot be null");
      }
      final Instant now = Instant.now();
      final Instant exp = now.plusSeconds(this.properties.getTtlSeconds());

      final String token = Jwts.builder()
            .setSubject(SUBJECT_PREFIX + citizen.id())
            .setIssuer(this.properties.getIssuer())
            .setAudience(this.properties.getAudience())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(exp))
            .signWith(this.signingKey, SignatureAlgorithm.HS256)
            .compact();

      return new IssuedToken(token, exp);
   }

   public Citizen validateAndResolve(String token) {
      final Claims claims = parseClaims(token);
      final String subject = claims.getSubject();
      if (!StringUtils.hasText(subject) || !subject.startsWith(SUBJECT_PREFIX)) {
         throw new GovAuthException("Invalid token subject");
      }

      final String citizenId = subject.substring(SUBJECT_PREFIX.length());
      return this.citizenRegistry.findById(citizenId)
            .orElseThrow(() -> new GovAuthException("Citizen not found"));
   }

   private Claims parseClaims(String token) {
      if (!StringUtils.hasText(token)) {
         throw new GovAuthException("Missing token");
      }
      try {
         return Jwts.parserBuilder()
               .setSigningKey(this.signingKey)
               .requireIssuer(this.properties.getIssuer())
               .requireAudience(this.properties.getAudience())
               .build()
               .parseClaimsJws(token)
               .getBody();
      } catch (JwtException | IllegalArgumentException ex) {
         throw new GovAuthException("Invalid or expired token");
      }
   }

   private Key buildKey(String secret) {
      final byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
      if (secretBytes.length < 32) {
         // try to decode base64 secrets, otherwise enforce length requirement
         byte[] decoded = Decoders.BASE64.decode(secret);
         if (decoded.length < 32) {
            throw new IllegalArgumentException("noc.gov.jwt.secret must be at least 32 bytes");
         }
         return Keys.hmacShaKeyFor(decoded);
      }
      return Keys.hmacShaKeyFor(secretBytes);
   }
}
