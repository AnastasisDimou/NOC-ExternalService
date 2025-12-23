package gr.hua.dit.noc.gov.api;

import gr.hua.dit.noc.gov.error.GovAuthException;
import gr.hua.dit.noc.gov.error.GovBadRequestException;
import gr.hua.dit.noc.gov.jwt.GovJwtService;
import gr.hua.dit.noc.gov.jwt.IssuedToken;
import gr.hua.dit.noc.gov.model.Citizen;
import gr.hua.dit.noc.gov.registry.CitizenRegistry;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/gov", produces = MediaType.APPLICATION_JSON_VALUE)
public class GovController {

   private final CitizenRegistry citizenRegistry;
   private final GovJwtService govJwtService;

   public GovController(CitizenRegistry citizenRegistry, GovJwtService govJwtService) {
      if (citizenRegistry == null) {
         throw new NullPointerException("citizenRegistry cannot be null");
      }
      if (govJwtService == null) {
         throw new NullPointerException("govJwtService cannot be null");
      }
      this.citizenRegistry = citizenRegistry;
      this.govJwtService = govJwtService;
   }

   @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<GovLoginResponse> login(@RequestBody @Valid GovLoginRequest request) {
      final Citizen citizen = this.citizenRegistry.findByAfm(request.afm())
            .filter(c -> c.pin().equals(request.pin()))
            .orElseThrow(() -> new GovAuthException("Invalid credentials"));

      final IssuedToken issuedToken = this.govJwtService.issueToken(citizen);
      final CitizenDto citizenDto = toDto(citizen);
      final GovLoginResponse response = new GovLoginResponse(issuedToken.token(), issuedToken.expiresAt(), citizenDto);
      return ResponseEntity.ok(response);
   }

   @GetMapping("/identity")
   public ResponseEntity<GovIdentityResponse> identity(
         @RequestHeader(name = "Authorization", required = false) String authorizationHeader) {
      final String token = extractBearerToken(authorizationHeader);
      final Citizen citizen = this.govJwtService.validateAndResolve(token);
      final CitizenDto citizenDto = toDto(citizen);
      final GovIdentityResponse response = new GovIdentityResponse(
            citizenDto.afm(),
            citizenDto.amka(),
            citizenDto.firstName(),
            citizenDto.lastName());
      return ResponseEntity.ok(response);
   }

   private String extractBearerToken(String authorizationHeader) {
      if (!StringUtils.hasText(authorizationHeader)) {
         throw new GovAuthException("Missing Authorization header");
      }
      final String prefix = "Bearer ";
      if (authorizationHeader.startsWith(prefix)) {
         return authorizationHeader.substring(prefix.length());
      }
      if (authorizationHeader.startsWith(prefix.toLowerCase())) {
         return authorizationHeader.substring(prefix.length());
      }
      throw new GovBadRequestException("Authorization header must start with Bearer");
   }

   private CitizenDto toDto(Citizen citizen) {
      return new CitizenDto(citizen.afm(), citizen.amka(), citizen.firstName(), citizen.lastName());
   }
}
