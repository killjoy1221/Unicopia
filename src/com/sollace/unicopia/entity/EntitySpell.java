package com.sollace.unicopia.entity;

import com.sollace.unicopia.effect.SpellList;
import com.sollace.unicopia.item.ItemSpell;
import com.sollace.unicopia.server.PlayerSpeciesRegister;
import com.sollace.util.entity.ITameable;
import com.blazeloader.api.entity.IMousePickHandler;
import com.sollace.unicopia.Unicopia.UItems;
import com.sollace.unicopia.effect.IMagicEffect;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntitySpell extends EntityLiving implements IMagicals, IMousePickHandler, ITameable {
	
	private IMagicEffect effect;
	
	private EntityLivingBase owner = null;
	
	public float hoverStart;
	
	private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(EntitySpell.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> EFFECT_ID = EntityDataManager.createKey(EntitySpell.class, DataSerializers.VARINT);
	private static final DataParameter<String> OWNER = EntityDataManager.createKey(EntitySpell.class, DataSerializers.STRING);
	
	public EntitySpell(World w) {
		super(w);
		setSize(0.6f, 0.25f);
		hoverStart = (float)(Math.random() * Math.PI * 2.0D);
		setRenderDistanceWeight(getRenderDistanceWeight() + 1);
		preventEntitySpawning = false;
		enablePersistence();
	}
	
	public boolean isInRangeToRenderDist(double distance) {
		return super.isInRangeToRenderDist(distance);
    }
	
	public void setEffect(IMagicEffect effect) {
		this.effect = effect;
		dataManager.set(EFFECT_ID, SpellList.getId(effect));
	}
	
	public IMagicEffect getEffect() {
		if (effect == null) {
			effect = SpellList.forId(dataManager.get(EFFECT_ID));
		}
		return effect;
	}
	
	protected void entityInit() {
		super.entityInit();
		dataManager.register(LEVEL, 0);
		dataManager.register(EFFECT_ID, 0);
		dataManager.register(OWNER, "");
	}
	
	
	public ItemStack onPlayerMiddleClick(EntityPlayer player) {
		return new ItemStack(UItems.spell, 1, SpellList.getId(getEffect()));
	}
	
    protected boolean canTriggerWalking() {return false;}
    
    public boolean isPushedByWater() {return false;}
    
    public boolean canRenderOnFire() {return false;}
    
	public void setOwner(EntityLivingBase owner) {
		this.owner = owner;
		setOwner(owner.getName());
	}
	
	protected void setOwner(String ownerName) {
		if (ownerName != null && ownerName.length() != 0) {
			dataManager.set(OWNER, ownerName);
		}
	}
	
	protected String getOwnerName() {
		String ownerName = dataManager.get(OWNER);
		if (ownerName == null || ownerName.length() == 0) {
			if (owner instanceof EntityPlayer) {
				return owner.getName();
			}
			return "";
        }
        return ownerName;
	}
	
	public EntityLivingBase getOwner() {
        if (owner == null) {
        	String ownerName = dataManager.get(OWNER);
        	if (ownerName != null && ownerName.length() > 0) {
        		owner = world.getPlayerEntityByName(ownerName);
        	}
        }
        return owner;
    }
	
	public void displayTick() {
		if (getEffect() != null) {
			effect.renderAt(this, world, posX, posY, posZ, getLevel());
		}
	}
	
	public void onUpdate() {
		if (world.isRemote) {
			displayTick();
		}
		
		if (getEffect() == null) {
			setDead();
		} else {
			if (effect.getDead()) {
				setDead();
				onDeath();
			} else {
				effect.updateAt(this, world, posX, posY, posZ, getLevel());
			}
			
			if (effect.allowAI()) {
				super.onUpdate();
			}
		}
	}
	
	
	public void fall(float distance, float damageMultiplier) {
		
	}

    protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
    	//super.updateFallState(y, onGroundIn = this.onGround = true, blockIn, pos);
    	this.onGround = true;
    }
    
    public void moveEntityWithHeading(float strafe, float forward) {
        double var8;
        float var10;
        if (this.isServerWorld()) {
            float var5;
            
            if (this.isInWater() || this.isInLava()) {
            	float var3 = 0.91F;
            	float var4 = 0.16277136F / (var3 * var3 * var3);
            	
                var8 = this.posY;
                
                var5 = this.getAIMoveSpeed() * var4;

                this.travel(strafe, forward, var5);
                var3 = 0.91F;
                
                move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                this.motionX *= (double)var3;
                this.motionY *= 0.800000011920929D;
                this.motionZ *= (double)var3;
                this.motionY -= 0.02D;

                if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + var8, this.motionZ)) {
                    this.motionY = 0.30000001192092896D;
                }
            } else {
                float var3 = 0.91F;

                if (onGround) {
                    var3 = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
                }

                float var4 = 0.16277136F / (var3 * var3 * var3);

                if (onGround) {
                    var5 = this.getAIMoveSpeed() * var4;
                } else {
                    var5 = this.jumpMovementFactor;
                }
                
                travel(strafe, forward, var5);
                var3 = 0.91F;

                if (this.onGround) {
                    var3 = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
                }
                
                move(MoverType.SELF, motionX, motionY, motionZ);

                if (isCollidedHorizontally && isOnLadder()) motionY = 0.2;

                if (this.world.isRemote && (!this.world.isBlockLoaded(new BlockPos((int)posX, 0, (int)posZ)) || !world.getChunkFromBlockCoords(new BlockPos((int)this.posX, 0, (int)this.posZ)).isLoaded())) {
                    if (this.posY > 0.0D) {
                        this.motionY = -0.1D;
                    } else {
                        this.motionY = 0.0D;
                    }
                } else {
                    this.motionY -= 0.08D;
                }

                this.motionY *= (double)var3;//0.9800000190734863D;
                this.motionX *= (double)var3;
                this.motionZ *= (double)var3;
            }
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;
        var8 = this.posX - this.prevPosX;
        double var9 = this.posZ - this.prevPosZ;
        var10 = MathHelper.sqrt(var8 * var8 + var9 * var9) * 4.0F;

        if (var10 > 1.0F) var10 = 1.0F;

        this.limbSwingAmount += (var10 - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }
    
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
			int level = getLevel();
			entityDropItem(new ItemStack(UItems.spell, level + 1, SpellList.getId(effect)), 0);
		}
	}
	
	public void setDead() {
		if (getEffect() != null) {
			getEffect().setDead();
		}
		super.setDead();
	}
	
	public int getLevel() {
		return dataManager.get(LEVEL);
	}
	
	public void setLevel(int radius) {
		dataManager.set(LEVEL, radius);
	}
	
	public boolean tryLevelUp(ItemStack stack) {
		if (stack.getMetadata() > 0 && stack.getMetadata() == SpellList.getId(getEffect())) {
			increaseLevel();
			if (!world.isRemote) {
				if ((rand.nextFloat() * getLevel()) > 10 || overLevelCap()) {
					world.createExplosion(this, posX, posY, posZ, getLevel()/2, true);
					setDead();
					return false;
				}
            }
			playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 0.1f, 1);
			return true;
		}
		return false;
	}
	
	public boolean interactAt(EntityPlayer player, Vec3d p_174825_2_) {
		if (PlayerSpeciesRegister.getPlayerSpecies(player).canCast()) {
			ItemStack currentItem = player.getHeldItem(EnumHand.MAIN_HAND);
			if (currentItem != null && currentItem.getItem() instanceof ItemSpell) {
				tryLevelUp(currentItem);
				if (!player.capabilities.isCreativeMode) {
					currentItem.shrink(1);
					if (currentItem.isEmpty()) {
						player.renderBrokenItemStack(currentItem);
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public void increaseLevel() {
		setLevel(getLevel() + 1);
	}
	
	public boolean canLevelUp() {
		int max = effect.getMaxLevel();
		return max < 0 || getLevel() < max;
	}
	
	public boolean overLevelCap() {
		int max = effect.getMaxLevel();
		return max > 0 && getLevel() >= (max * 1.1);
	}
	
	public void decreaseLevel() {
		int level = getLevel() - 1;
		if (level < 0) level = 0;
		setLevel(level);
	}
	
	/**
	 * <b>NBTTagCompound getClientNBTData()</b>
	 * <br>
	 * Called by the EntityTracker to get any nbt this entity wants to be
	 * available on the client
	 */
	public NBTTagCompound getNBTTagCompound() {
		if (getEffect() != null) {
			NBTTagCompound tag = new NBTTagCompound();
			getEffect().writeToNBT(tag);
			return tag;
		}
		
		return null;
	}
	
	/**
	 * <b>void loadClientNBTData(NBTTagCompound tag)</b>
	 * <br>
	 * Called by the EntityTracker to load NBT data on the client side.
	 * Only used if getNBTTagCompound returns a non-null value
	 */
	public void func_174834_g(NBTTagCompound compound) {
		if (getEffect() != null) {
			getEffect().readFromNBT(compound);
		}
	} 
	
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		setOwner(compound.getString("ownerName"));
        setLevel(compound.getInteger("level"));
        
		if (compound.hasKey("effect")) {
			NBTTagCompound effectTag = compound.getCompoundTag("effect");
			setEffect(SpellList.forName(effectTag.getString("effect_id")));
			if (effect != null) {
				effect.readFromNBT(effectTag);
			}
		}
	}
	
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
        compound.setString("ownerName", getOwnerName());
        compound.setInteger("level", getLevel());
        if (effect != null) {
			NBTTagCompound effectTag = new NBTTagCompound();
			effectTag.setString("effect_id", SpellList.getName(effect));
			effect.writeToNBT(effectTag);
			compound.setTag("effect", effectTag);
        }
	}
	
	public ItemStack getHeldItem() {return null;}
	
	public ItemStack getEquipmentInSlot(int slotIn) {return null;}
	
	public ItemStack getCurrentArmor(int slotIn) {return null;}
	
	public void setCurrentItemOrArmor(int slotIn, ItemStack stack) { }
	
	public ItemStack[] getInventory() {return new ItemStack[0];}
	
	public boolean isSitting() {
		return false;
	}
}