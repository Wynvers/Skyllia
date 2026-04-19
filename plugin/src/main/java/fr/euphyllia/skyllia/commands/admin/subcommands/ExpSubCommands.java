package fr.euphyllia.skyllia.commands.admin.subcommands;

import fr.euphyllia.skyllia.Skyllia;
import fr.euphyllia.skyllia.api.commands.SubCommandInterface;
import fr.euphyllia.skyllia.api.skyblock.Island;
import fr.euphyllia.skyllia.configuration.ConfigLoader;
import fr.euphyllia.skyllia.managers.skyblock.SkyblockManager;
import fr.euphyllia.skyllia.utils.IslandExperienceManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExpSubCommands implements SubCommandInterface {

    private final Logger logger = LogManager.getLogger(ExpSubCommands.class);

    @Override
    public void onExecute(@NotNull Plugin plugin, @NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("skyllia.admins.commands.island.exp")) {
            ConfigLoader.language.sendMessage(sender, "island.player.permission-denied");
            return;
        }

        if (args.length < 3) {
            ConfigLoader.language.sendMessage(sender, "island.admin.exp.args-missing");
            return;
        }

        String action = args[0].toLowerCase();
        String playerName = args[1];
        String amountStr = args[2];

        if (!action.equals("add")) {
            ConfigLoader.language.sendMessage(sender, "island.admin.exp.args-missing");
            return;
        }

        try {
            UUID playerId;
            try {
                playerId = UUID.fromString(playerName);
            } catch (IllegalArgumentException ignored) {
                playerId = Bukkit.getPlayerUniqueId(playerName);
            }

            SkyblockManager skyblockManager = Skyllia.getInstance().getInterneAPI().getSkyblockManager();
            Island island = skyblockManager.getIslandByPlayerId(playerId);
            if (island == null) {
                ConfigLoader.language.sendMessage(sender, "island.player.no-island");
                return;
            }

            double amount = Double.parseDouble(amountStr);
            boolean success = IslandExperienceManager.addExperience(island, amount);

            if (success) {
                double newExp = IslandExperienceManager.getExperience(island);
                int newLevel = IslandExperienceManager.getLevel(island);
                ConfigLoader.language.sendMessage(sender, "island.admin.exp.add-success", Map.of(
                        "%player%", playerName,
                        "%amount%", String.valueOf(amount),
                        "%experience%", String.valueOf(newExp),
                        "%level%", String.valueOf(newLevel)
                ));
            } else {
                ConfigLoader.language.sendMessage(sender, "island.admin.exp.add-failed");
            }

        } catch (NumberFormatException ignored) {
            ConfigLoader.language.sendMessage(sender, "island.admin.exp.nan");
        } catch (Exception e) {
            logger.log(Level.FATAL, e.getMessage(), e);
            ConfigLoader.language.sendMessage(sender, "island.generic.unexpected-error");
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull Plugin plugin, @NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("skyllia.admins.commands.island.exp")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            String partial = args[0].trim().toLowerCase();
            return Stream.of("add")
                    .filter(a -> a.startsWith(partial))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String partial = args[1].trim().toLowerCase();
            return new ArrayList<>(Bukkit.getOnlinePlayers()).stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .sorted()
                    .collect(Collectors.toList());
        }

        if (args.length == 3) {
            String partial = args[2].trim().toLowerCase();
            List<String> amounts = Arrays.asList("100", "500", "1000", "5000");
            return amounts.stream()
                    .filter(a -> a.startsWith(partial))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
