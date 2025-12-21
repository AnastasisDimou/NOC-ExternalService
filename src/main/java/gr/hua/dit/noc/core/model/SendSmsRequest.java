package gr.hua.dit.noc.core.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendSmsRequest(
      @NotNull @NotBlank String e164,
      @NotNull @NotBlank String content) {
}