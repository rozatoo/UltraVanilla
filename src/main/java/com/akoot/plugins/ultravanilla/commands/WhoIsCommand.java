package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.UltraVanilla;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WhoIsCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.DARK_GREEN;

    public WhoIsCommand(UltraVanilla instance) {
        super(instance);
        this.color = COLOR;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            String search = args[0];
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                String displayName = ChatColor.stripColor(player.getDisplayName());
                if (search.equals(displayName)) {
                    sendMessage(sender, "&r%s &:is none other than &d%s&:!", player.getDisplayName(), player.getName());
                    return true;
                }
            }
            sendMessage(sender, "Couldn't find anyone...");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                String displayName = ChatColor.stripColor(player.getDisplayName());
                if (!displayName.isEmpty() && !suggestions.contains(displayName) && !displayName.equals(player.getName())) {
                    suggestions.add(displayName);
                }
            }
        }
        return getSuggestions(suggestions, args);
    }
}
