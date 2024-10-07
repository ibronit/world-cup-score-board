package org.example.infrastructure;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.example.domain.Match;
import org.example.infrastructure.exceptions.AlreadyExistsException;

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
  public long countOfOngoingMatches() {
    return ongoingMatches.mappingCount();
  }
}
