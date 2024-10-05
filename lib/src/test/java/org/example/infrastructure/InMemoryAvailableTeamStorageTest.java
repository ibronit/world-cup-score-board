package org.example.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.example.domain.Team;
import org.example.infrastructure.exceptions.AlreadyExistsException;
import org.junit.jupiter.api.Test;

class InMemoryAvailableTeamStorageTest {

  @Test
  void shouldStoreNewTeam() {
    var storage = new InMemoryAvailableTeamStorage();
    var id = storage.put(new Team(UUID.randomUUID(), "France"));
    assertNotNull(id);
  }

  @Test
  void shouldThrowException_whenTryToStoreTheSameTeamTwice() {
    var storage = new InMemoryAvailableTeamStorage();
    var id = UUID.randomUUID();
    var team = new Team(id, "France");
    storage.put(team);

    assertThrows(AlreadyExistsException.class, () -> storage.put(team));
  }

  @Test
  void shouldTakeTeamFromStorage() {
    var storage = new InMemoryAvailableTeamStorage();
    var team = new Team(UUID.randomUUID(), "France");
    var storeTeam = storage.put(team);
    var takenTeam = storage.take(storeTeam.id());

    assertTrue(takenTeam.isPresent());
    assertEquals(team.id(), takenTeam.get().id());
    assertEquals(team.name(), takenTeam.get().name());
  }

  @Test
  void shouldReturnTeam_whenTeamDoesNotExist() {
    var storage = new InMemoryAvailableTeamStorage();
    var nonExistingTeam = storage.take(UUID.randomUUID());

    assertTrue(nonExistingTeam.isEmpty());
  }

  @Test
  void shouldNotReturnTeam_whenTeamIsAlreadyTaken() {
    var storage = new InMemoryAvailableTeamStorage();
    var team = new Team(UUID.randomUUID(), "France");
    var storedTeam = storage.put(team);
    // take team
    var takenTeam = storage.take(storedTeam.id());
    assertTrue(takenTeam.isPresent());
    // try to take it again
    var alreadyTakenTeam = storage.take(storedTeam.id());
    assertTrue(alreadyTakenTeam.isEmpty());
  }
}