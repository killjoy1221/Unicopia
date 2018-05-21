package com.sollace.unicopia.entity;

import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockFire;
import net.minecraft.block.SoundType;
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
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import com.blazeloader.api.particles.ApiParticles;
import com.blazeloader.api.particles.ParticleData;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Settings;
import com.sollace.unicopia.Unicopia;
import com.sollace.unicopia.client.ClientSide;
import com.sollace.unicopia.server.PlayerSpeciesRegister;
import com.sollace.util.VecHelper;

public class EntityCloud extends EntityFlying implements IAnimals {
	
	private double altitude;
	private float ridingHeight;
	
	private final double baseWidth = 3f;
	private final double baseHeight = 0.8f;
	
	public EntityCloud(World par1World) {
		super(par1World);
		preventEntitySpawning = false;
		ignoreFrustumCheck = true;
		if (world.rand.nextInt(20) == 0 && canRainHere()) {
			setRaining();
			if (world.rand.nextInt(20) == 0) {
				setIsThundering(true);
			}
		}
		setCloudSize(1 + rand.nextInt(3));
	}
	
	private static final DataParameter<Integer> RAINTIMER = EntityDataManager.createKey(EntityCloud.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> THUNDERING = EntityDataManager.createKey(EntityCloud.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> SCALE = EntityDataManager.createKey(EntityCloud.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> STATIONARY = EntityDataManager.createKey(EntityCloud.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> OPAQUE = EntityDataManager.createKey(EntityCloud.class, DataSerializers.BOOLEAN);
	
	protected void entityInit() {
        super.entityInit();
        dataManager.register(RAINTIMER, 0);
        dataManager.register(THUNDERING, false);
        dataManager.register(SCALE, 1);
        dataManager.register(STATIONARY, false);
        dataManager.register(OPAQUE, false);
    }
	
    protected SoundEvent getHurtSound(DamageSource damageSource) {
    	return SoundEvents.BLOCK_CLOTH_HIT;
	}
    
    protected SoundEvent getDeathSound() {
    	return SoundEvents.BLOCK_CLOTH_STEP;
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
    	if (posY < world.provider.getCloudHeight() - 18) {
    		setLocationAndAngles(posX, world.provider.getCloudHeight() - 18, posZ, rotationYaw, rotationPitch);
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
    
	private final ParticleData data = Unicopia.Particles.rain.getData();
    
    public void onUpdate() {
    	AxisAlignedBB boundingbox = getEntityBoundingBox();
    	if (getIsRaining()) {
    		if (world.isRemote) {
    			for (int i = 0; i < 30 * getCloudSize(); i++) {
		    		double x = posX + randomIn(rand, boundingbox.minX, boundingbox.maxX);
		    		double y = getEntityBoundingBox().minY + height/2;
		    		double z = posZ + randomIn(rand, boundingbox.minX, boundingbox.maxX);
		    		if (canSnowHere(new BlockPos(x, y, z))) {
		    			world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, x, y, z, 0, 0, 0);
		    		} else {
		    			ApiParticles.spawnParticle(data.setPos(x, y, z), world);
		    		}
	    		}
    			AxisAlignedBB rainedArea = boundingbox.expand(1, 0, 1);
    			rainedArea = new AxisAlignedBB(boundingbox.minX, rainedArea.minY - (posY - getBlockUnder(new BlockPos(posX, posY, posZ))), boundingbox.minZ, boundingbox.maxX, boundingbox.maxY, boundingbox.maxZ);
    			
    			List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, rainedArea);
    			if (players != null) {
    				for (EntityPlayer j : players) {
    					if (j == Minecraft.getMinecraft().player) {
    						world.playSound((int)j.posX, (int)j.posY, (int)j.posZ, SoundEvents.WEATHER_RAIN, SoundCategory.AMBIENT, 0.1F, 0.6F, false);
    					}
    				}
    			}
	    	}
    		
    		int x = (int)posX + rand.nextInt((int)width) - (int)(width/2);
		    int z = (int)posZ + rand.nextInt((int)width) - (int)(width/2);
	    	
	    	int y = getBlockUnder(new BlockPos(x, posY, z));
    		
	    	if (getIsThundering()) {
		    	if (rand.nextInt(3000) == 0) Thunder(y);
		    	if (rand.nextInt(200) == 0) setIsThundering(false);
	    	}
	    	
	    	BlockPos pos = new BlockPos(x, y, z);
	    	IBlockState state = world.getBlockState(pos);
	    	
	    	if (state.getBlock() instanceof BlockFire) {
    			world.setBlockState(pos, Blocks.AIR.getDefaultState());
    		}
	    	
	    	if (rand.nextInt(20) == 0) {
	    		BlockPos below = pos.down();
	    		state = world.getBlockState(below);
		    	if (state.getBlock() != null) {
		    		if (world.canBlockFreezeWater(below)) {
		    			world.setBlockState(below, Blocks.ICE.getDefaultState());
		    		}
		    		
		    		if (world.canSnowAt(pos, false)) {
	    				world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState());
		    		}
		    		
		    		if (state.getBlock() instanceof BlockFarmland) {
		    			int moisture = state.getValue(BlockFarmland.MOISTURE);
		    			world.setBlockState(below, state.withProperty(BlockFarmland.MOISTURE, moisture + 1));
		    		} else if (state.getBlock() instanceof BlockCrops) {
		    			int age = state.getValue(BlockCrops.AGE);
		    			if (age < 7) {
		    				world.setBlockState(below, state.withProperty(BlockCrops.AGE, age + 1), 2);
		    			}
		    		}
		    		
		    		state.getBlock().fillWithRain(world, below);
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
    	
    	Entity riddenByEntity = getControllingPassenger();
    	
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
        
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, boundingbox);
        if (list != null) {
        	for (int i = 0; i < list.size(); ++i) {
        		if (list.get(i) instanceof EntityPlayer) {
        			if (((EntityPlayer)list.get(i)).posY > posY + 0.5) {
        				floatPlayer((EntityPlayer)list.get(i));
        			}
        		}
        	}
        }
        
		if (isBurning() && !dead) {
			for (int i = 0; i < 5; i++) {
				world.spawnParticle(EnumParticleTypes.CLOUD,
						posX + randomIn(rand, boundingbox.minX, boundingbox.maxX),
						posY + randomIn(rand, boundingbox.minY, boundingbox.maxY),
						posZ + randomIn(rand, boundingbox.minZ, boundingbox.maxZ), 0, 0.25, 0);
			}
		}
        
    	super.onUpdate();
    	
    	hurtTime = 0;
    }
    
    public double getMountedYOffset() {
        return height - 0.2; // getEntityBoundingBox().maxY - getEntityBoundingBox().minY - 0.25;
    }
    
    public void moveEntityWithHeading(float strafe, float up, float forward, float friction) {
    	if (!getStationary()) {
    		super.moveRelative(strafe, up, forward, friction);
    	}
    }
        
    public void onCollideWithPlayer(EntityPlayer player) {
    	if (player.posY > posY + 1) {
	    	if (floatPlayer(player)) {
	    		double difX = player.posX - player.lastTickPosX;
	    		double difZ = player.posZ - player.lastTickPosZ;
	    		double difY = player.posY - player.lastTickPosY;
	            player.distanceWalkedModified = (float)(player.distanceWalkedModified + MathHelper.sqrt(difX * difX + difZ * difZ) * 0.6);
	            player.distanceWalkedOnStepModified = (float)(player.distanceWalkedOnStepModified + MathHelper.sqrt(difX * difX + difY * difY + difZ * difZ) * 0.6);
	    		
	            PlayerExtension prop = PlayerExtension.get(player);
	    		if ((player.fallDistance > 1f) || player.distanceWalkedOnStepModified > prop.nextStepDistance) {
	    			prop.nextStepDistance = player.distanceWalkedOnStepModified + 2f;
	    			player.fallDistance = 0;
					SoundType soundtype = SoundType.CLOTH;
		            player.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
	    		}
	    	}
    	}
    	super.onCollideWithPlayer(player);
    }
    
    public AxisAlignedBB getCollisionBox(Entity entityIn) {
        return entityIn.getEntityBoundingBox();
    }
    
    protected void updateAITasks() {
    	if (!getStationary()) {
    		super.updateAITasks();
	        if (!isBeingRidden()) {
		        double diffY = altitude - posZ;
		        
		        if (rand.nextInt(700) == 0 || diffY*diffY < 3f) {
		            altitude += rand.nextInt(10) - 5;
		        }
        		
		        if (altitude > world.provider.getCloudHeight() - 8) {
		        	altitude = world.provider.getCloudHeight() - 18;
		        } else if (altitude < 70) {
		        	altitude = posY + 10;
		        }
		        
		        double var3 = altitude + 0.1D - posY;
		        motionX -= 0.001;
		        motionY += (Math.signum(var3) * 0.699999988079071D - motionY) * 0.10000000149011612D;
	        }
    	}
    }
    
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
    	if ((isBeingRidden() || isRidingOrBeingRiddenBy(player)) && hand == EnumHand.MAIN_HAND) {
        	if (PlayerSpeciesRegister.getPlayerSpecies(player).canInteractWithClouds()) {
        		ItemStack stack = player.getHeldItem(hand);
        		if (stack != null) {
        			if (!world.isRemote && stack.getItem() == Items.SPAWN_EGG && stack.getItemDamage() == Settings.getEntityId("cloud")) {
        				if (spawnCreature(player, stack)) return EnumActionResult.SUCCESS;
        			} else if (getStationary() && ItemBlock.class.isAssignableFrom(stack.getItem().getClass())) {
        				placeBlock(player, stack, hand);
        				return EnumActionResult.SUCCESS;
        			}
        		}
        		if (!world.isRemote && !getStationary()) {
        			player.startRiding(this);
        			if (isBeingRidden()) {
        				onMount(player);
        			} else {
        				onUnmount(player);
        			}
        			return EnumActionResult.SUCCESS;
        		}
        	}
        }
        return EnumActionResult.FAIL;
    }
    
    protected void onMount(EntityPlayer player) {
    	ridingHeight = height + player.height;
    	setSize(width, ridingHeight);
    }
    
    protected void onUnmount(EntityPlayer player) {
    	ridingHeight = 0;
    	getCloudSize();
    }
        
    public void handleStatusUpdate(byte type) {
    	if (type == 2) {
    		if (world.isRemote && !isBurning()) {
    			for (int i = 0; i < 50 * getCloudSize(); i++) {
    				ApiParticles.addBlockHitEffectsToEntity(this, Unicopia.UBlocks.cloud.getDefaultState());
    			}
    		}
    	}
    	super.handleStatusUpdate(type);
    }
    
    public boolean attackEntityFrom(DamageSource source, float amount) {
    	Entity attacker = source.getImmediateSource();
    	if (attacker != null) {
    		if (EntityPlayer.class.isAssignableFrom(attacker.getClass())) {
    			return onAttackByPlayer(source, amount, (EntityPlayer)attacker);
    		}
    		return false;
    	}
		return source == DamageSource.IN_WALL || super.attackEntityFrom(source, amount);
    }
    
    private boolean spawnCreature(EntityPlayer player, ItemStack stack) {
    	Entity entity = ItemMonsterPlacer.spawnCreature(world, ItemMonsterPlacer.getNamedIdFrom(stack), posX, posY + height, posZ);
        if (entity != null) {
            if (entity instanceof EntityLivingBase && stack.hasDisplayName()) {
                ((EntityLiving)entity).setCustomNameTag(stack.getDisplayName());
            }
            
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            return true;
        }
        return false;
    }
    
    private void placeBlock(EntityPlayer player, ItemStack stack, EnumHand hand) {
    	if (world.isRemote) {
	    	double distance = ClientSide.getReach();
			Vec3d pos = VecHelper.geteEyePosition(player, 1);
			Vec3d look = player.getLook(1);
	        Vec3d ray = pos.addVector(look.x * distance, look.y * distance, look.z * distance);
	        double size = getCollisionBorderSize() * 2;
			RayTraceResult trace = getEntityBoundingBox().expand(size, size, size).calculateIntercept(pos, ray);
			if (trace != null) {
				double x = trace.hitVec.x;
				double y = trace.hitVec.y;
				double z = trace.hitVec.z;
				
				double fX = MathHelper.floor(x);
				double fY = MathHelper.floor(y);
				double fZ = MathHelper.floor(z);
				
				int stackSize = stack.getCount();
				
				if (trace.sideHit == EnumFacing.UP) {
					fY--;
					y--;
				}
				if (trace.sideHit == EnumFacing.DOWN) {
					fY+=2;
					y+=2;
				}
				
				if (stack.onItemUse(player, world, new BlockPos(fX, fY, fZ), hand, trace.sideHit, (float)(x - fX), (float)(y - fY), (float)(z - fZ)) == EnumActionResult.PASS) {
					player.swingArm(hand);
				}
				
				if (player.capabilities.isCreativeMode) {
					stack.setCount(stackSize);
				}
			}
    	}
    }
    
    private boolean onAttackByPlayer(DamageSource source, float amount, EntityPlayer player) {
    	boolean canFly = PlayerSpeciesRegister.getPlayerSpecies(player).canInteractWithClouds();
    	boolean stat = getStationary();
    	if (stat || canFly) {
			ItemStack stack = player.getHeldItemMainhand();
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
    	if (s == DamageSource.GENERIC || (s.getTrueSource() != null && s.getTrueSource() instanceof EntityPlayer)) {
    		if (world.isRemote) {
    			if (!isBurning()) ApiParticles.addBlockDestroyEffectsToEntity(this, Unicopia.UBlocks.cloud.getDefaultState());
    			setDead();
        	}
    	}
    	super.onDeath(s);
    }
    
    protected void dropFewItems(boolean hitByPlayer, int looting) {
    	if (hitByPlayer) {
	    	Item item = getDropItem();
	    	int amount = 2 + world.rand.nextInt(3 + looting);
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
    	if (!isRidingOrBeingRiddenBy(player) && floatStrength > 0) {
    		double boundModifier = player.fallDistance > 80 ? 80 : MathHelper.floor(player.fallDistance * 10)/10;
    		player.onGround = true;
			player.motionY += (((floatStrength > 2 ? 1 : floatStrength/2) * 0.699999998079071D) - player.motionY + boundModifier*0.7) * 0.10000000149011612D;
			if (!getStationary() && player.motionY > 0.4 && world.rand.nextInt(900) == 0) {
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
		for (ItemStack stack : player.getArmorInventoryList()) {
			if (stack != null) {
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
				if (enchantments.containsKey(Enchantments.FEATHER_FALLING)) {
					return (Integer)enchantments.get(Enchantments.FEATHER_FALLING);
				}
			}
		}
		return 0;
	}
	
	private boolean canRainHere() {
		return world.getBiome(new BlockPos(posX, posY, posZ)).canRain();
	}
	
	private boolean canSnowHere(BlockPos pos) {
		return world.getBiome(pos).getFloatTemperature(pos) <= 0.15f;
	}
	
    public void Thunder() {
    	Thunder(getBlockUnder(new BlockPos(posX, posY, posZ)));
    }
    
    public void Thunder(int altitude) {
    	world.addWeatherEffect(new EntityLightningBolt(world, posX, altitude, posZ, false));
    }
    
    private int getBlockUnder(BlockPos pos) {
    	int y = world.getTopSolidOrLiquidBlock(pos).getY();
    	if (y >= posY) {
    		while (pos.getY() > 0 && world.getBlockState(pos).getBlock() == Blocks.AIR) {
    			pos = pos.down();
    		}
    		pos.up();
    	}
    	return y;
    }
    
    public int getRainTimer() {
    	return dataManager.get(RAINTIMER);
    }
    
    public int setRainTimer(int val) {
    	if (val < 0) val = 0;
    	dataManager.set(RAINTIMER, val);
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
    	return dataManager.get(THUNDERING);
    }
    
    public void setIsThundering(boolean val) {
    	dataManager.set(THUNDERING, val);
    }
        
    private int getCloudSizeInternal() {
    	return dataManager.get(SCALE);
    }
    
    public boolean getStationary() {
    	return dataManager.get(STATIONARY);
    }
    
    public void setStationary(boolean val) {
    	dataManager.set(STATIONARY, val);
    }
    
    public boolean getOpaque() {
    	return dataManager.get(OPAQUE);
    }
    
    public void setOpaque(boolean val) {
    	dataManager.set(OPAQUE, val);
    }
    
    public int getCloudSize() {
    	int size = getCloudSizeInternal();
    	updateSize(size);
    	return size;
    }
    
    private void updateSize(int scale) {
    	if (width != (baseWidth * scale) || (isBeingRidden() && height != (baseHeight * scale))) {
			setSize((float)(baseWidth * scale), (float)(baseHeight * scale));
			setPosition(posX, posY, posZ);
		}
    }
    
    public void setCloudSize(int val) {
    	if (val < 1) {
    		val = 1;
    	}
    	updateSize(val);
    	dataManager.set(SCALE, val);
    }
}
