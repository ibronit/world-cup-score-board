package org.example.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.example.domain.Match;
import org.example.domain.Team;
import org.junit.jupiter.api.Test;

class InMemoryFinishedMatchStorageTest {

  @Test
  void shouldAddMatch() {
    var storage = new InMemoryFinishedMatchStorage();
    var homeTeam = new Team(UUID.randomUUID(), "Austria");
    var visitorTeam = new Team(UUID.randomUUID(), "England");
    storage.addMatch(new Match(homeTeam, visitorTeam));

    assertEquals(1, storage.getMatches().size(), "There should be one match in the FinishedMatchStorage");
  }
}