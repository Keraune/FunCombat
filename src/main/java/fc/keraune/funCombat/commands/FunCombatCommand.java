package fc.keraune.funCombat.commands;

import fc.keraune.funCombat.FunCombat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FunCombatCommand implements CommandExecutor {

    private final FunCombat plugin;

    public FunCombatCommand(FunCombat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadPluginConfig();
            sender.sendMessage(ChatColor.GREEN + "FunCombat recargado!");
            sendStatus(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("status")) {
            sendStatus(sender);
            return true;
        }

        sendHelp(sender);
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== FunCombat ===");
        sender.sendMessage(ChatColor.YELLOW + "/funcombat reload " + ChatColor.WHITE + "- Recargar configuración");
        sender.sendMessage(ChatColor.YELLOW + "/funcombat status " + ChatColor.WHITE + "- Ver estado actual");
    }

    private void sendStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Estado del Knockback ===");

        sender.sendMessage(ChatColor.YELLOW + "Reducir KB: " + ChatColor.WHITE + plugin.isReduceKnockback());
        sender.sendMessage(ChatColor.YELLOW + "Normal cancelado: " + ChatColor.WHITE + plugin.cancelNormalKB());
        sender.sendMessage(ChatColor.YELLOW + "Crítico cancelado: " + ChatColor.WHITE + plugin.cancelCriticalKB());
        sender.sendMessage(ChatColor.YELLOW + "Debug: " + ChatColor.WHITE + plugin.isDebug());
    }
}
