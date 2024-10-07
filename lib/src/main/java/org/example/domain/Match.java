package org.example.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Match(UUID id,
                    Team homeTeam,
                    Team visitorTeam,
                    int homeTeamScore,
                    int visitorTeamScore,
                    Instant startTime) {

  public Match {
    Objects.requireNonNull(id);
    Objects.requireNonNull(homeTeam);
    Objects.requireNonNull(visitorTeam);
    Objects.requireNonNull(startTime);

    if (homeTeam.id().equals(visitorTeam.id())) {
      throw new IllegalArgumentException("homeTeam cannot equal with the visitorTeam");
    }

    if (homeTeamScore < 0) {
      throw new IllegalArgumentException("homeTeamScore must be 0 or a positive number.");
    }

    if (visitorTeamScore < 0) {
      throw new IllegalArgumentException("visitorTeamScore must be 0 or a positive number.");
    }
  }

  public Match(Team homeTeam, Team visitorTeam, Instant startTime) {
    this(UUID.randomUUID(), homeTeam, visitorTeam, 0, 0, startTime);
  }
}
