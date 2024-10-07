package org.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.example.infrastructure.AvailableTeamStorage;
import org.example.infrastructure.FinishedMatchStorage;
import org.example.infrastructure.InMemoryAvailableTeamStorage;
import org.example.infrastructure.InMemoryFinishedMatchStorage;
import org.example.infrastructure.InMemoryOngoingMatchStorage;
import org.example.infrastructure.OngoingMatchStorage;
import org.example.infrastructure.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScoreBoardServiceImplIT {

  private OngoingMatchStorage inMemoryOngoingMatchStorage;
  private AvailableTeamStorage inMemoryAvailableTeamStorage;
  private FinishedMatchStorage finishedMatchStorage;

  private ScoreBoardService scoreBoardService;

  @BeforeEach
  void init() {
    this.inMemoryOngoingMatchStorage = new InMemoryOngoingMatchStorage();
    this.inMemoryAvailableTeamStorage = new InMemoryAvailableTeamStorage();
    this.finishedMatchStorage = new InMemoryFinishedMatchStorage();
    this.scoreBoardService = new ScoreBoardServiceImpl(this.inMemoryOngoingMatchStorage,
        this.inMemoryAvailableTeamStorage, this.finishedMatchStorage);
  }

  @Test
  void shouldThrowNotFoundException_whenHomeTeamIsNotAvailableToPlay() {
    var homeTeamUuid = UUID.randomUUID();
    var visitorTeamUuid = UUID.randomUUID();
    inMemoryAvailableTeamStorage.put(new Team(visitorTeamUuid, "England"));

    assertThrows(NotFoundException.class, () -> scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid));
  }

  @Test
  void shouldThrowNotFoundException_whenVisitorTeamIsNotAvailableToPlay() {
    var homeTeamUuid = UUID.randomUUID();
    var visitorTeamUuid = UUID.randomUUID();
    inMemoryAvailableTeamStorage.put(new Team(homeTeamUuid, "Austria"));

    assertThrows(NotFoundException.class, () -> scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid));
  }

  @Test
  void shouldThrowNotFoundException_whenTeamIsAlreadyPlayingAMatch() {
    var homeTeamUuid = UUID.randomUUID();
    var visitorTeamUuid = UUID.randomUUID();
    inMemoryAvailableTeamStorage.put(new Team(homeTeamUuid, "Austria"));
    inMemoryAvailableTeamStorage.put(new Team(visitorTeamUuid, "England"));

    // start game
    scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid);
    // try to start another game with the same teams.
    assertThrows(NotFoundException.class, () -> scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid));
  }

  @Test
  void shouldStartTheSameMatchOnlyOnce_whenTryingToStartTheSameMatchConcurrently() throws InterruptedException {
    var homeTeamUuid = UUID.randomUUID();
    var visitorTeamUuid = UUID.randomUUID();
    inMemoryAvailableTeamStorage.put(new Team(homeTeamUuid, "Austria"));
    inMemoryAvailableTeamStorage.put(new Team(visitorTeamUuid, "England"));

    // start game
    var executor = Executors.newVirtualThreadPerTaskExecutor();
    for (int i = 0; i < 10; i++) {
      executor.execute(() -> {
        try {
          scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid);
        } catch (Exception ignore) {}
      });
    }
    executor.awaitTermination(1, TimeUnit.SECONDS);

    assertEquals(1, scoreBoardService.countOfOngoingMatches());
  }

  @Test
  void shouldStartNewMatch() {
    var homeTeamUuid = UUID.randomUUID();
    var visitorTeamUuid = UUID.randomUUID();
    inMemoryAvailableTeamStorage.put(new Team(homeTeamUuid, "Austria"));
    inMemoryAvailableTeamStorage.put(new Team(visitorTeamUuid, "England"));

    Match match = scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid);

    assertNotNull(match);
    assertEquals(1, scoreBoardService.countOfOngoingMatches(), "Exactly 1 team should play");
  }

  @Test
  void shouldUpdateMatch() {
    var homeTeamUuid = UUID.randomUUID();
    var visitorTeamUuid = UUID.randomUUID();
    inMemoryAvailableTeamStorage.put(new Team(homeTeamUuid, "Austria"));
    inMemoryAvailableTeamStorage.put(new Team(visitorTeamUuid, "England"));

    Match match = scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid);

    assertNotNull(match);
    assertEquals(1, scoreBoardService.countOfOngoingMatches(), "Exactly 1 team should play");
    assertEquals(0, match.homeTeamScore());
    assertEquals(0, match.visitorTeamScore());

    Match updatedMatch = scoreBoardService.updateOngoingMatch(match.id(), 4, 1);
    assertNotNull(updatedMatch);
    assertEquals(1, scoreBoardService.countOfOngoingMatches(), "Exactly 1 team should play");
    assertEquals(4, updatedMatch.homeTeamScore());
    assertEquals(1, updatedMatch.visitorTeamScore());
  }

  @Test
  void shouldFinishMatch() {
    var homeTeamUuid = UUID.randomUUID();
    var visitorTeamUuid = UUID.randomUUID();
    inMemoryAvailableTeamStorage.put(new Team(homeTeamUuid, "Austria"));
    inMemoryAvailableTeamStorage.put(new Team(visitorTeamUuid, "England"));

    Match match = scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid);

    assertNotNull(match);
    assertEquals(1, scoreBoardService.countOfOngoingMatches(), "Exactly 1 team should play");
    assertEquals(0, match.homeTeamScore());
    assertEquals(0, match.visitorTeamScore());

    Match updatedMatch = scoreBoardService.updateOngoingMatch(match.id(), 4, 1);
    assertNotNull(updatedMatch);
    assertEquals(1, scoreBoardService.countOfOngoingMatches(), "Exactly 1 team should play");
    assertEquals(4, updatedMatch.homeTeamScore());
    assertEquals(1, updatedMatch.visitorTeamScore());

    scoreBoardService.finishMatch(match.id());
    assertEquals(0, scoreBoardService.countOfOngoingMatches(), "Exactly 0 team should play");
  }
}