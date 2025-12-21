package gr.hua.dit.noc.core.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import gr.hua.dit.noc.config.RouteeProperties;
import gr.hua.dit.noc.core.SmsService;
import gr.hua.dit.noc.core.model.SendSmsRequest;
import gr.hua.dit.noc.core.model.SendSmsResult;
import jakarta.validation.Valid;

@Service
public class RouteeSmsService implements SmsService {
   private static final Logger LOGGER = LoggerFactory.getLogger(RouteeSmsService.class);

   private static final String AUTHENTICATION_URL = "https://auth.routee.net/oauth/token";
   private static final String SMS_URL = "https://connect.routee.net/sms";

   private final RestTemplate restTemplate;
   private final RouteeProperties routeeProperties;

   public RouteeSmsService(final RestTemplate restTemplate, final RouteeProperties routeeProperties) {
      if (restTemplate == null)
         throw new NullPointerException();
      if (routeeProperties == null)
         throw new NullPointerException();

      this.restTemplate = restTemplate;
      this.routeeProperties = routeeProperties;
   }

   @SuppressWarnings("rawtypes")
   @Cacheable(value = "routeeAccessToken", unless = "#result == null")
   public String getAccessToken() {
      LOGGER.info("Requesting Routee Access Token");

      final String credentials = this.routeeProperties.getAppId() + ":" + this.routeeProperties.getAppSecret();
      final String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
      try {
         final HttpHeaders httpHeaders = new HttpHeaders();
         httpHeaders.set("Authorization", "Basic " + encoded);
         httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

         final HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", httpHeaders);

         final ResponseEntity<Map> response = this.restTemplate.exchange(AUTHENTICATION_URL, HttpMethod.POST, request,
               Map.class);

         if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.error("Routee token request failed. status={}", response.getStatusCode());
            return null;
         }

         Map body = response.getBody();
         if (body == null || body.get("access_token") == null) {
            LOGGER.error("Routee token response missing access_token. status={}", response.getStatusCode());
            return null;
         }

         return body.get("access_token").toString();
      } catch (RestClientResponseException ex) {
         // 4xx/5xx with body
         LOGGER.error("Routee token request failed. status={}, body={}",
               ex.getStatusCode().value(), ex.getResponseBodyAsString(), ex);
         return null;

      } catch (ResourceAccessException ex) {
         // network/timeout
         LOGGER.error("Routee token request failed (network). {}", ex.getMessage(), ex);
         return null;

      } catch (RestClientException ex) {
         LOGGER.error("Routee token request failed (unexpected RestClientException).", ex);
         return null;
      }
   }

   @Override
   public SendSmsResult send(@Valid SendSmsRequest request) {
      if (request == null) {
         throw new NullPointerException();
      }

      final String e164 = request.e164();
      final String content = request.content();

      if (e164 == null) {
         throw new NullPointerException();
      }

      if (e164.isBlank()) {
         throw new IllegalArgumentException();
      }

      if (content == null) {
         throw new NullPointerException();
      }

      if (content.isBlank()) {
         throw new IllegalArgumentException();
      }

      final String token = this.getAccessToken();
      if (token == null || token.isBlank()) {
         LOGGER.error("Cannot send SMS: Routee access token unavailable.");
         return new SendSmsResult(false);
      }

      try {
         final HttpHeaders httpHeaders = new HttpHeaders();
         httpHeaders.set("Authorization", "Bearer " + token);
         httpHeaders.setContentType(MediaType.APPLICATION_JSON);

         final Map<String, Object> body = Map.of("body", content, "to", e164, "from",
               this.routeeProperties.getSender());

         final HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);
         final ResponseEntity<String> response = this.restTemplate.postForEntity(SMS_URL, entity, String.class);

         LOGGER.info("Routee response: {}", response);

         if (!response.getStatusCode().is2xxSuccessful()) {
            LOGGER.error("SMS send to {} failed", e164);
            return new SendSmsResult(false);
         }

         return new SendSmsResult(true);
      } catch (RestClientResponseException ex) {
         LOGGER.error("Routee SMS request failed. status={}, body={}",
               ex.getStatusCode().value(), ex.getResponseBodyAsString(), ex);
         return new SendSmsResult(false);

      } catch (ResourceAccessException ex) {
         LOGGER.error("Routee SMS request failed (network). {}", ex.getMessage(), ex);
         return new SendSmsResult(false);

      } catch (RestClientException ex) {
         LOGGER.error("Routee SMS request failed (unexpected RestClientException).", ex);
         return new SendSmsResult(false);
      }
   }
}
