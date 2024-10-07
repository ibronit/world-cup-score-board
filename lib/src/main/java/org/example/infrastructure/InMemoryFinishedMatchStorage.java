package org.example.infrastructure;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.example.domain.Match;

public class InMemoryFinishedMatchStorage implements FinishedMatchStorage {

  private final List<Match> finishedMatches = new CopyOnWriteArrayList<>();

  @Override
  public void addMatch(Match match) {
    finishedMatches.add(match);
  }

  @Override
  public List<Match> getMatches() {
    return finishedMatches;
  }
}
