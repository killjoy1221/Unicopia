package com.minelittlepony.unicopia.entity;

import java.util.Map;

import com.minelittlepony.unicopia.Predicates;
import com.minelittlepony.unicopia.Race;
import com.minelittlepony.unicopia.forgebullshit.FUF;
import com.minelittlepony.unicopia.init.UBlocks;
import com.minelittlepony.unicopia.init.UItems;
import com.minelittlepony.unicopia.init.UParticles;
import com.minelittlepony.unicopia.particle.Particles;
import com.minelittlepony.unicopia.player.PlayerSpeciesList;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockFire;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityCloud extends EntityFlying implements IAnimals, IInAnimate {

    private static final DataParameter<Integer> RAINTIMER = EntityDataManager.createKey(EntityCloud.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> THUNDERING = EntityDataManager.createKey(EntityCloud.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> SCALE = EntityDataManager.createKey(EntityCloud.class, DataSerializers.VARINT);

    private static final DataParameter<Boolean> STATIONARY = EntityDataManager.createKey(EntityCloud.class, DataSerializers.BOOLEAN);

    protected double targetAltitude;

    protected int directionX;
    protected int directionZ;

    private final double baseWidth = 3f;
    private final double baseHeight = 0.8f;

    public EntityCloud(World world) {
        super(world);
        ignoreFrustumCheck = true;
        targetAltitude = getRandomFlyingHeight();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(RAINTIMER, 0);
        dataManager.register(THUNDERING, false);
        dataManager.register(STATIONARY, false);
        dataManager.register(SCALE, 1);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.BLOCK_CLOTH_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_CLOTH_STEP;
    }

    @Override
    protected Item getDropItem() {
        return UItems.cloud_matter;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }

    @Override
    public boolean canRenderOnFire() {
        return false;
    }

    /**
     * Gets the render brightness of this entity based on the surrounding world light values.
     */
    @Override
    public int getBrightnessForRender() {
        return 0xF000F0;
    }

    /**
     * Gets the brightness percentage for this entity based on the surrounding world light values.
     */
    @Override
    public float getBrightness() {
        return 1;
    }

    @Override
    protected boolean canDespawn() {
        return !hasCustomName() && !getStationary() && !getOpaque();
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 6;
    }

    @Override
    public boolean canInteract(Race race) {
        return race.canInteractWithClouds();
    }

    @Override
    public void onStruckByLightning(EntityLightningBolt lightningBolt) {

    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData pack) {
        if (world.rand.nextInt(20) == 0 && canRainHere()) {
            setRaining();
            if (world.rand.nextInt(20) == 0) {
                setIsThundering(true);
            }
        }

        setCloudSize(1 + rand.nextInt(4));

        return super.onInitialSpawn(difficulty, pack);
    }

    @Override
    protected void collideWithEntity(Entity other) {
        if (other instanceof EntityCloud || other instanceof EntityPlayer) {
            if (other.posY > posY) {
                return;
            }

            super.collideWithEntity(other);
        }
    }

    @Override
    public void applyEntityCollision(Entity other) {
        if (other instanceof EntityPlayer) {
            if (Predicates.INTERACT_WITH_CLOUDS.test((EntityPlayer)other)) {
                super.applyEntityCollision(other);
            }
        } else if (other instanceof EntityCloud) {
            super.applyEntityCollision(other);
        }
    }

    @Override
    public void onUpdate() {
        AxisAlignedBB boundingbox = getEntityBoundingBox();

        if (getIsRaining()) {
            if (world.isRemote) {
                for (int i = 0; i < 30 * getCloudSize(); i++) {
                    double x = MathHelper.nextDouble(rand, boundingbox.minX, boundingbox.maxX);
                    double y = getEntityBoundingBox().minY + height/2;
                    double z = MathHelper.nextDouble(rand, boundingbox.minZ, boundingbox.maxZ);

                    int particleId = canSnowHere(new BlockPos(x, y, z)) ? EnumParticleTypes.SNOW_SHOVEL.getParticleID() : UParticles.RAIN_DROPS;

                    Particles.instance().spawnParticle(particleId, false, x, y, z, 0, 0, 0);
                }

                AxisAlignedBB rainedArea = boundingbox
                        .expand(1, 0, 1)
                        .expand(0, -(posY - getGroundPosition(posX, posZ).getY()), 0);


                for (EntityPlayer j : world.getEntitiesWithinAABB(EntityPlayer.class, rainedArea)) {
                    if (!canSnowHere(j.getPosition())) {
                        j.world.playSound(j, j.getPosition(), SoundEvents.WEATHER_RAIN, SoundCategory.AMBIENT, 0.1F, 0.6F);
                    }
                }
            }

            BlockPos pos = getGroundPosition(
                posX + rand.nextFloat() * width,
                posZ + rand.nextFloat() * width
            );

            if (getIsThundering()) {
                if (rand.nextInt(3000) == 0) {
                    spawnThunderbolt(pos);
                }

                if (rand.nextInt(200) == 0) {
                    setIsThundering(false);
                }
            }

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

                        if (moisture < 7) {
                            world.setBlockState(below, state.withProperty(BlockFarmland.MOISTURE, moisture + 1));
                        }
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
                if (!getStationary()) {
                    pomf();

                    if (getCloudSize() > 1) {
                        setIsRaining(false);
                        setCloudSize(getCloudSize() - 1);
                    } else {
                        setDead();
                    }
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

        rotationPitch = 0;
        rotationYawHead = 0;
        rotationYaw = 0;

        for (Entity i : world.getEntitiesInAABBexcluding(this, boundingbox
                .grow(1 / (1 + getCloudSize())), Predicates.ENTITY_INTERACT_WITH_CLOUDS)) {
            if (i.posY > posY + 0.5) {
                applyGravityCompensation(i);
            }
        }

        if (isBurning() && !dead) {
            for (int i = 0; i < 5; i++) {
                world.spawnParticle(EnumParticleTypes.CLOUD,
                        MathHelper.nextDouble(rand, boundingbox.minX, boundingbox.maxX),
                        MathHelper.nextDouble(rand, boundingbox.minY, boundingbox.maxY),
                        MathHelper.nextDouble(rand, boundingbox.minZ, boundingbox.maxZ), 0, 0.25, 0);
            }
        }

        if (getStationary()) {
            motionX = 0;
            motionY = 0;
            motionZ = 0;
        }

        super.onUpdate();

        double motionFactor = (1 + getCloudSize() / 4);

        motionX /= motionFactor;
        motionZ /= motionFactor;

        hurtTime = 0;
    }

    @Override
    public double getMountedYOffset() {
        return getEntityBoundingBox().maxY - getEntityBoundingBox().minY - 0.25;
    }

    @Override
    public void moveRelative(float strafe, float up, float forward, float friction) {
        if (!getStationary()) {
            super.moveRelative(strafe, up, forward, friction);
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (player.posY >= posY) {
            if (applyGravityCompensation(player)) {
                double difX = player.posX - player.lastTickPosX;
                double difZ = player.posZ - player.lastTickPosZ;
                double difY = player.posY - player.lastTickPosY;

                player.distanceWalkedModified = (float)(player.distanceWalkedModified + MathHelper.sqrt(difX * difX + difZ * difZ) * 0.6);
                player.distanceWalkedOnStepModified = (float)(player.distanceWalkedOnStepModified + MathHelper.sqrt(difX * difX + difY * difY + difZ * difZ) * 0.6);

                if (PlayerSpeciesList.instance().getPlayer(player).stepOnCloud()) {
                    SoundType soundtype = SoundType.CLOTH;
                    player.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
                }
            }
        }

        super.onCollideWithPlayer(player);
    }

    @Override
    protected void updateAITasks() {
        if (!getStationary()) {
            if (!isBeingRidden()) {
                double distance = targetAltitude - posY;

                if (targetAltitude < posY && !world.isAirBlock(getPosition())) {
                    distance = 0;
                }

                if (Math.abs(distance) < 1 && rand.nextInt(7000) == 0) {
                    targetAltitude = getRandomFlyingHeight();
                    distance = targetAltitude - posY;
                }

                if (Math.abs(distance) < 1) {
                    distance = 0;
                }

                motionX -= 0.002;
                motionY += (Math.signum(distance) * 0.699999988079071D - motionY) * 0.10000000149011612D;
            }
        }
    }

    protected float getRandomFlyingHeight() {
        float a = getMaximumFlyingHeight();
        float b = getMinimumFlyingHeight();

        float min = Math.min(a, b);
        float max = Math.max(a, b);

        return min + world.rand.nextFloat() * (max - min);
    }

    protected float getMinimumFlyingHeight() {
        float ground = world.provider.getAverageGroundLevel();
        float cloud = world.getWorldType().getCloudHeight();

        float min = Math.min(ground, cloud);
        float max = Math.max(ground, cloud);

        return min + (max - min)/2;
    }

    protected float getMaximumFlyingHeight() {
        return world.provider.getActualHeight() - 5;
    }

    @Override
    public void handleStatusUpdate(byte type) {
        if (type == 2) {
            if (!isBurning()) {
                for (int i = 0; i < 50 * getCloudSize(); i++) {
                    Particles.instance().getEntityEmitter().emitDiggingParticles(this, UBlocks.cloud_block.getDefaultState());
                }
            }
        }
        super.handleStatusUpdate(type);
    }

    public void handlePegasusInteration(int interationType) {
        if (!world.isRemote) {
            switch (interationType) {
                case 1:
                    setIsRaining(!getIsRaining());
                    break;
                case 2:
                    spawnThunderbolt();
                    break;
            }
        }

        pomf();
    }

    public void pomf() {
        for (int i = 0; i < 50 * getCloudSize(); i++) {
            Particles.instance().getEntityEmitter().emitDiggingParticles(this, UBlocks.cloud_block.getDefaultState());
        }

        playHurtSound(DamageSource.GENERIC);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        Entity attacker = source.getImmediateSource();

        if (attacker instanceof EntityPlayer) {
            return onAttackByPlayer(source, amount, (EntityPlayer)attacker);
        }

        return source == DamageSource.IN_WALL || super.attackEntityFrom(source, amount);
    }

    private boolean onAttackByPlayer(DamageSource source, float amount, EntityPlayer player) {

        ItemStack stack = player.getHeldItemMainhand();

        boolean canFly = EnchantmentHelper.getEnchantments(stack).containsKey(Enchantments.FEATHER_FALLING)
                || Predicates.INTERACT_WITH_CLOUDS.test(player);
        boolean stat = getStationary();

        if (stat || canFly) {
            if (!isBurning()) {
                for (int i = 0; i < 50 * getCloudSize(); i++) {
                    Particles.instance().getEntityEmitter().emitDiggingParticles(this, UBlocks.cloud_block.getDefaultState());
                }
            }

            if (stack != null && stack.getItem() instanceof ItemSword) {
                return super.attackEntityFrom(source, amount);
            } else if (stack != null && stack.getItem() instanceof ItemSpade) {
                return super.attackEntityFrom(source, amount * 1.5f);
            } else if (canFly) {
                if (player.posY < posY || !world.isAirBlock(getPosition())) {
                    targetAltitude = posY + 5;
                } else if (player.posY > posY) {
                    targetAltitude = posY - 5;
                }
            }
        }
        return false;
    }

    @Override
    public void onDeath(DamageSource s) {
        if (s == DamageSource.GENERIC || (s.getTrueSource() != null && s.getTrueSource() instanceof EntityPlayer)) {
            setDead();
        }

        super.onDeath(s);
        clearItemFloatingState();
    }

    @Override
    public void setDead() {
        super.setDead();
        clearItemFloatingState();
    }

    @FUF(reason = "There is no TickEvent.EntityTickEvent. Waiting on mixins...")
    protected void clearItemFloatingState() {
        AxisAlignedBB bounds = getEntityBoundingBox().grow(1 / (1 + getCloudSize())).grow(5);

        for (Entity i : world.getEntitiesInAABBexcluding(this, bounds, this::entityIsFloatingItem)) {
            i.setNoGravity(false);
        }
    }

    private boolean entityIsFloatingItem(Entity e) {
        return e instanceof EntityItem
                && Predicates.ITEM_INTERACT_WITH_CLOUDS.test((EntityItem)e);
    }

    @Override
    protected void dropFewItems(boolean hitByPlayer, int looting) {
        if (hitByPlayer) {
            Item item = getDropItem();
            int amount = 13 + world.rand.nextInt(3);

            dropItem(item, amount * (1 + looting));

            if (world.rand.nextBoolean()) {
                dropItem(UItems.dew_drop, 3 + looting);
            }
        }
    }

    @Override
    public EntityItem entityDropItem(ItemStack stack, float offsetY) {
        EntityItem item = super.entityDropItem(stack, offsetY);

        PlayerSpeciesList.instance().getEntity(item).setPlayerSpecies(Race.PEGASUS);
        item.setNoGravity(true);
        item.motionY = 0;

        return item;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);

        setRainTimer(tag.getInteger("RainTimer"));
        setIsThundering(tag.getBoolean("IsThundering"));
        setCloudSize(tag.getByte("CloudSize"));
        setStationary(tag.getBoolean("IsStationary"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);

        tag.setInteger("RainTimer", getRainTimer());
        tag.setBoolean("IsThundering", getIsThundering());
        tag.setByte("CloudSize", (byte)getCloudSize());
        tag.setBoolean("IsStationary", getStationary());
    }

    protected boolean applyGravityCompensation(Entity entity) {
        int floatStrength = getFloatStrength(entity);

        if (!isRidingOrBeingRiddenBy(entity) && floatStrength > 0) {

            double boundModifier = entity.fallDistance > 80 ? 80 : MathHelper.floor(entity.fallDistance * 10) / 10;

            entity.onGround = true;
            entity.motionY += (((floatStrength > 2 ? 1 : floatStrength/2) * 0.699999998079071D) - entity.motionY + boundModifier * 0.7) * 0.10000000149011612D;
            if (!getStationary()) {
                entity.motionX += ((motionX - entity.motionX) / getCloudSize()) - 0.002F;
            }

            if (!getStationary() && entity.motionY > 0.4 && world.rand.nextInt(900) == 0) {
                spawnThunderbolt(getPosition());
            }

            // @FUF(reason = "There is no TickEvents.EntityTickEvent. Waiting on mixins...")
            if (getStationary() && entity instanceof EntityItem) {
                entity.motionX /= 8;
                entity.motionZ /= 8;
                entity.motionY /= 16;
                entity.setNoGravity(true);
            }

            return true;
        }

        return false;
    }

    @Override
    public void move(MoverType type, double x, double y, double z) {
        setEntityBoundingBox(getEntityBoundingBox().offset(x, y, z));
        resetPositionToBB();
    }

    public int getFloatStrength(Entity entity) {
        if (Predicates.ENTITY_INTERACT_WITH_CLOUDS.test(entity)) {
            return 3;
        }

        if (entity instanceof EntityPlayer) {
            return getFeatherEnchantStrength((EntityPlayer)entity);
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
        return world.getBiome(pos).getTemperature(pos) <= 0.15f;
    }

    public void spawnThunderbolt() {
        spawnThunderbolt(getGroundPosition(posX, posZ));
    }

    public void spawnThunderbolt(BlockPos pos) {
        world.addWeatherEffect(new EntityLightningBolt(world, pos.getX(), pos.getY(), pos.getZ(), false));
    }

    private BlockPos getGroundPosition(double x, double z) {
        BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, posY, z));

        if (pos.getY() >= posY) {
            while (world.isValid(pos)) {
                pos = pos.down();
                if (world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.UP)) {
                    return pos.up();
                }
            }

        }
        return pos;
    }

    public int getRainTimer() {
        return dataManager.get(RAINTIMER);
    }

    public int setRainTimer(int val) {
        val = Math.max(0, val);
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

    public boolean getStationary() {
        return dataManager.get(STATIONARY);
    }

    public void setStationary(boolean val) {
        dataManager.set(STATIONARY, val);
    }

    public boolean getOpaque() {
        return false;
    }

    public int getCloudSize() {
        int size = dataManager.get(SCALE);
        updateSize(size);
        return size;
    }

    private void updateSize(int scale) {
        setSize((float)baseWidth * scale, (float)baseHeight * scale);
    }

    @Override
    protected void setSize(float width, float height) {
        if (width != this.width || height != this.height) {
            super.setSize(width, height);
            setPosition(posX, posY, posZ);
        }
    }

    public void setCloudSize(int val) {
        val = Math.max(1, val);
        updateSize(val);
        dataManager.set(SCALE, val);
    }
}
