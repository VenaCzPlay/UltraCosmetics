package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHorse;
import org.bukkit.entity.Horse;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

/**
 * Created by sacha on 10/08/15.
 */
public class MountAngelHorse extends Mount {

    public MountAngelHorse(UUID owner) {
        super(owner, MountType.ANGELHORSE
        );

        if (owner != null) {

            if (ent instanceof Horse) {
                Horse horse = (Horse) ent;

                horse.setColor(Horse.Color.WHITE);
                color = Horse.Color.WHITE;
                variant = Horse.Variant.HORSE;
                horse.setVariant(Horse.Variant.HORSE);

                EntityHorse entityHorse = ((CraftHorse) horse).getHandle();

                entityHorse.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5D);
                horse.setJumpStrength(1.9);
            }
            Bukkit.getScheduler().runTaskLater(Core.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    try {
                        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10000000, 1));
                    } catch (Exception exc) {

                    }
                }
            }, 1);
        }
    }

    @Override
    void onUpdate() {
        Location loc = ent.getLocation().add(0, 1, 0);
        UtilParticles.display(Particles.FIREWORKS_SPARK, 0.4f, 0.2f, 0.4f, loc, 5);
        UtilParticles.display(Particles.CLOUD, 0.4f, 0.2f, 0.4f, loc, 5);
    }
}
