# Command Pattern Hints

## Goal
Encapsulate each hero action as an object so it can be queued, inspected, and undone before execution.

## Pattern Role Mapping

| Role | Class |
|------|-------|
| Command interface | `ActionCommand` |
| Concrete Commands | `AttackCommand`, `HealCommand`, `DefendCommand` |
| Invoker | `ActionQueue` |
| Receivers | `ArenaFighter` (heal, defend), `ArenaOpponent` (attack) |

## Core Idea
A command object stores its receiver and all the parameters it needs to act.
The invoker (`ActionQueue`) holds commands and decides when they run.
The receiver knows nothing about the invoker — it only exposes methods that commands call.

## Design Prompts

- **Who calls `execute()`?** — the invoker (`ActionQueue.executeAll()`), not the engine directly.
- **What state must `AttackCommand` remember?** — the actual damage dealt (not just `attackPower`), so `undo()` can reverse the exact amount.
- **What does `undo()` mean here?** — for a queued-but-not-yet-executed command, `undo()` reverses any changes made when the command was queued. For simplicity, `ActionQueue.undoLast()` just removes the last command from the queue without calling `undo()`.
- **How does `executeAll()` clean up?** — it must clear the queue after running all commands.

## Handler Table

| Command | Receiver | `execute()` action | `undo()` action |
|---------|---------|--------------------|-----------------|
| `AttackCommand` | `ArenaOpponent` | `target.takeDamage(attackPower)` | `target.restoreHealth(damageDealt)` |
| `HealCommand` | `ArenaFighter` | `target.heal(healAmount)` | `target.takeDamage(actualHealApplied)` |
| `DefendCommand` | `ArenaFighter` | `target.modifyDodgeChance(+dodgeBoost)` | `target.modifyDodgeChance(-dodgeBoost)` |

## What to Avoid

- Putting receiver logic (damage calculation, heal clamping) inside the invoker.
- Calling `opponent.takeDamage()` or `hero.takeDamage()` directly from `TournamentEngine` instead of routing through commands.
- Using `attackPower` in `undo()` instead of `damageDealt` — they differ when the target had less HP than `attackPower`.

## Self-Check

- [ ] Does `ActionQueue` depend only on `ActionCommand`, never on `AttackCommand` directly?
- [ ] Does `undoLast()` change the queue without executing any command?
- [ ] Does `executeAll()` leave the queue empty after all commands run?
- [ ] Does `AttackCommand.undo()` use `damageDealt`, not `attackPower`?
          