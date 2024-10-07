package org.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.example.infrastructure.AvailableTeamStorage;
import org.example.infrastructure.InMemoryAvailableTeamStorage;
import org.example.infrastructure.InMemoryOngoingMatchStorage;
import org.example.infrastructure.OngoingMatchStorage;
import org.example.infrastructure.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScoreBoardServiceImplIT {

  private OngoingMatchStorage inMemoryOngoingMatchStorage;
  private AvailableTeamStorage inMemoryAvailableTeamStorage;

  private ScoreBoardService scoreBoardService;

  @BeforeEach
  void init() {
    this.inMemoryOngoingMatchStorage = new InMemoryOngoingMatchStorage();
    this.inMemoryAvailableTeamStorage = new InMemoryAvailableTeamStorage();
    this.scoreBoardService = new ScoreBoardServiceImpl(this.inMemoryOngoingMatchStorage, this.inMemoryAvailableTeamStorage);
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
      executor.execute(() -> scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid));
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
}