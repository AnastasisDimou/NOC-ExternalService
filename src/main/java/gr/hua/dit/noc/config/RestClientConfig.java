package gr.hua.dit.noc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// provides http client
@Configuration
public class RestClientConfig {

   @Bean
   public RestTemplate restTemplate() {
      return new RestTemplate();
   }
}
