package gr.hua.dit.noc.gov.api;

import jakarta.validation.constraints.NotBlank;

public record GovLoginRequest(
      @NotBlank String afm,
      @NotBlank String pin) {
}
