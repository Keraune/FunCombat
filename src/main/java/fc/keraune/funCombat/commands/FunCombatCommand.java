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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("funcombat.admin")) {
                sender.sendMessage(ChatColor.RED + "No tienes permiso para este comando.");
                return true;
            }

            plugin.reloadPluginConfig();
            sender.sendMessage(ChatColor.GREEN + "FunCombat recargado! Configuración actualizada.");
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
        sender.sendMessage(ChatColor.YELLOW + "/funcombat status " + ChatColor.WHITE + "- Ver estado");
    }

    private void sendStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== FunCombat Status ===");
        sender.sendMessage(ChatColor.GREEN + "Reducir knockback: " + plugin.isReduceKnockback());
        sender.sendMessage(ChatColor.GREEN + "Knockback horizontal: " +
                (plugin.isHorizontalEnabled() ?
                        (plugin.getHorizontalMultiplier() * 100) + "%" :
                        "DESACTIVADO (knockback normal)"));
        sender.sendMessage(ChatColor.GREEN + "Knockback vertical: " +
                (plugin.isVerticalEnabled() ?
                        (plugin.getVerticalMultiplier() * 100) + "%" :
                        "DESACTIVADO (knockback normal)"));
        sender.sendMessage(ChatColor.GREEN + "Knockback en críticos: " +
                (plugin.isCriticalKnockbackEnabled() ? "CONFIGURADO" : "DESACTIVADO (knockback normal)"));
        if (plugin.isCriticalKnockbackEnabled()) {
            sender.sendMessage(ChatColor.GREEN + "  • Multiplicador horizontal: " +
                    (plugin.getCriticalHorizontalMultiplier() * 100) + "%");
            sender.sendMessage(ChatColor.GREEN + "  • Multiplicador vertical: " +
                    (plugin.getCriticalVerticalMultiplier() * 100) + "%");
        }
        sender.sendMessage(ChatColor.GREEN + "Modo debug: " + plugin.isDebug());
    }
}