package org.example.domain;

import java.util.UUID;

public interface ScoreBoardService {

  Match startNewMatch(UUID homeTeamUuid, UUID visitorTeamUuid);

  long countOfOngoingMatches();
}
