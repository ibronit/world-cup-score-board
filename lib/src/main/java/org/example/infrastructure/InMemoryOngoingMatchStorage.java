package org.example.infrastructure;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.example.domain.Match;
import org.example.infrastructure.exceptions.AlreadyExistsException;
import org.example.infrastructure.exceptions.NotFoundException;

public class InMemoryOngoingMatchStorage implements OngoingMatchStorage {

  private final ConcurrentHashMap<UUID, Match> ongoingMatches;

  public InMemoryOngoingMatchStorage() {
    this.ongoingMatches = new ConcurrentHashMap<>();
  }

  @Override
  public Match addMatch(Match match) {
    Match alreadyExistingMatch = ongoingMatches.putIfAbsent(match.id(), match);
    if (alreadyExistingMatch != null) {
      throw new AlreadyExistsException("Match is already started.");
    }
    return match;
  }

  @Override
  public Match updateMatch(UUID matchUuid, int homeTeamScore, int visitorTeamScore) {
    var updatedMatch = ongoingMatches.computeIfPresent(matchUuid,
        (k, v) -> new Match(v.id(), v.homeTeam(), v.visitorTeam(), homeTeamScore, visitorTeamScore, v.startTime()));
    if (updatedMatch == null) {
      throw new NotFoundException("Ongoing match is not present, it cannot be updated.");
    }
    return updatedMatch;
  }

  @Override
  public Match finishMatch(UUID matchUuid) {
    Match finishedMatch = ongoingMatches.remove(matchUuid);
    if (finishedMatch == null) {
      throw new NotFoundException("There is no ongoing match to delete with this uuid: " + matchUuid);
    }
    return finishedMatch;
  }

  @Override
  public long countOfOngoingMatches() {
    return ongoingMatches.mappingCount();
  }
}
