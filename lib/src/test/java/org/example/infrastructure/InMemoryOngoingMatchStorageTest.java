package org.example.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;
import org.example.domain.Match;
import org.example.domain.Team;
import org.example.infrastructure.exceptions.AlreadyExistsException;
import org.example.infrastructure.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;

class InMemoryOngoingMatchStorageTest {

  @Test
  void shouldAddMatch() {
    var storage = new InMemoryOngoingMatchStorage();
    var homeTeam = new Team(UUID.randomUUID(), "Austria");
    var visitorTeam = new Team(UUID.randomUUID(), "England");
    Match match = storage.addMatch(new Match(homeTeam, visitorTeam, Instant.parse("2024-10-07T12:00:00Z")));

    assertNotNull(match);
    assertEquals(1, storage.countOfOngoingMatches(), "There should be one match in the OngoingMatchStorage");
  }

  @Test
  void shouldThrowException_whenMatchIsAlreadyAdded() {
    var storage = new InMemoryOngoingMatchStorage();
    var homeTeam = new Team(UUID.randomUUID(), "Austria");
    var visitorTeam = new Team(UUID.randomUUID(), "England");
    var match = new Match(homeTeam, visitorTeam, Instant.parse("2024-10-07T12:00:00Z"));
    // Add the match
    storage.addMatch(match);

    // try to add the same match again
    assertThrows(AlreadyExistsException.class, () -> storage.addMatch(match));
  }

  @Test
  void shouldThrowException_whenTryToUpdateMatchButMatchDoesNotExist() {
    var storage = new InMemoryOngoingMatchStorage();

    assertThrows(NotFoundException.class, () -> storage.updateMatch(UUID.randomUUID(), 2, 1));
  }

  @Test
  void shouldThrowException_whenTryToUpdateMatchWithNegativeScores() {
    var storage = new InMemoryOngoingMatchStorage();
    var homeTeam = new Team(UUID.randomUUID(), "Austria");
    var visitorTeam = new Team(UUID.randomUUID(), "England");
    var match = new Match(homeTeam, visitorTeam, Instant.parse("2024-10-07T12:00:00Z"));
    storage.addMatch(match);

    assertThrows(IllegalArgumentException.class, () -> storage.updateMatch(match.id(), -2, -1));
  }

  @Test
  void shouldUpdateMatch() {
    var storage = new InMemoryOngoingMatchStorage();
    var homeTeam = new Team(UUID.randomUUID(), "Austria");
    var visitorTeam = new Team(UUID.randomUUID(), "England");
    var match = new Match(homeTeam, visitorTeam, Instant.parse("2024-10-07T12:00:00Z"));
    storage.addMatch(match);

    var updatedMatch = storage.updateMatch(match.id(), 2, 1);
    assertNotNull(updatedMatch);
    assertEquals(2, updatedMatch.homeTeamScore());
    assertEquals(1, updatedMatch.visitorTeamScore());
  }
}