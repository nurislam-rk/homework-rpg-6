# Chain of Responsibility Hints

## Goal
Let incoming damage pass through a sequence of handlers. Each handler either absorbs/reduces the damage
(and optionally stops the chain) or passes a modified amount to the next handler.

## Pattern Role Mapping

| Role | Class |
|------|-------|
| Abstract Handler | `DefenseHandler` |
| Concrete Handlers | `DodgeHandler`, `BlockHandler`, `ArmorHandler`, `HpHandler` |
| Chain Builder | `TournamentEngine` (and `Main.java` for the isolated demo) |

## Building the Chain

Use the fluent `setNext()` method to link handlers in order:

```java
DefenseHandler dodge = new DodgeHandler(hero.getDodgeChance(), seed);
DefenseHandler block = new BlockHandler(hero.getBlockRating() / 100.0);
DefenseHandler armor = new ArmorHandler(hero.getArmorValue());
DefenseHandler hp    = new HpHandler();

dodge.setNext(block).setNext(armor).setNext(hp);
```

Start the chain by calling `dodge.handle(incomingDamage, hero)`.

## Handler Behaviour

| Handler | What it does | Stops the chain? |
|---------|-------------|------------------|
| `DodgeHandler` | Rolls random dodge; absorbs **all** damage on success | Yes — only on a successful dodge |
| `BlockHandler` | Reduces damage by a percentage | No — always passes remainder |
| `ArmorHandler` | Reduces damage by a flat value (min 0) | No — always passes remainder |
| `HpHandler` | Applies final damage to `target.takeDamage()` | Yes — terminal, never passes further |

## Design Prompts

- **Which handlers stop the chain?** — only `DodgeHandler` (on success) and `HpHandler` (always terminal).
- **Which handlers always continue?** — `BlockHandler` and `ArmorHandler` always call `passToNext`.
- **What if damage reaches 0 before `HpHandler`?** — decide whether to still call `passToNext` or short-circuit.
- **Why does `setNext()` return the argument, not `this`?** — it allows fluent chaining: `a.setNext(b).setNext(c)` links `a→b→c`, not `a→a→a`.

## What to Avoid

- Checking handler types with `instanceof` anywhere in `TournamentEngine`.
- Applying damage to the fighter in `BlockHandler` or `ArmorHandler` — only `HpHandler` applies final damage.
- Calling `passToNext` from `HpHandler`.
- Hard-coding the chain order inside `DefenseHandler` itself — the order must be configurable from outside.

## Self-Check

- [ ] Can you swap handler order by changing only the chain-building code?
- [ ] Does `DodgeHandler` stop the chain when a dodge succeeds?
- [ ] Does `HpHandler` never call `passToNext()`?
- [ ] Does `BlockHandler` always forward to the next handler (even if damage is reduced to 0)?
- [ ] Is the chain built using `hero.getBlockRating() / 100.0` (double), not `hero.getBlockRating()` (int)?            
