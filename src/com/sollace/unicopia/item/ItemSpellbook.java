package com.sollace.unicopia.item;

import com.sollace.unicopia.entity.EntitySpellbook;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemSpellbook extends ItemBook {
	private static final IBehaviorDispenseItem dispenserBehavior = new BehaviorDefaultDispenseItem() {
		protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
			BlockPos pos = source.getBlockPos().offset(facing);
			int yaw = 0;
			
			
			//0deg == SOUTH
			//90deg == WEST
			//180deg == NORTH
			//270deg == EAST
			
			/*switch (facing) {
			case NORTH: yaw -= 90; break;
			case SOUTH: yaw += 90; break;
			case EAST: yaw += 180; break;
			default:
			}*/
			
			yaw = facing.getOpposite().getHorizontalIndex() * 90;
			placeBook(source.getWorld(), pos.getX(), pos.getY(), pos.getZ(), yaw);
			stack.shrink(1);
			return stack;
		}
	};
	
	public ItemSpellbook(String name) {
		super();
		setMaxDamage(0);
		setUnlocalizedName(name);
        maxStackSize = 1;
        setCreativeTab(CreativeTabs.BREWING);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, dispenserBehavior);
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
    	if (!world.isRemote) {
    		if (PlayerSpeciesRegister.getPlayerSpecies(player).canCast()) {
	    		pos = pos.offset(side);
	    		
                double diffX = player.posX - (pos.getX() + 0.5);
                double diffZ = player.posZ - (pos.getZ() + 0.5);
                float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX) + Math.PI);
	    		
	            placeBook(world, pos.getX(), pos.getY(), pos.getZ(), yaw);
	            if (!player.capabilities.isCreativeMode) stack.shrink(1);
	            return true;
    		}
    	}
    	return false;
    }
	
	private static void placeBook(World world, int x, int y, int z, float yaw) {
		EntitySpellbook book = new EntitySpellbook(world);
		book.setPositionAndRotation(x + 0.5, y, z + 0.5, yaw, 0);
		book.renderYawOffset = 0;
		book.prevRotationYaw = yaw;
		world.spawnEntity(book);
	}
}















