package gr.hua.dit.noc.gov.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GovExceptionHandler {

   @ExceptionHandler(GovAuthException.class)
   public ResponseEntity<GovErrorResponse> handleAuth(GovAuthException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new GovErrorResponse("UNAUTHORIZED", ex.getMessage()));
   }

   @ExceptionHandler(GovBadRequestException.class)
   public ResponseEntity<GovErrorResponse> handleBadRequest(GovBadRequestException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new GovErrorResponse("BAD_REQUEST", ex.getMessage()));
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<GovErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new GovErrorResponse("BAD_REQUEST", "Invalid request payload"));
   }
}
