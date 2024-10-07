package org.example.infrastructure;

import java.util.UUID;
import org.example.domain.Match;
import org.example.infrastructure.exceptions.AlreadyExistsException;

public interface OngoingMatchStorage {

  /**
   * @param match {@link Match} object
   * @return Match that was added to the score board
   * @throws AlreadyExistsException if match is already added
   */
  Match addMatch(Match match);

  Match updateMatch(UUID matchUuid, int homeTeamScore, int visitorTeamScore);

  Match finishMatch(UUID matchUuid);

  long countOfOngoingMatches();
}
