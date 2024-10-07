package org.example.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ScoreBoardService {

  Match startNewMatch(UUID homeTeamUuid, UUID visitorTeamUuid, Instant startTime);

  Match updateOngoingMatch(UUID matchUuid, int homeTeamScore, int visitorTeamScore);

  void finishMatch(UUID matchUuid);

  List<Match> getFinishedMatchSummary();

  long countOfOngoingMatches();
}
