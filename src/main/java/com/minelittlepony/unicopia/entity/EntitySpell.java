package com.minelittlepony.unicopia.entity;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.minelittlepony.unicopia.Predicates;
import com.minelittlepony.unicopia.Race;
import com.minelittlepony.unicopia.init.UItems;
import com.minelittlepony.unicopia.item.ICastable;
import com.minelittlepony.unicopia.network.EffectSync;
import com.minelittlepony.unicopia.spell.ICaster;
import com.minelittlepony.unicopia.spell.IMagicEffect;
import com.minelittlepony.unicopia.spell.SpellAffinity;
import com.minelittlepony.unicopia.spell.SpellRegistry;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntitySpell extends EntityCreature implements IMagicals, ICaster<EntityLivingBase>, IInAnimate {

    private EntityLivingBase owner = null;

    public float hoverStart;

    private static final DataParameter<Integer> LEVEL = EntityDataManager
            .createKey(EntitySpell.class, DataSerializers.VARINT);

    private static final DataParameter<String> OWNER = EntityDataManager
            .createKey(EntitySpell.class, DataSerializers.STRING);

    private static final DataParameter<NBTTagCompound> EFFECT = EntityDataManager
            .createKey(EntitySpell.class, DataSerializers.COMPOUND_TAG);

    private static final DataParameter<Integer> AFFINITY = EntityDataManager
            .createKey(EntitySpell.class, DataSerializers.VARINT);

    private final EffectSync<EntityLivingBase> effectDelegate = new EffectSync<>(this, EFFECT);

    public EntitySpell(World w) {
        super(w);
        setSize(0.6f, 0.25f);
        hoverStart = (float)(Math.random() * Math.PI * 2.0D);
        setRenderDistanceWeight(getRenderDistanceWeight() + 1);
        preventEntitySpawning = false;
        enablePersistence();
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        if (getCurrentLevel() > 0) {
            distance /= getCurrentLevel();
        }
        return super.isInRangeToRenderDist(distance);
    }

    @Override
    public SpellAffinity getAffinity() {
        return SpellAffinity.values()[dataManager.get(AFFINITY)];
    }

    public void setAffinity(SpellAffinity affinity) {
        dataManager.set(AFFINITY, affinity.ordinal());
    }

    @Override
    public void setEffect(@Nullable IMagicEffect effect) {
        effectDelegate.set(effect);

        if (effect != null) {
            effect.onPlaced(this);
        }
    }

    @Override
    public boolean canInteract(Race race) {
        return race.canCast();
    }

    @Nullable
    @Override
    public <T extends IMagicEffect> T getEffect(@Nullable Class<T> type, boolean update) {
        return effectDelegate.get(type, update);
    }

    @Override
    public boolean hasEffect() {
        return effectDelegate.has();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(LEVEL, 0);
        dataManager.register(EFFECT, new NBTTagCompound());
        dataManager.register(OWNER, "");
        dataManager.register(AFFINITY, SpellAffinity.NEUTRAL.ordinal());
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return SpellRegistry.instance().enchantStack(new ItemStack(getItem()), getEffect().getName());
    }

    protected Item getItem() {
        return getAffinity() == SpellAffinity.BAD ? UItems.corrupted_gem : UItems.gem;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    @Override
    public boolean canRenderOnFire() {
        return false;
    }

    @Override
    public void setOwner(EntityLivingBase owner) {
        this.owner = owner;
        setOwner(owner.getName());
    }

    protected void setOwner(String ownerName) {
        if (!StringUtils.isEmpty(ownerName)) {
            dataManager.set(OWNER, ownerName);
        }
    }

    protected String getOwnerName() {
        String ownerName = dataManager.get(OWNER);

        if (!StringUtils.isEmpty(ownerName)) {
            if (owner instanceof EntityPlayer) {
                return owner.getName();
            }

            return "";
        }

        return ownerName;
    }

    @Override
    public EntityLivingBase getOwner() {
        if (owner == null) {
            String ownerName = dataManager.get(OWNER);
            if (ownerName != null && ownerName.length() > 0) {
                owner = world.getPlayerEntityByName(ownerName);
            }
        }

        return owner;
    }

    protected void displayTick() {
        if (hasEffect()) {
            getEffect().render(this);
        }
    }

    @Override
    public void onUpdate() {
        if (world.isRemote) {
            displayTick();
        }

        if (!hasEffect()) {
            setDead();
        } else {
            if (getEffect().getDead()) {
                setDead();
                onDeath();
            } else {
                getEffect().update(this);
            }

            if (getEffect().allowAI()) {
                height = 1.5F;
                super.onUpdate();
            }
        }

        if (overLevelCap()) {
            if (world.rand.nextInt(10) == 0) {
                spawnExplosionParticle();
            }

            if (!world.isRemote && hasEffect()) {
                float exhaustionChance = getEffect().getExhaustion(this);

                if (exhaustionChance == 0 || world.rand.nextInt((int)(exhaustionChance / 500)) == 0) {
                    addLevels(-1);
                } else if (world.rand.nextInt((int)(exhaustionChance * 500)) == 0) {
                    setEffect(null);
                } else if (world.rand.nextInt((int)(exhaustionChance * 3500)) == 0) {
                    world.createExplosion(this, posX, posY, posZ, getCurrentLevel()/2, true);
                    setDead();
                }
            }
        }

        if (getCurrentLevel() < 0) {
            setDead();
        }
    }

    @Override
    public void fall(float distance, float damageMultiplier) {

    }

    public boolean overLevelCap() {
        return getCurrentLevel() > getMaxLevel();
    }

    @Override
    protected void updateFallState(double y, boolean onGround, IBlockState state, BlockPos pos) {
        this.onGround = true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!world.isRemote) {
            setDead();
            onDeath();
        }
        return false;
    }

    protected void onDeath() {
        SoundType sound = SoundType.STONE;

        world.playSound(posX, posY, posZ, sound.getBreakSound(), SoundCategory.NEUTRAL, sound.getVolume(), sound.getPitch(), true);

        if (world.getGameRules().getBoolean("doTileDrops")) {
            int level = getCurrentLevel();

            ItemStack stack = new ItemStack(getItem(), level + 1);
            if (hasEffect()) {
                SpellRegistry.instance().enchantStack(stack, getEffect().getName());
            }

            entityDropItem(stack, 0);
        }
    }

    @Override
    public void setDead() {
        if (hasEffect()) {
            getEffect().setDead();
        }
        super.setDead();
    }

    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
        if (Predicates.MAGI.test(player)) {
            ItemStack currentItem = player.getHeldItem(EnumHand.MAIN_HAND);

            if (currentItem != null
                    && currentItem.getItem() instanceof ICastable
                    && ((ICastable)currentItem.getItem()).canFeed(this, currentItem)
                    && tryLevelUp(currentItem)) {

                if (!player.capabilities.isCreativeMode) {
                    currentItem.shrink(1);

                    if (currentItem.isEmpty()) {
                        player.renderBrokenItemStack(currentItem);
                    }
                }

                return EnumActionResult.SUCCESS;
            }
        }

        return EnumActionResult.FAIL;
    }

    public boolean tryLevelUp(ItemStack stack) {
        if (hasEffect() && SpellRegistry.stackHasEnchantment(stack)) {
            if (!getEffect().getName().contentEquals(SpellRegistry.getKeyFromStack(stack))) {
                return false;
            }

            addLevels(1);

            playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 0.1f, 1);

            return true;
        }

        return false;
    }

    @Override
    public int getMaxLevel() {
        return hasEffect() ? getEffect().getMaxLevelCutOff(this) : 0;
    }

    @Override
    public int getCurrentLevel() {
        return dataManager.get(LEVEL);
    }

    @Override
    public void setCurrentLevel(int level) {
        dataManager.set(LEVEL, Math.max(level, 0));
    }

    @Override
    public Entity getEntity() {
        return this;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("affinity")) {
            setAffinity(SpellAffinity.of(compound.getString("affinity")));
        }

        setOwner(compound.getString("ownerName"));
        setCurrentLevel(compound.getInteger("level"));

        if (compound.hasKey("effect")) {
            setEffect(SpellRegistry.instance().createEffectFromNBT(compound.getCompoundTag("effect")));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        compound.setString("affinity", getAffinity().name());
        compound.setString("ownerName", getOwnerName());
        compound.setInteger("level", getCurrentLevel());

        if (hasEffect()) {
            compound.setTag("effect", SpellRegistry.instance().serializeEffectToNBT(getEffect()));
        }
    }
}