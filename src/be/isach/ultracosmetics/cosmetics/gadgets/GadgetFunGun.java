package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Sacha on 12/10/15.
 */
public class GadgetFunGun extends Gadget {

    private List<Projectile> projectiles = new ArrayList<>();

    public GadgetFunGun(UUID owner) {
        super(owner, GadgetType.FUNGUN);

        if (owner != null)
            Bukkit.getPluginManager().registerEvents(this, Core.getPlugin());
    }

    @Override
    void onRightClick() {
        for (int i = 0; i < 5; i++)
            projectiles.add(getPlayer().launchProjectile(Snowball.class));
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (!projectiles.contains(projectile)) return;

        Location location = projectile.getLocation();

        for (Projectile snowball : projectiles)
            snowball.remove();

        UtilParticles.display(Particles.LAVA, 1.3f, 1f, 1.3f, location, 16);
        UtilParticles.display(Particles.HEART, 0.8f, 0.8f, 0.8f, location, 20);
        location.getWorld().playSound(location, Sound.CAT_MEOW, 2, 1);
    }

    @Override
    void onLeftClick() {
    }

    @Override
    void onUpdate() {}

    @Override
    public void onClear() {
        HandlerList.unregisterAll(this);
    }
}
