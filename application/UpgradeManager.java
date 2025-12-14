package application;

import java.util.*;

public class UpgradeManager {
    private int pushRangeLevel;
    private int pushStrengthLevel;
    private int pushAreaLevel;

    // Track active upgrades
    private boolean hasPushRange = false;
    private boolean hasRangePlus = false;
    private boolean hasPushStrengthUpgrade = false;
    private boolean hasPushAreaUpgrade = false;

    public enum UpgradeType {
        PUSH_RANGE,
        PUSH_RANGE_PLUS,
        PUSH_STRENGTH,
        PUSH_AREA,
        SPAWN_TRAPS,
        HEAL
    }

    public static class Upgrade {
        public final UpgradeType type;
        public final String name;
        public final String description;

        public Upgrade(UpgradeType type, String name, String description) {
            this.type = type;
            this.name = name;
            this.description = description;
        }
    }

    public UpgradeManager() {
        this.pushRangeLevel = 0;
        this.pushStrengthLevel = 0;
        this.pushAreaLevel = 0;
    }

    /**
     * Gets 3 random upgrade cards including heal option
     */
    public List<Upgrade> getRandomUpgrades() {
        List<Upgrade> available = new ArrayList<>();

        // Push Range upgrades
        if (!hasPushRange) {
            available.add(new Upgrade(
                UpgradeType.PUSH_RANGE,
                "Push Range I",
                "Push enemies an extra tile away"
            ));
        }

        if (hasPushRange && !hasRangePlus) {
            available.add(new Upgrade(
                UpgradeType.PUSH_RANGE_PLUS,
                "Push Range II",
                "Push enemies two extra tiles away"
            ));
        }

        // Push Strength upgrade
        if (!hasPushStrengthUpgrade) {
            available.add(new Upgrade(
                UpgradeType.PUSH_STRENGTH,
                "Push Strength",
                "Enemy flies back an extra tile (Brutes always 1 tile)"
            ));
        }

        // Push Area upgrade
        if (!hasPushAreaUpgrade) {
            available.add(new Upgrade(
                UpgradeType.PUSH_AREA,
                "Push Area",
                "Wave Push - 3 tiles in front"
            ));
        }

        // Spawn Traps - always available
        available.add(new Upgrade(
            UpgradeType.SPAWN_TRAPS,
            "Spawn Traps",
            "Instantly spawn 2-3 Bear Traps"
        ));

        // Heal - always available
        available.add(new Upgrade(
            UpgradeType.HEAL,
            "Heal",
            "Restore 1 HP"
        ));

        // Shuffle and pick 3
        Collections.shuffle(available);
        return available.subList(0, Math.min(3, available.size()));
    }

    /**
     * Apply selected upgrade
     */
    public void applyUpgrade(UpgradeType type, Player player, GameLogic logic) {
        switch (type) {
            case PUSH_RANGE:
                hasPushRange = true;
                pushRangeLevel = 1;
                System.out.println("[UPGRADE] Push Range I activated");
                break;

            case PUSH_RANGE_PLUS:
                hasRangePlus = true;
                pushRangeLevel = 2;
                System.out.println("[UPGRADE] Push Range II activated - telekinetic push!");
                break;

            case PUSH_STRENGTH:
                hasPushStrengthUpgrade = true;
                pushStrengthLevel = 1;
                System.out.println("[UPGRADE] Push Strength activated - enemies fly 2+ tiles!");
                break;

            case PUSH_AREA:
                hasPushAreaUpgrade = true;
                pushAreaLevel = 1;
                System.out.println("[UPGRADE] Push Area activated - Wave Push!");
                break;

            case SPAWN_TRAPS:
                logic.spawnBearTraps();
                System.out.println("[UPGRADE] Spawning Bear Traps!");
                break;

            case HEAL:
                player.heal(1);
                System.out.println("[UPGRADE] Player healed 1 HP!");
                break;
        }
    }

    // Getters
    public int getPushRangeLevel() { return pushRangeLevel; }
    public int getPushStrengthLevel() { return pushStrengthLevel; }
    public int getPushAreaLevel() { return pushAreaLevel; }
    public boolean hasPushRange() { return hasPushRange; }
    public boolean hasRangePlus() { return hasRangePlus; }
    public boolean hasPushStrengthUpgrade() { return hasPushStrengthUpgrade; }
    public boolean hasPushAreaUpgrade() { return hasPushAreaUpgrade; }
}