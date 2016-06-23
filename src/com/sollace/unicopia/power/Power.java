package com.sollace.unicopia.power;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.sollace.unicopia.UnicopiaPacketChannel;
import com.blazeloader.api.particles.ApiParticles;
import com.blazeloader.api.particles.ParticleData;
import com.blazeloader.util.shape.IShape;
import com.blazeloader.util.shape.Sphere;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Race;
import com.sollace.unicopia.Unicopia;
import com.sollace.unicopia.client.UKeyHandler;
import com.sollace.unicopia.network.PPacket;
import com.sollace.util.VecHelper;

/**
 * Base class for player's abilities
 * 
 * @param <T>	The type of data used when communicating
 */
public abstract class Power<T extends IData> {
	public static ArrayList<Power> powerRegistry = new ArrayList<Power>();
	private static HashMap<String, Power> nameToPowerMap = new HashMap<String, Power>();
	private static HashMap<Integer, ArrayList<Power>> keyToPower = new HashMap<Integer, ArrayList<Power>>();
	
	private final String Category;
	private final String Name;
	private final int Key;
	
	public static boolean keyHasRegisteredPower(int key) {
		return keyToPower.containsKey(key);
	}
	
	public static Power getCapablePowerFromKey(int key, Race race) {
		if (keyToPower.containsKey(key)) {
			for (Power i : keyToPower.get(key)) {
				if (i.canUse(race)) {
					return i;
				}
			}
		}
		return null;
	}
	
	public static Power powerFromName(String name) {
		if (nameToPowerMap.containsKey(name)) {
			return nameToPowerMap.get(name);
		}
		return null;
	}
	
	public Power(String name, int key) {
		this("unicopia.category.name", name, key);
	}
	
	public Power(String cat, String name, int key) {
		Category = cat;
		Name = name;
		Key = key;
		powerRegistry.add(this);
		nameToPowerMap.put(name, this);
		
		if (!keyToPower.containsKey(Key)) {
			keyToPower.put(Key, new ArrayList<Power>());
		}
		keyToPower.get(Key).add(this);
		
		if (Unicopia.isClient()) {
			UKeyHandler.RegisterKeyBinding(this);
		}
	}
	
	public String getKeyCategory() {
		return Category;
	}
	
	public final String getKeyName() {
		return Name;
	}
	
	public final int getDefaultKeyIndex() {
		return Key;
	}
		
	public final boolean Activated(EntityPlayer p, World w) {
		IData data = tryActivate(p, w);
		if (data != null) {
			UnicopiaPacketChannel.instance().sendToServer(new PPacket.Message(this, data));
			return true;
		}
		return false;
	}
	
	/**
	 * Subtracts a given food amount from the player. If not enough hunger available will subtract health.
	 */
	public static boolean TakeFromPlayer(EntityPlayer player, double foodSubtract) {
		if (!player.capabilities.isCreativeMode) {
			int food = (int)(player.getFoodStats().getFoodLevel() - foodSubtract);
			if (food < 0) {
				player.getFoodStats().addStats(-player.getFoodStats().getFoodLevel(), 0);
				player.attackEntityFrom(DamageSource.magic, -food);
			} else {
				player.getFoodStats().addStats((int)-foodSubtract, 0);
			}
		}
		return player.getHealth() > 0;
	}
	
	/**
	 * Gets the entity the player is currently looking at, or null.
	 */
	protected Entity getLookedAtEntity(EntityLivingBase e, int reach) {
		MovingObjectPosition objectMouseOver = VecHelper.getObjectMouseOver(e, reach, 1f);
		if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectType.ENTITY) {
			return objectMouseOver.entityHit;
		}
		return null;
	}
	
	/**
	 * Gets all entities within a given range from the player.
	 */
	public static List<Entity> getWithinRange(EntityPlayer player, double reach) {
		Vec3 look = player.getLook(0);
		float var9 = 1.0F;
		return player.worldObj.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().addCoord(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach).expand((double)var9, (double)var9, (double)var9));
	}
	
	protected void spawnParticles(ParticleData data, EntityPlayer player, int count) {
		double halfDist = player.getEyeHeight() / 1.5;
		double middle = player.getEntityBoundingBox().minY + halfDist;
		
		IShape shape = new Sphere(false, (float)halfDist);
		ApiParticles.spawnParticleShape(data, player.worldObj, player.posX, middle, player.posZ, shape, count);
	}
	
	protected double getPlayerEyeYPos(EntityPlayer player) {
		if (player.worldObj.isRemote) {
			return player.posY + player.getEyeHeight() - player.getYOffset();
		}
		return player.posY + player.getEyeHeight() - 1;
	}
	
	/**
	 * Returns the number of ticks the player must hold the ability key to trigger this ability.
	 */
	public abstract int getWarmupTime(PlayerExtension player);
	
	/**
	 * Returns the number of ticks allowed for cooldown
	 */
	public abstract int getCooldownTime(PlayerExtension player);
	
	/**
	 * Called to check preconditions for activating the ability.
	 * 
	 * @param w			The world
	 * @param player	The player
	 * @return	True to allow activation
	 */
	public abstract boolean canActivate(World w, EntityPlayer player);
	
	/**
	 * Checks if the given race is permitted to use this ability
	 * @param playerSpecies	The player's species
	 */
	public abstract boolean canUse(Race playerSpecies);
	
	/**
	 * Called on the client to activate the ability.
	 * 
	 * @param player	The player activating the ability
	 * @param w			The player's world
	 * @return	Data to be sent, or null if activation failed
	 */
	public abstract T tryActivate(EntityPlayer player, World w);
	
	public abstract T fromBytes(ByteBuf buf);
	
	/**
	 * Called to actually apply the ability.
	 * Only called on the server side.
	 * 
	 * @param player	The player that triggered the ability
	 * @param data		Data previously sent from the client
	 */
	public abstract void apply(EntityPlayer player, T data);
	
	/**
	 * Called just before the ability is activated.
	 * @param player	The current player
	 */
	public abstract void preApply(EntityPlayer player);
	
	/**
	 * Called every tick until the cooldown timer runs out.
	 * @param player	The current player
	 */
	public abstract void postApply(EntityPlayer player);
	
	/**
	 * An empty IData class *shrugs*
	 * 
	 */
	protected static class EmptyData implements IData {
		public EmptyData() {}
		public void toBytes(ByteBuf buf) {}
		public void fromBytes(ByteBuf buf) {}
		
	}
	
	/**
	 * Common IData class for sending location data.
	 * 
	 */
	protected static class LocationData implements IData {
		int x;
		int y;
		int z;
		
		public LocationData() {}
		
		public LocationData(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public LocationData(BlockPos pos) {
			x = pos.getX();
			y = pos.getY();
			z = pos.getZ();
		}
		
		public void toBytes(ByteBuf buf) {
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(z);
		}
		
		public void fromBytes(ByteBuf buf) {
			x = buf.readInt();
			y = buf.readInt();
			z = buf.readInt();
		}
	}
}