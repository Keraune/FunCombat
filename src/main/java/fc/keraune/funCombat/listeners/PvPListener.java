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
    private final Map<UUID, Boolean> recentPvPDamage; // True = crítico, False = normal

    public PvPListener(FunCombat plugin) {
        this.plugin = plugin;
        this.recentPvPDamage = new HashMap<>();
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

        // Guardar tipo de golpe para procesar en el evento de velocidad
        recentPvPDamage.put(victim.getUniqueId(), isCritical);

        // Debug
        if (plugin.isDebug()) {
            String attackType = isCritical ? "CRÍTICO" : "NORMAL";
            double horizontalMultiplier = isCritical ?
                    plugin.getCriticalHorizontalMultiplier() : plugin.getNormalHorizontalMultiplier();
            double verticalMultiplier = isCritical ?
                    plugin.getCriticalVerticalMultiplier() : plugin.getNormalVerticalMultiplier();

            damager.sendMessage("§e[FunCombat] " + attackType + " detectado");
            damager.sendMessage("§eMultiplicadores - Horizontal: " + horizontalMultiplier + "x, Vertical: " + verticalMultiplier + "x");
        }
    }

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        if (!plugin.isReduceKnockback()) {
            return;
        }

        Player player = event.getPlayer();

        // Verificar si este jugador recibió daño PvP recientemente
        Boolean isCritical = recentPvPDamage.get(player.getUniqueId());

        if (isCritical != null && System.currentTimeMillis() - getRecentDamageTime(player.getUniqueId()) < 500) {
            Vector originalVelocity = event.getVelocity();
            Vector modifiedVelocity = applyKnockbackMultipliers(originalVelocity, isCritical);

            // Debug detallado
            if (plugin.isDebug()) {
                player.sendMessage("§6=== FunCombat DEBUG ===");
                player.sendMessage("§eTipo: " + (isCritical ? "CRÍTICO" : "NORMAL"));
                player.sendMessage("§eOriginal: " + formatVector(originalVelocity));
                player.sendMessage("§eModificado: " + formatVector(modifiedVelocity));
            }

            // Aplicar la velocidad modificada
            event.setVelocity(modifiedVelocity);

            // Limpiar después de aplicar
            recentPvPDamage.remove(player.getUniqueId());
        }
    }

    /**
     * Aplica los multiplicadores de knockback
     */
    private Vector applyKnockbackMultipliers(Vector original, boolean isCritical) {
        double horizontalMultiplier = isCritical ?
                plugin.getCriticalHorizontalMultiplier() : plugin.getNormalHorizontalMultiplier();
        double verticalMultiplier = isCritical ?
                plugin.getCriticalVerticalMultiplier() : plugin.getNormalVerticalMultiplier();

        // Aplicar multiplicadores a cada componente
        double x = original.getX() * horizontalMultiplier;
        double y = original.getY() * verticalMultiplier;
        double z = original.getZ() * horizontalMultiplier;

        return new Vector(x, y, z);
    }

    /**
     * Formatea un vector para debug
     */
    private String formatVector(Vector vector) {
        return String.format("X=%.3f, Y=%.3f, Z=%.3f",
                vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Obtiene el timestamp del daño reciente (simulado)
     */
    private long getRecentDamageTime(UUID playerId) {
        // Usamos el tiempo actual menos un pequeño offset
        return System.currentTimeMillis() - 100;
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
        recentPvPDamage.entrySet().removeIf(entry ->
                currentTime - getRecentDamageTime(entry.getKey()) > 1000);
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
}