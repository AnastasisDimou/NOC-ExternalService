package gr.hua.dit.noc.gov.error;

public class GovBadRequestException extends RuntimeException {
   public GovBadRequestException(String message) {
      super(message);
   }
}
