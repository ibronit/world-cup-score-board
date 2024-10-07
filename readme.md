# Live Football World Cup Score Board Lib

## Requirements

The scoreboard supports the following operations:

1. Start a new match, assuming initial score 0 – 0 and adding it the scoreboard.
   This should capture following parameters:
   a. Home team
   b. Away team
2. Update score. This should receive a pair of absolute scores: home team score and away
   team score.
3. Finish match currently in progress. This removes a match from the scoreboard.
4. Get a summary of matches in progress ordered by their total score. The matches with the
   same total score will be returned ordered by the most recently started match in the
   scoreboard.

## Architectural decisions
- `AvailableTeamStorage`: Stores the available teams ready to play. Once a team is taken from the pool, it cannot
  participate in another match until the team is not put back to the pool.
- `OngoingMatchStorage`: Stores the ongoing matches that can be still updated until the end of the match. `Match`
  objects are read-only objects. When a `match` is updated then a new `match` object will be created and replaced in the
  `OngoingMatchStorage`.
- `FinishedMatchStorage`: Stores the finished matches for analytical purposes.
- For simple data manipulation in the `InMemoryStorages`, I use concurrent datastructures and single operations so it remains
  thread-safe. For more complex scenarios I use the `synchronized` block.

## Usage

### Prerequisites & Tooling
- java 21

### Run the tests
- Run tests from your IDE

OR
```sh
./gradlew test
```