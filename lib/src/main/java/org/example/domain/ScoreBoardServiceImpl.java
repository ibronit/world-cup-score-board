package org.example.domain;

import java.util.UUID;
import org.example.infrastructure.AvailableTeamStorage;
import org.example.infrastructure.FinishedMatchStorage;
import org.example.infrastructure.OngoingMatchStorage;
import org.example.infrastructure.exceptions.NotFoundException;

public class ScoreBoardServiceImpl implements ScoreBoardService {

  private final OngoingMatchStorage ongoingMatchStorage;
  private final AvailableTeamStorage availableTeamStorage;
  private final FinishedMatchStorage finishedMatchStorage;

  public ScoreBoardServiceImpl(OngoingMatchStorage ongoingMatchStorage, AvailableTeamStorage availableTeamStorage,
      FinishedMatchStorage finishedMatchStorage) {
    this.ongoingMatchStorage = ongoingMatchStorage;
    this.availableTeamStorage = availableTeamStorage;
    this.finishedMatchStorage = finishedMatchStorage;
  }

  @Override
  public Match startNewMatch(UUID homeTeamUuid, UUID visitorTeamUuid) {
    Team homeTeam = availableTeamStorage.take(homeTeamUuid)
        .orElseThrow(() -> new NotFoundException("Home team not found with uuid: " + homeTeamUuid));
    Team visitorTeam = availableTeamStorage.take(visitorTeamUuid)
        .orElseThrow(() -> new NotFoundException("Visitor team not found with uuid: " + visitorTeamUuid));

    return ongoingMatchStorage.addMatch(new Match(homeTeam, visitorTeam));
  }

  @Override
  public Match updateOngoingMatch(UUID matchUuid, int homeTeamScore, int visitorTeamScore) {
    return ongoingMatchStorage.updateMatch(matchUuid, homeTeamScore, visitorTeamScore);
  }

  @Override
  public synchronized void finishMatch(UUID matchUuid) {
    Match finishedMatch = ongoingMatchStorage.finishMatch(matchUuid);
    finishedMatchStorage.addMatch(finishedMatch);
  }

  @Override
  public long countOfOngoingMatches() {
    return ongoingMatchStorage.countOfOngoingMatches();
  }
}
