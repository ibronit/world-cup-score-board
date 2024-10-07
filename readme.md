### Notes
- `AvailableTeamStorage`: Once a team is taken from the pool, it cannot participate in another match until the team is not put back.
- `OngoingMatchStorage`: `Match` objects are read-only objects. When a `match` is updated then a new `match` object will be created and replaced in the `OngoingMatchStorage`.