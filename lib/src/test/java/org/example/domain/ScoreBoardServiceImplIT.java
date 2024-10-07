package org.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.List;
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

    assertThrows(NotFoundException.class,
        () -> scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid, Instant.parse("2024-10-07T12:00:00Z")));
  }

  @Test
  void shouldThrowNotFoundException_whenVisitorTeamIsNotAvailableToPlay() {
    var homeTeamUuid = UUID.randomUUID();
    var visitorTeamUuid = UUID.randomUUID();
    inMemoryAvailableTeamStorage.put(new Team(homeTeamUuid, "Austria"));

    assertThrows(NotFoundException.class,
        () -> scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid, Instant.parse("2024-10-07T12:00:00Z")));
  }

  @Test
  void shouldThrowNotFoundException_whenTeamIsAlreadyPlayingAMatch() {
    var homeTeamUuid = UUID.randomUUID();
    var visitorTeamUuid = UUID.randomUUID();
    inMemoryAvailableTeamStorage.put(new Team(homeTeamUuid, "Austria"));
    inMemoryAvailableTeamStorage.put(new Team(visitorTeamUuid, "England"));

    // start game
    scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid, Instant.parse("2024-10-07T12:00:00Z"));
    // try to start another game with the same teams.
    assertThrows(NotFoundException.class,
        () -> scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid, Instant.parse("2024-10-07T12:00:00Z")));
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
          scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid, Instant.parse("2024-10-07T12:00:00Z"));
        } catch (Exception ignore) {
        }
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

    Match match = scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid, Instant.parse("2024-10-07T12:00:00Z"));

    assertNotNull(match);
    assertEquals(1, scoreBoardService.countOfOngoingMatches(), "Exactly 1 team should play");
  }

  @Test
  void shouldUpdateMatch() {
    var homeTeamUuid = UUID.randomUUID();
    var visitorTeamUuid = UUID.randomUUID();
    inMemoryAvailableTeamStorage.put(new Team(homeTeamUuid, "Austria"));
    inMemoryAvailableTeamStorage.put(new Team(visitorTeamUuid, "England"));

    Match match = scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid, Instant.parse("2024-10-07T12:00:00Z"));

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

    Match match = scoreBoardService.startNewMatch(homeTeamUuid, visitorTeamUuid, Instant.parse("2024-10-07T12:00:00Z"));

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

  @Test
  void shouldSummarizeFinishedMatchesOrderedByTotalScoresAndStartTime() {
    var homeTeam1Uuid = UUID.randomUUID();
    var visitorTeam1Uuid = UUID.randomUUID();
    var homeTeam2Uuid = UUID.randomUUID();
    var visitorTeam2Uuid = UUID.randomUUID();
    var homeTeam3Uuid = UUID.randomUUID();
    var visitorTeam3Uuid = UUID.randomUUID();
    var homeTeam4Uuid = UUID.randomUUID();
    var visitorTeam4Uuid = UUID.randomUUID();
    inMemoryAvailableTeamStorage.put(new Team(homeTeam1Uuid, "Austria"));
    inMemoryAvailableTeamStorage.put(new Team(visitorTeam1Uuid, "England"));
    inMemoryAvailableTeamStorage.put(new Team(homeTeam2Uuid, "France"));
    inMemoryAvailableTeamStorage.put(new Team(visitorTeam2Uuid, "Belgium"));
    inMemoryAvailableTeamStorage.put(new Team(homeTeam3Uuid, "Turkey"));
    inMemoryAvailableTeamStorage.put(new Team(visitorTeam3Uuid, "Hungary"));
    inMemoryAvailableTeamStorage.put(new Team(homeTeam4Uuid, "USA"));
    inMemoryAvailableTeamStorage.put(new Team(visitorTeam4Uuid, "Brazil"));

    Match match1 = scoreBoardService.startNewMatch(homeTeam1Uuid, visitorTeam1Uuid,
        Instant.parse("2024-10-07T12:00:00Z"));
    Match match2 = scoreBoardService.startNewMatch(homeTeam2Uuid, visitorTeam2Uuid,
        Instant.parse("2024-10-07T12:00:00Z"));
    // match3 starts a bit later than match4
    Match match3 = scoreBoardService.startNewMatch(homeTeam3Uuid, visitorTeam3Uuid,
        Instant.parse("2024-10-07T13:00:00Z"));
    Match match4 = scoreBoardService.startNewMatch(homeTeam4Uuid, visitorTeam4Uuid,
        Instant.parse("2024-10-07T12:00:00Z"));

    scoreBoardService.updateOngoingMatch(match1.id(), 4, 1);
    scoreBoardService.updateOngoingMatch(match2.id(), 1, 3);
    scoreBoardService.updateOngoingMatch(match3.id(), 1, 1);
    scoreBoardService.updateOngoingMatch(match4.id(), 1, 1);

    scoreBoardService.finishMatch(match1.id());
    scoreBoardService.finishMatch(match2.id());
    scoreBoardService.finishMatch(match3.id());
    scoreBoardService.finishMatch(match4.id());

    List<Match> summary = scoreBoardService.getFinishedMatchSummary();
    assertNotNull(summary);
    assertEquals(4, summary.size());
    assertEquals(match1.id(), summary.get(0).id());
    assertEquals(match2.id(), summary.get(1).id());
    assertEquals(match3.id(), summary.get(2).id());
    assertEquals(match4.id(), summary.get(3).id());
  }
}