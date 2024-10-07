package org.example.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.example.domain.Match;
import org.example.domain.Team;
import org.example.infrastructure.exceptions.AlreadyExistsException;
import org.junit.jupiter.api.Test;

class InMemoryOngoingMatchStorageTest {

  @Test
  void shouldAddMatch() {
    var storage = new InMemoryOngoingMatchStorage();
    var homeTeam = new Team(UUID.randomUUID(), "Austria");
    var visitorTeam = new Team(UUID.randomUUID(), "England");
    Match match = storage.addMatch(new Match(homeTeam, visitorTeam));

    assertNotNull(match);
    assertEquals(1, storage.countOfOngoingMatches(), "There should be one match in the OngoingMatchStorage");
  }

  @Test
  void shouldThrowException_whenMatchIsAlreadyAdded() {
    var storage = new InMemoryOngoingMatchStorage();
    var homeTeam = new Team(UUID.randomUUID(), "Austria");
    var visitorTeam = new Team(UUID.randomUUID(), "England");
    var match = new Match(homeTeam, visitorTeam);
    // Add the match
    storage.addMatch(match);

    // try to add the same match again
    assertThrows(AlreadyExistsException.class, () -> storage.addMatch(match));
  }
}