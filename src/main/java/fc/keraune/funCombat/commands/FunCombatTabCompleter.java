package fc.keraune.funCombat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class FunCombatTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Completar subcomandos principales
            if (sender.hasPermission("funcombat.admin")) {
                completions.add("reload");
            }
            completions.add("status");
        }

        return completions;
    }
}