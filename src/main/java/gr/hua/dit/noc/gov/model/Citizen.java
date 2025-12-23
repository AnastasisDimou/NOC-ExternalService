package gr.hua.dit.noc.gov.model;

/**
 * Simple immutable citizen representation kept in-memory.
 */
public record Citizen(
      String id,
      String afm,
      String amka,
      String firstName,
      String lastName,
      String pin) {
}
