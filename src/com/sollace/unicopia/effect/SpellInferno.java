package com.sollace.unicopia.effect;

import com.blazeloader.util.shape.IShape;
import com.blazeloader.util.shape.Sphere;
import com.sollace.unicopia.entity.EntitySpell;
import com.sollace.util.IStateMapping;
import com.sollace.util.MagicalDamageSource;
import com.sollace.util.StateMapList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockOre;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class SpellInferno extends SpellFire {
	
	public static final StateMapList affected = new StateMapList();
	
	static {
		affected.add(new IStateMapping() {
			public boolean canConvert(IBlockState state) {
				Block id = state.getBlock();
				return id == Blocks.grass || id == Blocks.dirt || id == Blocks.stone;
			}
			public IBlockState getConverted(IBlockState state) {
				return Blocks.netherrack.getDefaultState();
			}
		});
		affected.add(Blocks.sand, Blocks.soul_sand);
		affected.add(new IStateMapping() {
			public boolean canConvert(IBlockState state) {
				return state.getBlock().getMaterial() == Material.water;
			}
			public IBlockState getConverted(IBlockState state) {
				return Blocks.obsidian.getDefaultState();
			}
		});
		affected.add(Blocks.gravel, Blocks.soul_sand);
		affected.add(new IStateMapping() {
			public boolean canConvert(IBlockState state) {
				return state.getBlock() instanceof BlockBush;
			}
			public IBlockState getConverted(IBlockState state) {
				return Blocks.nether_wart.getDefaultState();
			}
		});
		affected.add(new IStateMapping() {
			public boolean canConvert(IBlockState state) {
				Block id = state.getBlock();
				return (id != Blocks.quartz_ore) && (id instanceof BlockOre);
			}
			public IBlockState getConverted(IBlockState state) {
				return Blocks.quartz_ore.getDefaultState();
			}
		});
		affected.add(snowConversion);
	}
	
	public int getMaxLevel() {
		return 10;
	}
	
	public ActionResult onUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		return ActionResult.PLACE;
	}
	
	public ActionResult onUse(ItemStack stack, EntityPlayer player, World world, Entity hitEntity) {
		return ActionResult.NONE;
	}
	
	public boolean update(Entity source) {
		return updateAt(null, source.worldObj, source.posX, source.posY, source.posZ, 0);
	}
	
	public boolean updateAt(EntitySpell source, World w, double x, double y, double z, int level) {
		if (!w.isRemote) {
			int radius = 4 + (level * 4);
			IShape shape = new Sphere(false, radius);
			
			for (int i = 0; i < radius; i++) {
				BlockPos pos = new BlockPos(shape.computePoint(w.rand).addVector(x, y, z));
				IBlockState state = w.getBlockState(pos);
				state = affected.getConverted(state);
				if (state != null) {
					w.setBlockState(pos, state, 3);
				}
			}
			
			shape = new Sphere(false, radius-1);
			for (int i = 0; i < radius * 2; i++) {
				if (w.rand.nextInt(12) == 0) {
					apply((EntityPlayer)source.getOwner(), w, new BlockPos(shape.computePoint(w.rand).addVector(x, y, z)));
				}
			}
		}
		return false;
	}
	
	protected DamageSource getDamageCause(Entity target, EntityLivingBase attacker) {
		if (attacker != null && attacker.getUniqueID().equals(target.getUniqueID())) {
			return MagicalDamageSource.causeMobDamage("fire.own", null);
		}
		return MagicalDamageSource.causeMobDamage("fire", attacker);
	}
}
