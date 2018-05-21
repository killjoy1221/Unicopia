package com.sollace.unicopia.entity;

import com.blazeloader.api.entity.IMousePickHandler;
import com.blazeloader.api.gui.ApiGui;
import com.blazeloader.api.gui.IModInventory;
import com.sollace.unicopia.Unicopia.UItems;
import com.sollace.unicopia.container.ContainerBook;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import net.minecraft.block.SoundType;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class EntitySpellbook extends EntityLiving implements IMagicals, IMousePickHandler {

	public EntitySpellbook(World worldIn) {
		super(worldIn);
		setSize(0.6f, 0.6f);
	}
	
	private static final DataParameter<Boolean> OPENED = EntityDataManager.createKey(EntitySpellbook.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Byte> OPENED_USER = EntityDataManager.createKey(EntitySpellbook.class, DataSerializers.BYTE);
	
	protected void entityInit() {
		super.entityInit();
		dataManager.register(OPENED, true);
		dataManager.register(OPENED_USER, (byte)1);
	}
	
    protected boolean canTriggerWalking() {return false;}
    
    public boolean isPushedByWater() {return false;}
    
    public boolean canRenderOnFire() {return false;}
	
    public boolean getIsOpen() {
    	return dataManager.get(OPENED);
    }
    
    public Boolean getUserSetState() {
    	byte state = dataManager.get(OPENED_USER);
    	return state == 1 ? null : state == 2;
    }
    
    public void setIsOpen(boolean val) {
    	dataManager.set(OPENED, val);
    }
    
    public void setUserSetState(Boolean val) {
    	dataManager.set(OPENED_USER, val == null ? (byte)1 : val == true ? (byte)2 : (byte)0);
    }
    
    public void onUpdate() {
    	boolean open = getIsOpen();
		this.isJumping = open && isInWater();
    	super.onUpdate();
    	if (open && world.isRemote) {
	    	for (int offX = -2; offX <= 1; ++offX) {
	            for (int offZ = -2; offZ <= 1; ++offZ) {
	                if (offX > -1 && offX < 1 && offZ == -1) offZ = 1;
	                if (rand.nextInt(320) == 0) {
	                    for (int offY = 0; offY <= 1; ++offY) {
	                    	float vX = (float)offX/2 + rand.nextFloat();
	                    	float vY = (float)offY/2 - rand.nextFloat() + 0.5f;
	                    	float vZ = (float)offZ/2 + rand.nextFloat();
	                        world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, posX, posY, posZ, vX, vY, vZ, new int[0]);
	                    }
	                }
	            }
	        }
    	}
    	
    	if (world.rand.nextInt(30) == 0) {
	    	float celest = world.getCelestialAngle(1) * 4;
	    	boolean isDay = celest > 3 || celest < 1;
	    	Boolean userState = getUserSetState();
	    	boolean canToggle = (isDay != open) && (userState == null || userState == isDay);
	    	if (canToggle) {
	    		setUserSetState(null);
	    		setIsOpen(isDay);
	    	}
	    	if (userState != null && (isDay == open) && (userState == open)) {
	    		setUserSetState(null);
	    	}
    	}
    }
    
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (!world.isRemote) {
			setDead();
			SoundType sound = SoundType.WOOD;
			world.playSound(posX, posY, posZ, sound.getBreakSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch(), true);
			if (world.getGameRules().getBoolean("doTileDrops")) {
				entityDropItem(new ItemStack(UItems.spellBook, 1), 0);
			}
		}
		return false;
	}
	
	public boolean interactAt(EntityPlayer player, Vec3d p_174825_2_) {
		if (player.isSneaking()) {
			boolean open = !getIsOpen();
			setIsOpen(open);
			setUserSetState(open);
			return true;
		}
		if (PlayerSpeciesRegister.getPlayerSpecies(player).canCast()) {
			if (player instanceof EntityPlayerMP) {
				ApiGui.openContainer((EntityPlayerMP)player, new InterfaceBook());
			}
			return true;
		}
		return false;
	}
	
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		setIsOpen(compound.getBoolean("open"));
		if (compound.hasKey("force_open")) {
			setUserSetState(compound.getBoolean("force_open"));
		} else {
			setUserSetState(null);
		}
	}
	
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean("open", getIsOpen());
		Boolean state = getUserSetState();
		if (state != null) {
			compound.setBoolean("force_open", state);
		}
	}
	
	protected float func_110146_f(float direction, float velocity) {
        return computeRotationChange(direction, velocity);
    }
	
	protected float computeRotationChange(float direction, float velocity) {
		if (direction > 0) {
	        direction = MathHelper.wrapDegrees(direction) * 0.3F;
	        
	        float nextRotation = MathHelper.wrapDegrees(rotationYaw - direction);
	        
	        boolean angleClip = nextRotation < -90.0F || nextRotation >= 90.0F;
	        
	        if (nextRotation > 75) nextRotation = 75;
	        if (nextRotation > -75) nextRotation = -75;
	        
	        prevRotationYaw = rotationYaw;
	        rotationYaw = nextRotation;
	        
	        return angleClip ? -velocity : velocity;
		}
		return velocity;
	}
	
	public ItemStack getHeldItem() {return null;}
	
	public ItemStack getEquipmentInSlot(int slotIn) {return null;}
	
	public ItemStack getCurrentArmor(int slotIn) {return null;}
	
	public void setCurrentItemOrArmor(int slotIn, ItemStack stack) { }
	
	public ItemStack[] getInventory() {return new ItemStack[0];}
	
	public static class InterfaceBook implements IModInventory {
		
		public String getName() {return null;}
		
		public boolean hasCustomName() {return false;}
		
		public ITextComponent getDisplayName() {return null;}
		
		public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player) {
			return new ContainerBook(playerInventory, player.world, new BlockPos(player));
		}
		
		public String getGuiID() {return "unicopia:book";}
		
		public int getSizeInventory() {return 0;}
		
		public ItemStack getStackInSlot(int index) {return null;}
		
		public ItemStack decrStackSize(int index, int count) {return null;}
		
		public void setInventorySlotContents(int index, ItemStack stack) { }
		
		public int getInventoryStackLimit() {return 0;}
		
		public void markDirty() { }
		
		public boolean isUsableByPlayer(EntityPlayer player) {return false;}
		
		public void openInventory(EntityPlayer player) { }
		
		public void closeInventory(EntityPlayer player) { }
		
		public boolean isItemValidForSlot(int index, ItemStack stack) {return false;}
		
		public int getField(int id) {return 0;}
		
		public void setField(int id, int value) { }
		
		public int getFieldCount() {return 0;}
		
		public void clear() { }

		public boolean isEmpty() {return true;}

		public ItemStack removeStackFromSlot(int index) {return null;}
		
	}
	
	public ItemStack onPlayerMiddleClick(EntityPlayer player) {
		return new ItemStack(UItems.spellBook);
	}
}
