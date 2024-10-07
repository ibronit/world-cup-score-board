package org.example.infrastructure;

import java.util.Optional;
import java.util.UUID;
import org.example.domain.Team;

public interface AvailableTeamStorage {
  Team put(Team teamName);
  Optional<Team> take(UUID id);
  long count();
}
