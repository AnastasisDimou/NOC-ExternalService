package gr.hua.dit.noc.gov.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gr.hua.dit.noc.gov.model.Citizen;
import gr.hua.dit.noc.gov.registry.InMemoryCitizenRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GovJwtServiceTest {

   private GovJwtService govJwtService;
   private Citizen citizen;

   @BeforeEach
   void setUp() {
      final GovJwtProperties properties = new GovJwtProperties();
      properties.setSecret("abcdefghijklmnopqrstuvwxyz123456");
      properties.setIssuer("noc-mock-gov");
      properties.setAudience("mycitygov");
      properties.setTtlSeconds(300);

      final InMemoryCitizenRegistry registry = new InMemoryCitizenRegistry();
      registry.registerInitialData();

      this.govJwtService = new GovJwtService(properties, registry);
      this.citizen = registry.findByAfm("123456789").orElseThrow();
   }

   @Test
   void shouldIssueAndValidateToken() {
      final IssuedToken issued = govJwtService.issueToken(citizen);

      assertNotNull(issued);
      assertNotNull(issued.token());
      assertNotNull(issued.expiresAt());

      final Citizen resolved = govJwtService.validateAndResolve(issued.token());
      assertEquals(citizen.afm(), resolved.afm());
   }
}
