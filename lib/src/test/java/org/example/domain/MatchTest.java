package org.example.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class MatchTest {

  @Test
  void shouldThrowException_nullProvidedForRequiredProperties() {
    assertThrows(NullPointerException.class, () -> new Match(null, null, null, 0, 0));
  }

  @Test
  void shouldThrowException_whenHomeAndVisitorTeamAreTheSame() {
    var homeTeam = new Team(UUID.randomUUID(), "Austria");
    var visitorTeam = homeTeam;

    assertThrows(IllegalArgumentException.class, () -> new Match(homeTeam, visitorTeam));
  }
}
