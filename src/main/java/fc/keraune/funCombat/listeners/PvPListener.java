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
    private final Map<UUID, Boolean> recentHits; // True = crítico, False = normal

    public PvPListener(FunCombat plugin) {
        this.plugin = plugin;
        this.recentHits = new HashMap<>();
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!plugin.isReduceKnockback()) return;
        if (!(event.getEntity() instanceof Player victim)) return;

        Player damager = getDamager(event);
        if (damager == null) return;

        boolean isCritical = isCriticalHit(damager);

        recentHits.put(victim.getUniqueId(), isCritical);

        if (plugin.isDebug()) {
            damager.sendMessage("§e[FunCombat] Golpe detectado: §6" + (isCritical ? "CRÍTICO" : "NORMAL"));
        }
    }

    @EventHandler
    public void onVelocity(PlayerVelocityEvent event) {
        if (!plugin.isReduceKnockback()) return;

        Player player = event.getPlayer();
        Boolean critical = recentHits.remove(player.getUniqueId());

        if (critical == null) return;

        boolean cancelKB = critical ? plugin.cancelCriticalKB() : plugin.cancelNormalKB();

        if (cancelKB) {
            event.setVelocity(new Vector(0, 0, 0));
        }

        if (plugin.isDebug()) {
            player.sendMessage("§6=== FunCombat DEBUG ===");
            player.sendMessage("§eTipo: " + (critical ? "CRÍTICO" : "NORMAL"));
            player.sendMessage("§eKnockback cancelado: " + cancelKB);
        }
    }

    private boolean isCriticalHit(Player p) {
        return p.getFallDistance() > 0 &&
                !p.isOnGround() &&
                !p.isInWater() &&
                !p.isClimbing() &&
                !p.hasPotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS);
    }

    private Player getDamager(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player p) return p;

        if (event.getDamager() instanceof Projectile proj &&
                proj.getShooter() instanceof Player p)
            return p;

        return null;
    }
}
