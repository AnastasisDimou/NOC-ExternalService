package gr.hua.dit.noc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "routee")
@Validated
public class RouteeProperties {
   @NotBlank
   private String appId;
   @NotBlank
   private String appSecret;
   @NotBlank
   private String sender;

   public String getAppId() {
      return appId;
   }

   public void setAppId(String appId) {
      this.appId = appId;
   }

   public String getAppSecret() {
      return appSecret;
   }

   public void setAppSecret(String appSecret) {
      this.appSecret = appSecret;
   }

   public String getSender() {
      return sender;
   }

   public void setSender(String sender) {
      this.sender = sender;
   }
}
