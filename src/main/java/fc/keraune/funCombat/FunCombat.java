package fc.keraune.funCombat;

import fc.keraune.funCombat.commands.FunCombatCommand;
import fc.keraune.funCombat.commands.FunCombatTabCompleter;
import fc.keraune.funCombat.listeners.PvPListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class FunCombat extends JavaPlugin {

    private static FunCombat instance;
    private PvPListener pvpListener;

    @Override
    public void onEnable() {
        instance = this;

        // Cargar configuración
        saveDefaultConfig();

        // Registrar eventos
        this.pvpListener = new PvPListener(this);
        Bukkit.getPluginManager().registerEvents(pvpListener, this);

        // Registrar comando y autocompletado
        FunCombatCommand commandExecutor = new FunCombatCommand(this);
        getCommand("funcombat").setExecutor(commandExecutor);
        getCommand("funcombat").setTabCompleter(new FunCombatTabCompleter());

        // Tarea para limpiar entradas antiguas cada minuto
        new BukkitRunnable() {
            @Override
            public void run() {
                pvpListener.cleanOldEntries();
            }
        }.runTaskTimer(this, 1200L, 1200L);

        getLogger().info("FunCombat activado! Sistema de PvP con knockback configurable.");
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

    // Getters para la configuración
    public boolean isReduceKnockback() {
        return getConfig().getBoolean("reduce-knockback", true);
    }

    // Getters para knockback NORMAL
    public double getNormalHorizontalMultiplier() {
        return getConfig().getDouble("normal-knockback.horizontal-multiplier", 0.2);
    }

    public double getNormalVerticalMultiplier() {
        return getConfig().getDouble("normal-knockback.vertical-multiplier", 0.0);
    }

    // Getters para knockback CRÍTICO
    public double getCriticalHorizontalMultiplier() {
        return getConfig().getDouble("critical-knockback.horizontal-multiplier", 0.1);
    }

    public double getCriticalVerticalMultiplier() {
        return getConfig().getDouble("critical-knockback.vertical-multiplier", 0.0);
    }

    public boolean isDebug() {
        return getConfig().getBoolean("debug", false);
    }
}