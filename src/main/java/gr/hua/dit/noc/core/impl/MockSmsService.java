package gr.hua.dit.noc.core.impl;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import gr.hua.dit.noc.core.SmsService;
import gr.hua.dit.noc.core.model.SendSmsRequest;
import gr.hua.dit.noc.core.model.SendSmsResult;

@Service
public class MockSmsService implements SmsService {

   private static final Logger LOGGER = LoggerFactory.getLogger(MockSmsService.class);

   @Override
   public SendSmsResult send(SendSmsRequest request) {
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

      LOGGER.info("SENDING SMS {} {}", e164, content);

      return new SendSmsResult(false);
   }
}
