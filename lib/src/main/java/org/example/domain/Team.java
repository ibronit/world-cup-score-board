package org.example.domain;

import java.util.Objects;
import java.util.UUID;

public record Team(UUID id, String name) {

  public Team {
    Objects.requireNonNull(id);
    Objects.requireNonNull(name);
  }
}
