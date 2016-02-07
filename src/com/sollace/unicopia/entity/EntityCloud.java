package com.sollace.unicopia.entity;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.blazeloader.api.particles.ApiParticles;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Settings;
import com.sollace.unicopia.Unicopia;
import com.sollace.unicopia.client.ClientSide;
import com.sollace.unicopia.server.PlayerSpeciesRegister;
import com.sollace.util.VecHelper;

public class EntityCloud extends EntityFlying implements IAnimals {
	
	private double altitude;
	
	private final double baseWidth = 3f;
	private final double baseHeight = 0.8f;
	
	public EntityCloud(World par1World) {
		super(par1World);
		preventEntitySpawning = false;
		ignoreFrustumCheck = true;
		if (worldObj.rand.nextInt(20) == 0 && canRainHere()) {
			setRaining();
			if (worldObj.rand.nextInt(20) == 0) {
				setIsThundering(true);
			}
		}
		setCloudSize(1 + rand.nextInt(3));
	}
	
	protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(12, (Integer)0);
        dataWatcher.addObject(13, new Byte((byte)0));
        dataWatcher.addObject(14, new Byte((byte)1));
        dataWatcher.addObject(16, new Byte((byte)0));
        dataWatcher.addObject(17, new Byte((byte)0));
    }
	
    protected String getHurtSound() {
    	return "step.cloth";
	}
    
    protected String getDeathSound() {
    	return "step.cloth";
	}
    
    protected Item getDropItem() {
    	return Unicopia.UItems.cloudMatter;
	}
    
    public boolean canBeSteered() {
    	return true;
	}
        
    protected boolean canTriggerWalking() {
    	return false;
	}
    
    public boolean doesEntityNotTriggerPressurePlate() {
    	return true;
	}
    
    public boolean canRenderOnFire() {
    	return false;
	}
    
    public int getBrightnessForRender(float renderPartialTicks) {
    	return 15728640;
	}
    
	protected boolean canDespawn() {
		return false;// !hasCustomName() && !getStationary() && !getOpaque();
	}
    
    public int getMaxSpawnedInChunk() {
    	return 6;
	}
    
    public void onStruckByLightning(EntityLightningBolt par1EntityLightningBolt) {
    	
    }
    
    protected void collideWithEntity(Entity other) {
    	if (EntityCloud.class.isAssignableFrom(other.getClass())) super.collideWithEntity(other);
    }
    
    protected void collideWithNearbyEntities() {
    	
    }
    
    protected void checkLocation() {
    	if (posY < worldObj.provider.getCloudHeight() - 18) {
    		setLocationAndAngles(posX, worldObj.provider.getCloudHeight() - 18, posZ, rotationYaw, rotationPitch);
        }
    	super.collideWithNearbyEntities();
    }

    public void applyEntityCollision(Entity other) {
    	if (EntityPlayer.class.isAssignableFrom(other.getClass())) {
    		if (PlayerSpeciesRegister.getPlayerSpecies((EntityPlayer)other).canInteractWithClouds()) {
    			super.applyEntityCollision(other);
    		}
    	} else if (EntityCloud.class.isAssignableFrom(other.getClass())) {
			super.applyEntityCollision(other);
    	}
    }
    
    public static double randomIn(Random rand, double min, double max) {
    	double range = (max - min) * 1000;
    	return (rand.nextInt((int)(range)) - (range/2)) / 1000;
    }
    
    public void onUpdate() {
    	AxisAlignedBB boundingbox = getEntityBoundingBox();
    	if (getIsRaining()) {
    		if (worldObj.isRemote) {
    			for (int i = 0; i < 30 * getCloudSize(); i++) {
		    		double x = posX + randomIn(rand, boundingbox.minX, boundingbox.maxX);
		    		double y = posY;
		    		double z = posZ + randomIn(rand, boundingbox.minX, boundingbox.maxX);
	    			ClientSide.spawnParticle(canSnowHere(new BlockPos(x, y, z)) ? "snowshovel" : "rain", worldObj, x, y, z, 0, 0, 0);
	    		}
    			AxisAlignedBB rainedArea = boundingbox.expand(1, 0, 1);
    			rainedArea = AxisAlignedBB.fromBounds(boundingbox.minX, rainedArea.minY - (posY - getBlockUnder(new BlockPos(posX, posY, posZ))), boundingbox.minZ, boundingbox.maxX, boundingbox.maxY, boundingbox.maxZ);
    			
    			List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, rainedArea);
    			if (players != null) {
    				for (EntityPlayer j : players) {
    					if (j == Minecraft.getMinecraft().thePlayer) {
    						worldObj.playSound((int)j.posX, (int)j.posY, (int)j.posZ, "ambient.weather.rain", 0.1F, 0.6F, false);
    					}
    				}
    			}
	    	}
    		
    		int x = (int)posX + rand.nextInt((int)width) - (int)(width/2);
		    int z = (int)posZ + rand.nextInt((int)width) - (int)(width/2);
	    	
	    	int y = getBlockUnder(new BlockPos(x, 0, z));
    		
	    	if (getIsThundering()) {
		    	if (rand.nextInt(3000) == 0) Thunder(y);
		    	if (rand.nextInt(200) == 0) setIsThundering(false);
	    	}
	    	
	    	if (rand.nextInt(20) == 0) {
	    		BlockPos pos = new BlockPos(x, y, z);
	    		BlockPos below = pos.down();
		    	IBlockState state = worldObj.getBlockState(below);
		    	if (state.getBlock() != null) {
		    		if (worldObj.canBlockFreezeWater(below)) {
		    			worldObj.setBlockState(below, Blocks.ice.getDefaultState());
		    		}
		    		
		    		if (worldObj.canSnowAt(pos, false)) {
	    				worldObj.setBlockState(pos, Blocks.snow_layer.getDefaultState());
		    		}
		    		
		    		int meta = state.getBlock().getMetaFromState(state);
		    		if (BlockFarmland.class.isAssignableFrom(state.getBlock().getClass())) {
		    			worldObj.setBlockState(below, state.getBlock().getStateFromMeta(meta + 1));
		    		} else if (BlockCrops.class.isAssignableFrom(state.getBlock().getClass())) {
		    			if (meta < 7) {
		    				worldObj.setBlockState(below, state.getBlock().getStateFromMeta(meta + 1), 2);
		    			}
		    		}
		    		
		    		state.getBlock().fillWithRain(worldObj, below);
		    	}
	    	}
	    	
    		if (setRainTimer(getRainTimer() - 1) == 0) {
    			if (rand.nextInt(20000) == 0) {
    				setDead();
    			}
    		}
    	} else {
        	if (rand.nextInt(8000) == 0 && canRainHere()) {
        		setRaining();
        		if (rand.nextInt(7000) == 0) {
        			setIsThundering(true);
        		}
        	}
    	}
    	
        if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase) {
        	float speedModifier = 1.5f;
            EntityLivingBase entitylivingbase = (EntityLivingBase)riddenByEntity;
            float f = riddenByEntity.rotationYaw - entitylivingbase.moveStrafing * 90.0F;
            motionX += -Math.sin((double)(f * (float)Math.PI / 180.0F)) * speedModifier * (double)entitylivingbase.moveForward * 0.05000000074505806D;
            motionZ += Math.cos((double)(f * (float)Math.PI / 180.0F)) * speedModifier * (double)entitylivingbase.moveForward * 0.05000000074505806D;
            
            float g = riddenByEntity.rotationPitch;
            motionY += -Math.sin((double)(g * (float)Math.PI / 180.0F)) * (double)entitylivingbase.moveForward * 0.05000000074505806D;
        } else {
        	motionY = 0;
        }
        
        rotationPitch = 0;
        rotationYawHead = 0;
        rotationYaw = 0;
        
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingbox);
        if (list != null) {
        	for (int i = 0; i < list.size(); ++i) {
        		if (list.get(i) instanceof EntityPlayer) {
        			if (((EntityPlayer)list.get(i)).posY > posY + 1) {
        				floatPlayer((EntityPlayer)list.get(i));
        			}
        		}
        	}
        }
        
		if (isBurning() && !dead) {
			double x = posX + randomIn(rand, boundingbox.minX, boundingbox.maxX);
			double y = posY + randomIn(rand, boundingbox.minY, boundingbox.maxY);
			double z = posZ + randomIn(rand, boundingbox.minZ, boundingbox.maxZ);
			ClientSide.spawnParticle("cloud", worldObj, x, y, z, 0, 0.25, 0);
		}
        
    	super.onUpdate();
    	
    	hurtTime = 0;
    }
    
    public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_) {
    	if (!getStationary()) {
    		super.moveEntityWithHeading(p_70612_1_, p_70612_2_);
    	}
    }
        
    public void onCollideWithPlayer(EntityPlayer player) {
    	if (player.posY > posY + 1) {
	    	if (floatPlayer(player)) {
	    		double difX = player.posX - player.lastTickPosX;
	    		double difZ = player.posZ - player.lastTickPosZ;
	    		double difY = player.posY - player.lastTickPosY;
	            player.distanceWalkedModified = (float)(player.distanceWalkedModified + MathHelper.sqrt_double(difX * difX + difZ * difZ) * 0.6);
	            player.distanceWalkedOnStepModified = (float)(player.distanceWalkedOnStepModified + MathHelper.sqrt_double(difX * difX + difY * difY + difZ * difZ) * 0.6);
	    		
	            PlayerExtension prop = PlayerExtension.get(player);
	    		if ((player.fallDistance > 1f) || player.distanceWalkedOnStepModified > prop.nextStepDistance) {
	    			prop.nextStepDistance = player.distanceWalkedOnStepModified + 2f;
	    			player.fallDistance = 0;
					Block.SoundType soundtype = Blocks.wool.stepSound;
		            player.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getFrequency());
	    		}
	    	}
    	}
    	super.onCollideWithPlayer(player);
    }
    
    protected void updateAITasks() {
    	if (!getStationary()) {
    		super.updateAITasks();
	        if (riddenByEntity == null) {
		        double diffY = altitude - posZ;
		        
		        if (rand.nextInt(700) == 0 || diffY*diffY < 3f) {
		            altitude += rand.nextInt(10) - 5;
		        }
        		
		        if (altitude > worldObj.provider.getCloudHeight() - 8) {
		        	altitude = worldObj.provider.getCloudHeight() - 18;
		        } else if (altitude < 70) {
		        	altitude = posY + 10;
		        }
		        
		        double var3 = altitude + 0.1D - posY;
		        motionX -= 0.001;
		        motionY += (Math.signum(var3) * 0.699999988079071D - motionY) * 0.10000000149011612D;
	        }
    	}
    }
    
    public boolean interact(EntityPlayer player) {
        if (super.interact(player)) {
            return true;
        } else if ((riddenByEntity == null || riddenByEntity == player)) {
        	if (PlayerSpeciesRegister.getPlayerSpecies(player).canInteractWithClouds()) {
        		ItemStack stack = player.getHeldItem();
        		if (stack != null) {
        			if (!worldObj.isRemote && stack.getItem() == Items.spawn_egg && stack.getItemDamage() == Settings.getEntityId("cloud")) {
        				if (spawnCreature(player, stack)) return true;
        			} else if (getStationary() && ItemBlock.class.isAssignableFrom(stack.getItem().getClass())) {
        				placeBlock(player, stack);
        				return true;
        			}
        		}
        		if (!worldObj.isRemote && !getStationary()) {
        			player.mountEntity(this);
        			return true;
        		}
        	}
        }
        return false;
    }
        
    public void handleHealthUpdate(byte type) {
    	if (type == 2) {
    		if (worldObj.isRemote && !isBurning()) {
    			for (int i = 0; i < 50 * getCloudSize(); i++) {
    				ApiParticles.addBlockHitEffectsToEntity(this, Unicopia.UBlocks.cloud.getDefaultState());
    			}
    		}
    	}
    	super.handleHealthUpdate(type);
    }
    
    public boolean attackEntityFrom(DamageSource source, float amount) {
    	Entity attacker = source.getSourceOfDamage();
    	if (attacker != null) {
    		if (EntityPlayer.class.isAssignableFrom(attacker.getClass())) {
    			return onAttackByPlayer(source, amount, (EntityPlayer)attacker);
    		}
    		return false;
    	}
		return source == DamageSource.inWall || super.attackEntityFrom(source, amount);
    }
    
    private boolean spawnCreature(EntityPlayer player, ItemStack stack) {
    	Entity entity = ItemMonsterPlacer.spawnCreature(worldObj, stack.getItemDamage(), posX, posY + height, posZ);
        if (entity != null) {
            if (entity instanceof EntityLivingBase && stack.hasDisplayName()) {
                ((EntityLiving)entity).setCustomNameTag(stack.getDisplayName());
            }
            
            if (!player.capabilities.isCreativeMode) stack.stackSize--;
            return true;
        }
        return false;
    }
    
    private void placeBlock(EntityPlayer player, ItemStack stack) {
    	if (worldObj.isRemote) {
	    	double distance = ClientSide.getReach();
			Vec3 pos = VecHelper.geteEyePosition(player, 1);
			Vec3 look = player.getLook(1);
	        Vec3 ray = pos.addVector(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);
	        double size = getCollisionBorderSize() * 2;
			MovingObjectPosition trace = getEntityBoundingBox().expand(size, size, size).calculateIntercept(pos, ray);
			if (trace != null) {
				double x = trace.hitVec.xCoord;
				double y = trace.hitVec.yCoord;
				double z = trace.hitVec.zCoord;
				
				double fX = MathHelper.floor_double(x);
				double fY = MathHelper.floor_double(y);
				double fZ = MathHelper.floor_double(z);
				
				int stackSize = stack.stackSize;
				
				if (trace.sideHit == EnumFacing.UP) {
					fY--;
					y--;
				}
				if (trace.sideHit == EnumFacing.DOWN) {
					fY+=2;
					y+=2;
				}
				
				ClientSide.sendBlockPlacement(player, fX, fY, fZ, x, y, z, trace.sideHit);
				if (stack.onItemUse(player, worldObj, new BlockPos(fX, fY, fZ), trace.sideHit, (float)(x - fX), (float)(y - fY), (float)(z - fZ))) {
					player.swingItem();
				}
				
				if (player.capabilities.isCreativeMode) {
					stack.stackSize = stackSize;
				}
			}
    	}
    }
    
    private boolean onAttackByPlayer(DamageSource source, float amount, EntityPlayer player) {
    	boolean canFly = PlayerSpeciesRegister.getPlayerSpecies(player).canInteractWithClouds();
    	boolean stat = getStationary();
    	if (stat || canFly) {
			ItemStack stack = (ItemStack)player.inventory.mainInventory[player.inventory.currentItem];
			if (stack != null && ItemSword.class.isAssignableFrom(stack.getItem().getClass())) {
				return super.attackEntityFrom(source, amount);
			} else if (stack != null && ItemSpade.class.isAssignableFrom(stack.getItem().getClass())) {
				return super.attackEntityFrom(source, amount * 1.5f);
			} else if (canFly) {
				if (player.posY < posY) {
	    			altitude += 5;
	    		} else if (player.posY > posY) {
	    			altitude -= 5;
	    		}
			}
    	}
		return false;
    }
    
    public void onDeath(DamageSource s) {
    	if (s == DamageSource.generic || (s.getEntity() != null && EntityPlayer.class.isAssignableFrom(s.getEntity().getClass()))) {
    		if (worldObj.isRemote) {
    			if (!isBurning()) ApiParticles.addBlockDestroyEffectsToEntity(this, Unicopia.UBlocks.cloud.getDefaultState());
    			setDead();
        	}
    	}
    	super.onDeath(s);
    }
    
    protected void dropFewItems(boolean hitByPlayer, int looting) {
    	if (hitByPlayer) {
	    	Item item = getDropItem();
	    	int amount = 2 + worldObj.rand.nextInt(3 + looting);
	    	for (int i = 0; i < amount; i++) {
	    		dropItem(item, 1);
	    	}
    	}
    }
            
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        setRainTimer(tag.getInteger("RainTimer"));
        setIsThundering(tag.getBoolean("IsThundering"));
        setCloudSize(tag.getByte("CloudSize"));
        setStationary(tag.getBoolean("IsStationary"));
        setOpaque(tag.getBoolean("IsOpaque"));
    }
    
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("RainTimer", getRainTimer());
        tag.setBoolean("IsThundering", getIsThundering());
        tag.setByte("CloudSize", (byte)getCloudSize());
        tag.setBoolean("IsStationary", getStationary());
        tag.setBoolean("IsOpaque", getOpaque());
    }
    
    private boolean floatPlayer(EntityPlayer player) {
    	int floatStrength = getFloatStrength(player);
    	if (player != riddenByEntity && floatStrength > 0) {
    		double boundModifier = player.fallDistance > 80 ? 80 : MathHelper.floor_double(player.fallDistance * 10)/10;
    		player.onGround = true;
			player.motionY += (((floatStrength > 2 ? 1 : floatStrength/2) * 0.699999998079071D) - player.motionY + boundModifier*0.7) * 0.10000000149011612D;
			if (!getStationary() && player.motionY > 0.4 && worldObj.rand.nextInt(900) == 0) {
				Thunder((int)posY);
			}
			return true;
    	}
    	return false;
    }
    
	public int getFloatStrength(EntityPlayer player) {
		if (PlayerSpeciesRegister.getPlayerSpecies(player).canInteractWithClouds()) return 2;
		if (getOpaque()) {
			return getFeatherEnchantStrength(player);
		}
		return 0;
	}
	
	public static int getFeatherEnchantStrength(EntityPlayer player) {
		for (int i = 0; i < 5; i++) {
			ItemStack stack = player.getEquipmentInSlot(i);
			if (stack != null) {
				Map enchantments = EnchantmentHelper.getEnchantments(stack);
				if (enchantments.containsKey(Enchantment.featherFalling.effectId)) {
					return (Integer)enchantments.get(Enchantment.featherFalling.effectId);
				}
			}
		}
		return 0;
	}
	
	private boolean canRainHere() {
		return worldObj.getBiomeGenForCoords(new BlockPos(posX, posY, posZ)).canSpawnLightningBolt();
	}
	
	private boolean canSnowHere(BlockPos pos) {
		return worldObj.getBiomeGenForCoords(pos).getFloatTemperature(pos) <= 0.15f;
	}
	
    public void Thunder() {
    	Thunder(getBlockUnder(new BlockPos(posX, posY, posZ)));
    }
    
    public void Thunder(int altitude) {
    	worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, posX, altitude, posZ));
    }
    
    private int getBlockUnder(BlockPos pos) {
    	int y = worldObj.getTopSolidOrLiquidBlock(pos).getY();
    	if (y >= posY) {
    		y = (int)posY;
    		while (worldObj.getBlockState(pos).getBlock() == Blocks.air) {
    			pos = pos.down();
    		}
    		pos.up();
    	}
    	return pos.getY();
    }
    
    public int getRainTimer() {
    	return dataWatcher.getWatchableObjectInt(12);
    }
    
    public int setRainTimer(int val) {
    	if (val < 0) val = 0;
    	dataWatcher.updateObject(12, val);
    	return val;
    }
    
    private void setRaining() {
    	setRainTimer(700 + rand.nextInt(20));
    }
    
    public void setIsRaining(boolean val) {
    	if (val) {
    		setRaining();
    	} else {
    		setRainTimer(0);
    	}
    }
    
    public boolean getIsRaining() {
    	return getRainTimer() > 0;
    }
    
    public boolean getIsThundering() {
    	return dataWatcher.getWatchableObjectByte(13) == 1;
    }
    
    public void setIsThundering(boolean val) {
    	dataWatcher.updateObject(13, Byte.valueOf((byte)(val ? 1 : 0)));
    }
        
    private int getCloudSizeInternal() {
    	return dataWatcher.getWatchableObjectByte(14);
    }
    
    public boolean getStationary() {
    	return dataWatcher.getWatchableObjectByte(16) == 1;
    }
    
    public void setStationary(boolean v) {
    	dataWatcher.updateObject(16, Byte.valueOf((byte)(v ? 1 : 0)));
    }
    
    public boolean getOpaque() {
    	return dataWatcher.getWatchableObjectByte(17) == 1;
    }
    
    public void setOpaque(boolean v) {
    	dataWatcher.updateObject(17, Byte.valueOf((byte)(v ? 1 : 0)));
    }
    
    public int getCloudSize() {
    	int size = getCloudSizeInternal();
    	updateSize(size);
    	return size;
    }
    
    private void updateSize(int scale) {
    	if (width != (baseWidth * scale) || height != (baseHeight * scale)) {
			setSize((float)(baseWidth * scale), (float)(baseHeight * scale));
		}
    	setPosition(posX, posY, posZ);
    }
    
    public void setCloudSize(int val) {
    	if (val < 1) {
    		val = 1;
    	}
    	updateSize(val);
    	dataWatcher.updateObject(14, Byte.valueOf((byte)(val)));
    }
}
