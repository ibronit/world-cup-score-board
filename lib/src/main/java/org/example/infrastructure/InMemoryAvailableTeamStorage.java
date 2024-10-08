package org.example.infrastructure;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.example.domain.Team;
import org.example.infrastructure.exceptions.AlreadyExistsException;
import org.example.infrastructure.exceptions.NotFoundException;

public class InMemoryAvailableTeamStorage implements AvailableTeamStorage {

  private final ConcurrentHashMap<UUID, Team> availableTeams = new ConcurrentHashMap<>();

  @Override
  public Team put(Team team) {
    Team existingTeam = availableTeams.putIfAbsent(team.id(), team);
    if (existingTeam != null) {
      throw new AlreadyExistsException("Team already exists with id: " + team.id());
    }
    return team;
  }

  @Override
  public Optional<Team> take(UUID id) throws NotFoundException {
    return Optional.ofNullable(availableTeams.remove(id));
  }

  @Override
  public long count() {
    return availableTeams.mappingCount();
  }
}
