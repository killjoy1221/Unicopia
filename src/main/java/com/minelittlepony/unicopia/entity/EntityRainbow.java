package com.minelittlepony.unicopia.entity;

import com.minelittlepony.unicopia.Race;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;

public class EntityRainbow extends EntityWeatherEffect implements IInAnimate {

    public static final SpawnListEntry SPAWN_ENTRY = new SpawnListEntry(EntityRainbow.Spawner.class, 1, 1, 1);

    private int ticksAlive;

    private double radius;

    public static final int RAINBOW_MAX_SIZE = 180;
    public static final int RAINBOW_MIN_SIZE = 50;

    public static final AxisAlignedBB SPAWN_COLLISSION_RADIUS = new AxisAlignedBB(
            -RAINBOW_MAX_SIZE, -RAINBOW_MAX_SIZE, -RAINBOW_MAX_SIZE,
             RAINBOW_MAX_SIZE,  RAINBOW_MAX_SIZE,  RAINBOW_MAX_SIZE
     ).grow(RAINBOW_MAX_SIZE);

    public EntityRainbow(World world) {
        this(world, 0, 0, 0);
    }

    public EntityRainbow(World world, double x, double y, double z) {
        super(world);

        float yaw = (int)MathHelper.nextDouble((world == null ? rand : world.rand), 0, 360);

        setLocationAndAngles(x, y, z, yaw, 0);

        radius = MathHelper.nextDouble(world == null ? rand : world.rand, RAINBOW_MIN_SIZE, RAINBOW_MAX_SIZE);
        ticksAlive = 10000;

        ignoreFrustumCheck = true;

        width = (float)radius;
        height = width;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    public boolean canInteract(Race race) {
        return false;
    }

    @Override
    public void setPosition(double x, double y, double z) {
        posX = x;
        posY = y;
        posZ = z;

        setEntityBoundingBox(new AxisAlignedBB(
                x - width, y - radius/2, z,
                x + width, y + radius/2, z
        ));
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.WEATHER;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (ticksAlive-- <= 0) {
            setDead();
        }

        if (!isDead) {
            AxisAlignedBB bounds = SPAWN_COLLISSION_RADIUS.offset(getPosition());

            world.getEntitiesWithinAABB(EntityRainbow.class, bounds).forEach(this::attackCompetitor);
            world.getEntitiesWithinAABB(EntityRainbow.Spawner.class, bounds).forEach(this::attackCompetitor);
        }
    }

    private void attackCompetitor(Entity other) {
        if (other != this) {
            other.setDead();
        }
    }

    @Override
    protected void entityInit() {
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }

    public static class Spawner extends EntityLiving {

        public Spawner(World worldIn) {
            super(worldIn);
            this.setInvisible(true);
        }

        @Override
        public boolean getCanSpawnHere() {
            AxisAlignedBB bounds = SPAWN_COLLISSION_RADIUS.offset(getPosition());

            return super.getCanSpawnHere()
                    && world.getEntitiesWithinAABB(EntityRainbow.class, bounds).isEmpty()
                    && world.getEntitiesWithinAABB(EntityRainbow.Spawner.class, bounds).isEmpty();
        }

        @Override
        public int getMaxSpawnedInChunk() {
            return 1;
        }

        @Override
        public void onUpdate() {
            super.onUpdate();
            if (!this.dead) {
                setDead();
                trySpawnRainbow();
            }
        }

        public void trySpawnRainbow() {
            EntityRainbow rainbow = new EntityRainbow(world);
            rainbow.setPosition(posX, posY, posZ);
            world.spawnEntity(rainbow);
        }
    }
}
