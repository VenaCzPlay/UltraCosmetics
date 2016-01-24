package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHorse;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by sacha on 10/08/15.
 */
public class MountOfFire extends Mount {

    public MountOfFire(UUID owner) {
        super(owner, MountType.MOUNTOFFIRE);
        if (owner != null) {
            Core.registerListener(this);
            Horse horse = (Horse) ent;
            horse.setColor(Horse.Color.CREAMY);
            horse.setVariant(Horse.Variant.HORSE);
            color = Horse.Color.CREAMY;
            variant = Horse.Variant.HORSE;
            horse.setJumpStrength(0.7);
            EntityHorse entityHorse = ((CraftHorse) horse).getHandle();
            entityHorse.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.4);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer() == getPlayer()
                && Core.getCustomPlayer(getPlayer()).currentMount == this
                && (boolean) SettingsManager.getConfig().get("Mounts-Block-Trails")) {
            List<Byte> datas = new ArrayList<>();
            datas.add((byte) 0x1);
            datas.add((byte) 0x4);
            datas.add((byte) 0xe);
            for (Block b : BlockUtils.getBlocksInRadius(event.getPlayer().getLocation(), 3, false))
                if (b.getLocation().getBlockY() == event.getPlayer().getLocation().getBlockY() - 1)
                    BlockUtils.setToRestore(b, Material.STAINED_CLAY, datas.get(new Random().nextInt(2)), 20);
        }
    }

    @Override
    void onUpdate() {
        UtilParticles.display(Particles.FLAME, 0.4f, 0.2f, 0.4f, ent.getLocation().clone().add(0, 1, 0), 5);
    }
}
