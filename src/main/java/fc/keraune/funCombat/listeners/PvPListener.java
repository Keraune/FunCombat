package fc.keraune.funCombat.listeners;

import fc.keraune.funCombat.FunCombat;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PvPListener implements Listener {

    private final FunCombat plugin;
    private final Map<UUID, KnockbackData> recentDamage;

    public PvPListener(FunCombat plugin) {
        this.plugin = plugin;
        this.recentDamage = new HashMap<>();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!plugin.isReduceKnockback()) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player damager = getDamager(event);

        // Solo aplicar entre jugadores (PvP)
        if (damager == null || victim.equals(damager)) {
            return;
        }

        // Verificar si es golpe crítico
        boolean isCritical = isCriticalHit(damager);

        // Guardar datos del knockback
        KnockbackData data = new KnockbackData(isCritical);
        recentDamage.put(victim.getUniqueId(), data);

        // Debug
        if (plugin.isDebug()) {
            String attackType = isCritical ? "CRÍTICO" : "NORMAL";
            damager.sendMessage("§e[FunCombat] " + attackType + " - Knockback configurado");
            damager.sendMessage("§eConfig Horizontal: " + plugin.isHorizontalEnabled() +
                    " (" + plugin.getHorizontalMultiplier() + ")");
            damager.sendMessage("§eConfig Vertical: " + plugin.isVerticalEnabled() +
                    " (" + plugin.getVerticalMultiplier() + ")");
            damager.sendMessage("§eConfig Críticos: " + plugin.isCriticalKnockbackEnabled() +
                    " H(" + plugin.getCriticalHorizontalMultiplier() +
                    ") V(" + plugin.getCriticalVerticalMultiplier() + ")");
        }
    }

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        if (!plugin.isReduceKnockback()) {
            return;
        }

        Player player = event.getPlayer();
        KnockbackData data = recentDamage.get(player.getUniqueId());

        if (data != null && System.currentTimeMillis() - data.timestamp < 500) {
            Vector originalVelocity = event.getVelocity();
            Vector modifiedVelocity = modifyKnockback(originalVelocity, data.isCritical);

            // Debug detallado
            if (plugin.isDebug()) {
                player.sendMessage("§6=== FunCombat DEBUG ===");
                player.sendMessage("§eTipo de golpe: " + (data.isCritical ? "CRÍTICO" : "NORMAL"));
                player.sendMessage("§eVelocidad original: " +
                        String.format("X=%.3f, Y=%.3f, Z=%.3f",
                                originalVelocity.getX(), originalVelocity.getY(), originalVelocity.getZ()));
                player.sendMessage("§eVelocidad modificada: " +
                        String.format("X=%.3f, Y=%.3f, Z=%.3f",
                                modifiedVelocity.getX(), modifiedVelocity.getY(), modifiedVelocity.getZ()));
            }

            // Aplicar la velocidad modificada
            event.setVelocity(modifiedVelocity);

            // Limpiar después de aplicar
            recentDamage.remove(player.getUniqueId());
        }
    }

    /**
     * Modifica el knockback según la configuración
     */
    private Vector modifyKnockback(Vector original, boolean isCritical) {
        double x = original.getX();
        double y = original.getY();
        double z = original.getZ();

        if (isCritical) {
            // Para críticos: usar configuración específica si está activada
            if (plugin.isCriticalKnockbackEnabled()) {
                x *= plugin.getCriticalHorizontalMultiplier();
                z *= plugin.getCriticalHorizontalMultiplier();
                y *= plugin.getCriticalVerticalMultiplier();
            }
            // Si critical-knockback.enabled: false → NO modificar (knockback normal)
        } else {
            // Para golpes normales: usar configuración general si está activada
            if (plugin.isHorizontalEnabled()) {
                x *= plugin.getHorizontalMultiplier();
                z *= plugin.getHorizontalMultiplier();
            }
            // Si horizontal-knockback.enabled: false → NO modificar (knockback normal)

            if (plugin.isVerticalEnabled()) {
                y *= plugin.getVerticalMultiplier();
            }
            // Si vertical-knockback.enabled: false → NO modificar (knockback normal)
        }

        return new Vector(x, y, z);
    }

    /**
     * Verifica si es un golpe crítico
     */
    private boolean isCriticalHit(Player player) {
        return player.getFallDistance() > 0.0F &&
                !player.isOnGround() &&
                !player.isInWater() &&
                !player.isClimbing() &&
                !player.hasPotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS);
    }

    /**
     * Limpiar mapa periódicamente
     */
    public void cleanOldEntries() {
        long currentTime = System.currentTimeMillis();
        recentDamage.entrySet().removeIf(entry -> currentTime - entry.getValue().timestamp > 1000);
    }

    /**
     * Obtiene el jugador que causó el daño
     */
    private Player getDamager(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            return (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                return (Player) projectile.getShooter();
            }
        }
        return null;
    }

    /**
     * Clase para almacenar datos del knockback
     */
    private static class KnockbackData {
        final boolean isCritical;
        final long timestamp;

        KnockbackData(boolean isCritical) {
            this.isCritical = isCritical;
            this.timestamp = System.currentTimeMillis();
        }
    }
}