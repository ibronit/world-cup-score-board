package org.example.domain;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
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
  public Match startNewMatch(UUID homeTeamUuid, UUID visitorTeamUuid, Instant startTime) {
    Team homeTeam = availableTeamStorage.take(homeTeamUuid)
        .orElseThrow(() -> new NotFoundException("Home team not found with uuid: " + homeTeamUuid));
    Team visitorTeam = availableTeamStorage.take(visitorTeamUuid)
        .orElseThrow(() -> new NotFoundException("Visitor team not found with uuid: " + visitorTeamUuid));

    return ongoingMatchStorage.addMatch(new Match(homeTeam, visitorTeam, startTime));
  }

  @Override
  public Match updateOngoingMatch(UUID matchUuid, int homeTeamScore, int visitorTeamScore) {
    return ongoingMatchStorage.updateMatch(matchUuid, homeTeamScore, visitorTeamScore);
  }

  @Override
  public synchronized void finishMatch(UUID matchUuid) {
    Match finishedMatch = ongoingMatchStorage.finishMatch(matchUuid);
    putBackTeams(finishedMatch);
    finishedMatchStorage.addMatch(finishedMatch);
  }

  private void putBackTeams(Match finishedMatch) {
    availableTeamStorage.put(finishedMatch.homeTeam());
    availableTeamStorage.put(finishedMatch.visitorTeam());
  }

  @Override
  public List<Match> getFinishedMatchSummary() {
    return finishedMatchStorage.getMatches().stream()
        .sorted(Comparator
            .comparingInt((Match m) -> m.homeTeamScore() + m.visitorTeamScore())
            .reversed()
            .thenComparing(Match::startTime, Comparator.reverseOrder()))
        .toList();
  }

  @Override
  public long countOfOngoingMatches() {
    return ongoingMatchStorage.countOfOngoingMatches();
  }
}
