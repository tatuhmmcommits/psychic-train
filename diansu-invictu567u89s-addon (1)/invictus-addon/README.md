# Diansu Invictus (Elysium addon) - Fabric 1.20.1

Become the bossfight. Needs **Elysium 1.1.2**, **Pehkui** and Fabric API.

## Build / run
1. Copy your Elysium jar to `libs/elysium-1_1_2.jar` (dev runtime only; don't redistribute it).
2. Make sure you have a Gradle wrapper: `gradle wrapper --gradle-version 8.6` (needs Java 17+).
3. `./gradlew runClient` to test, `./gradlew build` for the jar in `build/libs/`.
   If `runClient` complains about missing geckolib / cardinal-components / satin, add them as
   `modLocalRuntime` dependencies (Elysium bundles them, Loom normally handles that for you).

## Build on GitHub (no local setup)
1. Create a repo and push this folder (the Elysium jar is git-ignored on purpose; do not commit it).
2. Open the **Actions** tab, wait for the **build** run, and download the `diansu-invictus` artifact.
3. Put that jar in your `mods` folder with Elysium 1.1.2, Pehkui and Fabric API (Fabric, 1.20.1).
If the build fails, open the failed step and copy the error text.

## What's in it
| Item | What it does |
|---|---|
| Invictus Engine | Right-click toggles the **Invictus form** (infinite status effect). |
| Condensed Energy Rapier | Fast sword. Right-click = dash, chain 3 (Invictus form only). |
| AMPE | Aim and right-click: launches the target skyward; it explodes on the way down and on landing. |
| Battery | Food, 8 hunger + steak saturation. |
| Flask of Enchanted Elysium, Nanobot Swarm | Crafting chain from the video (see recipes). |

**Invictus form** = Pehkui scale x1.25, flight, and a boss bar (shown to players within 64 blocks)
with 3 phases (>66%, >33%, below). Later phases shorten the dash recharge and AMPE cooldown.

## Placeholders to review
- Recipes in `data/invictus/recipes/` are my guesses (the video doesn't show them).
- Damage numbers: `AmpeTracker` (4 per pulse, 10 on landing). Dash speed: `RapierItem`.
- Textures are original art in Elysium's palette (`tools/make_textures.py` regenerates them).
- Rapier hits apply Elysium's Voltage effect if present (`RapierItem.postHit`); remove if unwanted.

## Not done yet
Strafe, Netherite-armor handling, incendiary weapon, the final explosion, music, and the
armor/model look from the video. Say which you want next.
