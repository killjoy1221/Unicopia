package com.sollace.unicopia.entity;

import com.sollace.unicopia.effect.SpellList;
import com.sollace.unicopia.item.ItemSpell;
import com.sollace.unicopia.server.PlayerSpeciesRegister;
import com.blazeloader.api.entity.IMousePickHandler;
import com.sollace.unicopia.Unicopia.UItems;
import com.sollace.unicopia.effect.IMagicEffect;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntitySpell extends EntityLivingBase implements IMagicals, IMousePickHandler {
	
	private IMagicEffect effect;
	
	private EntityLivingBase owner = null;
	
	public float hoverStart;
	
	public EntitySpell(World w) {
		super(w);
		setSize(0.25F, 0.25F);
		hoverStart = (float)(Math.random() * Math.PI * 2.0D);
		renderDistanceWeight += 1;
		preventEntitySpawning = false;
	}
	
	public boolean isInRangeToRenderDist(double distance) {
		return super.isInRangeToRenderDist(distance);
    }
	
	public void setEffect(IMagicEffect effect) {
		this.effect = effect;
		dataWatcher.updateObject(11, SpellList.getId(effect));
	}
	
	public IMagicEffect getEffect() {
		if (effect == null) {
			effect = SpellList.forId(dataWatcher.getWatchableObjectInt(11));
		}
		return effect;
	}
	
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(10, 0);
		dataWatcher.addObject(11, 0);
		dataWatcher.addObject(12, "");
	}
	
	
	public ItemStack onPlayerMiddleClick(EntityPlayer player) {
		return new ItemStack(UItems.spell, 1, SpellList.getId(getEffect()));
	}
	
    protected boolean canTriggerWalking() {return false;}
    
    public boolean isPushedByWater() {return false;}
    
    public boolean canRenderOnFire() {return false;}
    
    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return super.isInvisibleToPlayer(player);
    }
    
	public void setOwner(EntityLivingBase owner) {
		this.owner = owner;
		setOwner(owner.getCommandSenderName());
	}
	
	protected void setOwner(String ownerName) {
		if (ownerName != null && ownerName.length() != 0) {
			dataWatcher.updateObject(12, ownerName);
		}
	}
	
	protected String getOwnerName() {
		String ownerName = dataWatcher.getWatchableObjectString(12);
		if (ownerName == null || ownerName.length() == 0) {
			if (owner instanceof EntityPlayer) {
				return owner.getCommandSenderName();
			}
			return "";
        }
        return ownerName;
	}
	
	public EntityLivingBase getOwner() {
        if (owner == null) {
        	String ownerName = dataWatcher.getWatchableObjectString(12);
        	if (ownerName != null && ownerName.length() > 0) {
        		owner = worldObj.getPlayerEntityByName(ownerName);
        	}
        }
        return owner;
    }
	
	public void displayTick() {
		if (getEffect() != null) {
			effect.renderAt(this, worldObj, posX, posY, posZ, getLevel());
		}
	}
	
	public void onUpdate() {
		if (worldObj.isRemote) {
			displayTick();
		}
		
		if (getEffect() == null) {
			setDead();
		} else {
			if (effect.getDead()) {
				setDead();
				onDeath();
			} else {
				effect.updateAt(this, worldObj, posX, posY, posZ, getLevel());
			}
		}
	}
	
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (!worldObj.isRemote) {
			setDead();
			onDeath();
		}
		return false;
	}
	
	protected void onDeath() {
		SoundType sound = Block.soundTypeStone;
		worldObj.playSoundEffect(posX, posY, posZ, sound.getBreakSound(), sound.getVolume(), sound.getFrequency());
		if (worldObj.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
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
		return dataWatcher.getWatchableObjectInt(10);
	}
	
	public void setLevel(int radius) {
		dataWatcher.updateObject(10, radius);
	}
	
	public boolean tryLevelUp(ItemStack stack) {
		if (stack.getMetadata() > 0 && stack.getMetadata() == SpellList.getId(getEffect())) {
			increaseLevel();
			if (!worldObj.isRemote) {
				if ((rand.nextFloat() * getLevel()) > 10 || overLevelCap()) {
					worldObj.createExplosion(this, posX, posY, posZ, getLevel()/2, true);
					setDead();
					return false;
				}
            }
			playSound("mob.zombie.remedy", 0.1f, 1);
			return true;
		}
		return false;
	}
	
	public boolean interactAt(EntityPlayer player, Vec3 p_174825_2_) {
		if (PlayerSpeciesRegister.getPlayerSpecies(player).canCast()) {
			ItemStack currentItem = player.getCurrentEquippedItem();
			if (currentItem != null && currentItem.getItem() instanceof ItemSpell) {
				tryLevelUp(currentItem);
				if (!player.capabilities.isCreativeMode) {
					currentItem.stackSize--;
					if (currentItem.stackSize < 1) {
						player.destroyCurrentEquippedItem();
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
}