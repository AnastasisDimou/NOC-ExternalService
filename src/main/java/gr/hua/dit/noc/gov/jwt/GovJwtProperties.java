package gr.hua.dit.noc.gov.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "noc.gov.jwt")
@Validated
public class GovJwtProperties {

   @NotBlank
   private String secret;

   @NotBlank
   private String issuer;

   @NotBlank
   private String audience;

   @Positive
   private long ttlSeconds;

   public String getSecret() {
      return secret;
   }

   public void setSecret(String secret) {
      this.secret = secret;
   }

   public String getIssuer() {
      return issuer;
   }

   public void setIssuer(String issuer) {
      this.issuer = issuer;
   }

   public String getAudience() {
      return audience;
   }

   public void setAudience(String audience) {
      this.audience = audience;
   }

   public long getTtlSeconds() {
      return ttlSeconds;
   }

   public void setTtlSeconds(long ttlSeconds) {
      this.ttlSeconds = ttlSeconds;
   }
}
