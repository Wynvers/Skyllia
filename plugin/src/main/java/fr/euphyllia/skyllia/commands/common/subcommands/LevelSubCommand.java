package fr.euphyllia.skyllia.commands.common.subcommands;

import fr.euphyllia.skyllia.api.SkylliaAPI;
import fr.euphyllia.skyllia.api.commands.SubCommandInterface;
import fr.euphyllia.skyllia.api.skyblock.Island;
import fr.euphyllia.skyllia.configuration.ConfigLoader;
import fr.euphyllia.skyllia.utils.IslandExperienceManager;
import fr.euphyllia.skyllia.utils.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LevelSubCommand implements SubCommandInterface {

    @Override
    public void onExecute(@NotNull Plugin plugin, @NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            ConfigLoader.language.sendMessage(sender, "island.player.player-only-command");
            return;
        }

        if (!PlayerUtils.hasPermission(player, permission())) {
            ConfigLoader.language.sendMessage(player, "island.level.no-permission");
            return;
        }

        Island island = SkylliaAPI.getIslandByPlayerId(player.getUniqueId());
        if (island == null) {
            ConfigLoader.language.sendMessage(player, "island.player.no-island");
            return;
        }

        int level = IslandExperienceManager.getLevel(island);
        double experience = IslandExperienceManager.getExperience(island);

        ConfigLoader.language.sendMessage(player, "island.level.display", Map.of(
                "%level%", String.valueOf(level),
                "%experience%", String.valueOf(experience)
        ));
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull Plugin plugin, @NotNull CommandSender sender, @NotNull String[] args) {
        return Collections.emptyList();
    }

    @Override
    public String permission() {
        return "skyllia.island.command.level";
    }
}
