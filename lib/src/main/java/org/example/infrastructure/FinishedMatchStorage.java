package org.example.infrastructure;

import java.util.List;
import org.example.domain.Match;

public interface FinishedMatchStorage {

  void addMatch(Match match);

  List<Match> getMatches();
}
