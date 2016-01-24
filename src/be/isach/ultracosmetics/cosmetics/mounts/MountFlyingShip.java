package be.isach.ultracosmetics.cosmetics.mounts;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import net.minecraft.server.v1_8_R3.EntityBoat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftBoat;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * Created by Matthew on 23/01/16.
 * Copy from iSach's Dragon Mount
 */
public class MountFlyingShip extends Mount {

	long nextAllowTime = 0;
	Entity currentboom = null;
	
    public MountFlyingShip(UUID owner) {
        super(owner, MountType.FLYINGSHIP);
        if (owner != null)
            Core.registerListener(this);
    }

    @Override
    void onUpdate() {
        if (ent.getPassenger() == null)
            clear();
     
        EntityBoat ec = ((CraftBoat) ent).getHandle();

     
        Vector vector = getPlayer().getLocation().toVector();

        double rotX = getPlayer().getLocation().getYaw();
        double rotY = getPlayer().getLocation().getPitch();

        vector.setY(-Math.sin(Math.toRadians(rotY)));

        double h = Math.cos(Math.toRadians(rotY));

        vector.setX(-h * Math.sin(Math.toRadians(rotX)));
        vector.setZ(h * Math.cos(Math.toRadians(rotX)));

        ec.getBukkitEntity().setVelocity(vector);

        ec.pitch = getPlayer().getLocation().getPitch();
        ec.yaw = getPlayer().getLocation().getYaw() - 180;
        if(currentboom != null){
        	if(currentboom.isDead()){
        		currentboom = null;
        		return;
        	}
        	currentboom.getWorld().playSound(currentboom.getLocation(), Sound.CLICK, 1, 1);
        	if(currentboom.isOnGround()){
        		Location l = currentboom.getLocation().clone();
        		for(Entity i : currentboom.getNearbyEntities(3, 3, 3)){
                    double dX = i.getLocation().getX() - currentboom.getLocation().getX();
                    double dY = i.getLocation().getY() - currentboom.getLocation().getY();
                    double dZ = i.getLocation().getZ() - currentboom.getLocation().getZ();
                    double yaw = Math.atan2(dZ, dX);
                    double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
                    double X = Math.sin(pitch) * Math.cos(yaw);
                    double Y = Math.sin(pitch) * Math.sin(yaw);
                    double Z = Math.cos(pitch);
        			MathUtils.applyVelocity(i,  new Vector(X, Z, Y));
        		}
        		UtilParticles.display(Particles.EXPLOSION_HUGE, l);
        		l.getWorld().playSound(l, Sound.EXPLODE, 1, 1);
        		currentboom.remove();
        		currentboom = null;
        	}
        }
    }

    @EventHandler
    public void stopBoatDamage(EntityExplodeEvent event) {
        Entity e = event.getEntity();
        if (e instanceof EntityBoat && e == ent)
            event.setCancelled(true);

    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event){
    	if(event.getAction() == Action.PHYSICAL ){
    		return;
    	}
    	if(event.getPlayer() != getPlayer()){
    		return;
    	}
    	if(event.getPlayer().getVehicle() == null  || event.getPlayer().getVehicle() != ent){
    		return;
    	}
    	
    	if(System.currentTimeMillis() < nextAllowTime){
    		getPlayer().playSound(getPlayer().getLocation(), Sound.ITEM_PICKUP, 1, 1);
    		return;
    	}
    	
		getPlayer().playSound(getPlayer().getLocation(), Sound.CLICK, 1, 1);
    	nextAllowTime = System.currentTimeMillis() + 10000;
    	currentboom = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), EntityType.PRIMED_TNT);
    	currentboom.setCustomName(ChatColor.RED+ ChatColor.BOLD.toString() + "!");
    	currentboom.setCustomNameVisible(true);
    	
    	if(currentboom instanceof LivingEntity){
    		((LivingEntity)currentboom).setNoDamageTicks(-1);
    		if(currentboom instanceof Animals){
    			((Animals)currentboom).setBreed(false);
    		}
    	}
    	
    	
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity e = event.getDamager();
        if (e instanceof EntityBoat && e == ent) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBoatBreak(VehicleDestroyEvent event){
    	Entity e = event.getVehicle();
    	if(e == ent){
    		event.setCancelled(true);
    	}
    }
    @Override
    public void onClear(){
    	if(currentboom != null){
    		currentboom.remove();
    	}
    }
    
}
