package org.example.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TeamTest {

  @Test
  void shouldThrowException_nullProvidedForRequiredProperties() {
    assertThrows(NullPointerException.class, () -> new Team(null, null));
  }
}
