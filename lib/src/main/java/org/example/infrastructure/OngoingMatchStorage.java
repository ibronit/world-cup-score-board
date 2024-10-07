package org.example.infrastructure;

import org.example.domain.Match;
import org.example.infrastructure.exceptions.AlreadyExistsException;

public interface OngoingMatchStorage {

  /**
   * @param match {@link Match} object
   * @return Match that was added to the score board
   * @throws AlreadyExistsException if match is already added
   */
  Match addMatch(Match match);

  long countOfOngoingMatches();
}
