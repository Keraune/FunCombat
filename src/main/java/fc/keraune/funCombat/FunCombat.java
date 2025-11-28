package fc.keraune.funCombat;

import fc.keraune.funCombat.commands.FunCombatCommand;
import fc.keraune.funCombat.commands.FunCombatTabCompleter;
import fc.keraune.funCombat.listeners.PvPListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class FunCombat extends JavaPlugin {

    private static FunCombat instance;

    @Override
    public void onEnable() {
        instance = this;

        // Cargar configuración
        saveDefaultConfig();

        // Registrar eventos
        Bukkit.getPluginManager().registerEvents(new PvPListener(this), this);

        // Registrar comando
        FunCombatCommand cmd = new FunCombatCommand(this);
        getCommand("funcombat").setExecutor(cmd);
        getCommand("funcombat").setTabCompleter(new FunCombatTabCompleter());

        getLogger().info("FunCombat activado! Knockback configurable cargado.");
    }

    @Override
    public void onDisable() {
        getLogger().info("FunCombat desactivado!");
    }

    public static FunCombat getInstance() {
        return instance;
    }

    public void reloadPluginConfig() {
        reloadConfig();
    }

    // === GETTERS NUEVOS ===

    public boolean cancelNormalKB() {
        return getConfig().getBoolean("normal-knockback.cancel", true);
    }

    public boolean cancelCriticalKB() {
        return getConfig().getBoolean("critical-knockback.cancel", true);
    }

    public boolean isReduceKnockback() {
        return getConfig().getBoolean("reduce-knockback", true);
    }

    public boolean isDebug() {
        return getConfig().getBoolean("debug", false);
    }
}
