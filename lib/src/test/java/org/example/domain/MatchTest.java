package org.example.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MatchTest {

  @Test
  void shouldThrowException_nullProvidedForRequiredProperties() {
    assertThrows(NullPointerException.class, () -> new Match(null, null, null, 0, 0, null));
  }

  @Test
  void shouldThrowException_whenHomeAndVisitorTeamAreTheSame() {
    var homeTeam = new Team(UUID.randomUUID(), "Austria");
    var visitorTeam = homeTeam;

    assertThrows(IllegalArgumentException.class, () -> new Match(homeTeam, visitorTeam, Instant.parse("2024-10-07T12:00:00Z")));
  }
}
