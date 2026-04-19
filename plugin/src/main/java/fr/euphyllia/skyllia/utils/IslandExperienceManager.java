package fr.euphyllia.skyllia.utils;

import fr.euphyllia.skyllia.api.SkylliaAPI;
import fr.euphyllia.skyllia.api.database.IslandCustomDataQuery;
import fr.euphyllia.skyllia.api.skyblock.Island;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

/**
 * Manages island experience and level using the custom data storage.
 * <p>
 * Experience is stored per-island in the custom data table.
 * Level is derived from experience: {@code level = floor(experience / 100)}.
 */
public final class IslandExperienceManager {

    private static final String EXPERIENCE_KEY = "experience";
    private static final NamespacedKey NAMESPACE_KEY =
            new NamespacedKey("skyllia", "island_experience");

    private IslandExperienceManager() {
    }

    /**
     * Gets the current experience of an island.
     *
     * @param island the island
     * @return the experience value (0 if not set)
     */
    public static double getExperience(@NotNull Island island) {
        IslandCustomDataQuery query = SkylliaAPI.getIslandCustomDataQuery();
        Double value = query.get(NAMESPACE_KEY, island, EXPERIENCE_KEY, PersistentDataType.DOUBLE);
        return value != null ? value : 0.0;
    }

    /**
     * Sets the experience of an island.
     *
     * @param island     the island
     * @param experience the new experience value
     * @return {@code true} if the update succeeded
     */
    public static boolean setExperience(@NotNull Island island, double experience) {
        IslandCustomDataQuery query = SkylliaAPI.getIslandCustomDataQuery();
        return query.set(NAMESPACE_KEY, island, EXPERIENCE_KEY, PersistentDataType.DOUBLE, Math.max(0, experience));
    }

    /**
     * Adds experience to an island.
     *
     * @param island the island
     * @param amount the amount to add (can be negative to remove)
     * @return {@code true} if the update succeeded
     */
    public static boolean addExperience(@NotNull Island island, double amount) {
        double current = getExperience(island);
        return setExperience(island, current + amount);
    }

    /**
     * Gets the level of an island derived from its experience.
     * <p>
     * Formula: {@code level = floor(experience / 100)}
     *
     * @param island the island
     * @return the island level
     */
    public static int getLevel(@NotNull Island island) {
        return (int) Math.floor(getExperience(island) / 100.0);
    }
}
