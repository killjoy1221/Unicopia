package com.sollace.unicopia.item;

import java.util.Iterator;

import com.sollace.unicopia.Settings;
import com.sollace.unicopia.Unicopia;
import com.sollace.unicopia.Unicopia.UItems;
import com.sollace.unicopia.effect.ActionResult;
import com.sollace.unicopia.effect.IDispenceable;
import com.sollace.unicopia.effect.IMagicEffect;
import com.sollace.unicopia.effect.IUseAction;
import com.sollace.unicopia.effect.SpellList;
import com.sollace.unicopia.entity.EntitySpell;
import com.sollace.unicopia.server.PlayerSpeciesRegister;
import com.sollace.util.VecHelper;

import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemSpell extends Item {
	private static final IBehaviorDispenseItem dispenserBehavior = new BehaviorDefaultDispenseItem() {
		protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			if (hasDispenceBehaviour(stack)) {
				EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
				BlockPos pos = source.getBlockPos().offset(facing);
				IMagicEffect effect = SpellList.forId(stack.getMetadata());
				ActionResult dispenceResult = ((IDispenceable)effect).onDispenced(pos, facing, source);
				if (dispenceResult == ActionResult.DEFAULT) {
					return super.dispenseStack(source, stack);
				}
				if (dispenceResult == ActionResult.PLACE) {
					castContainedSpell(source.getWorld(), pos.getX(), pos.getY(), pos.getZ(), stack, effect);
				}
				stack.shrink(1);
				return stack;
			}
			return super.dispenseStack(source, stack);
		}
	};
	
	public ItemSpell(String name) {
		super();
		setHasSubtypes(true);
		setMaxDamage(0);
		setUnlocalizedName(name);
        maxStackSize = 16;
        setCreativeTab(CreativeTabs.BREWING);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, dispenserBehavior);
	}
	
	//TODO: Where'd this go?
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		if (renderPass == 1 && (!Unicopia.isClient() || Settings.getSpecies().canCast())) {
			return SpellList.getGemColour(stack.getMetadata());
		}
		return 0;//super.getColorFromItemStack(stack, renderPass);
	}
	
	public boolean hasEffect(ItemStack stack) {
		return (!Unicopia.isClient() || Settings.getSpecies().canCast()) && stack.getMetadata() > 0;
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (hasEnchantment(stack) && PlayerSpeciesRegister.getPlayerSpecies(player).canCast()) {
    		IMagicEffect effect = SpellList.forId(stack.getMetadata());
    		ActionResult result = ActionResult.PLACE;
    		if (effect instanceof IUseAction) {
    			result = ((IUseAction)effect).onUse(stack, player, world, pos, side, hitX, hitY, hitZ);
    		}
    		if (!world.isRemote) {
	    		pos = pos.offset(side);
	    		if (result == ActionResult.PLACE) {
	    			castContainedSpell(world, pos.getX(), pos.getY(), pos.getZ(), stack, effect).setOwner(player);
	    		}
    		}
    		if (result != ActionResult.NONE) {
    			if (!player.capabilities.isCreativeMode) stack.shrink(1);
            	return true;
    		}
		}
    	return false;
    }
	
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		RayTraceResult mop = VecHelper.getObjectMouseOver(player, 5, 0);
		if (mop != null && mop.typeOfHit == RayTraceResult.Type.ENTITY) {
			IMagicEffect effect = SpellList.forId(stack.getMetadata());
			ActionResult result = ActionResult.NONE;
			if (effect instanceof IUseAction) {
				result = ((IUseAction)effect).onUse(stack, player, world, mop.entityHit);
			}
	        if (result == ActionResult.DEFAULT) {
	        	if (!player.capabilities.isCreativeMode) stack.shrink(1);
	        	player.swingArm(hand);
	        }
		}
        return stack;
    }
	
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		super.getSubItems(tab, subItems);
		Iterator<Integer> subTypes = SpellList.getIDIterator();
		while (subTypes.hasNext()) {
			subItems.add(new ItemStack(UItems.spell, 1, subTypes.next()));
		}
	}
	
	public String getUnlocalizedName(ItemStack stack) {
		String result = super.getUnlocalizedName(stack);
		int meta = stack.getMetadata();
		if (meta > 0) result += "." + SpellList.getName(meta);
		return result;
	}
	
	protected static EntitySpell castContainedSpell(World world, int x, int y, int z, ItemStack stack, IMagicEffect effect) {
		EntitySpell spell = new EntitySpell(world);
        spell.setEffect(effect);
		spell.setLocationAndAngles(x + 0.5, y + 0.5, z + 0.5, 0, 0);
    	world.spawnEntity(spell);
    	return spell;
	}
	
	public static boolean hasEnchantment(ItemStack stack) {
		if (stack.getItemDamage() > 0) {
			return SpellList.isEffect(stack.getMetadata());
		}
		return false;
	}
	
	public static boolean hasDispenceBehaviour(ItemStack stack) {
		return hasEnchantment(stack) && SpellList.hasDispenceBehaviour(stack.getMetadata());
	}
}
