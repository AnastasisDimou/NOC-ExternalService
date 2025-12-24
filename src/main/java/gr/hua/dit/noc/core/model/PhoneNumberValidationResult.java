package gr.hua.dit.noc.core.model;

import java.util.Locale;

/**
 * Phone number validation/normalization result.
 */
public record PhoneNumberValidationResult(
      String raw,
      boolean valid,
      String type,
      String e164) {

   public static PhoneNumberValidationResult invalid(String raw) {
      return new PhoneNumberValidationResult(raw, false, null, null);
   }

   public static PhoneNumberValidationResult valid(String raw, String type, String e164) {
      return new PhoneNumberValidationResult(raw, true, lower(type), e164);
   }

   private static String lower(String value) {
      return value == null ? null : value.toLowerCase(Locale.ROOT);
   }
}
