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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class SpellInferno extends SpellFire {
	
	public static final StateMapList affected = new StateMapList();
	
	static {
		affected.add(new IStateMapping() {
			public boolean canConvert(IBlockState state) {
				Block id = state.getBlock();
				return id == Blocks.GRASS || id == Blocks.DIRT || id == Blocks.STONE;
			}
			public IBlockState getConverted(IBlockState state) {
				return Blocks.NETHERRACK.getDefaultState();
			}
		});
		affected.add(Blocks.SAND, Blocks.SOUL_SAND);
		affected.add(new IStateMapping() {
			public boolean canConvert(IBlockState state) {
				return state.getMaterial() == Material.WATER;
			}
			public IBlockState getConverted(IBlockState state) {
				return Blocks.OBSIDIAN.getDefaultState();
			}
		});
		affected.add(Blocks.GRAVEL, Blocks.SOUL_SAND);
		affected.add(new IStateMapping() {
			public boolean canConvert(IBlockState state) {
				return state.getBlock() instanceof BlockBush;
			}
			public IBlockState getConverted(IBlockState state) {
				return Blocks.NETHER_WART.getDefaultState();
			}
		});
		affected.add(new IStateMapping() {
			public boolean canConvert(IBlockState state) {
				Block id = state.getBlock();
				return (id != Blocks.QUARTZ_ORE) && (id instanceof BlockOre);
			}
			public IBlockState getConverted(IBlockState state) {
				return Blocks.QUARTZ_ORE.getDefaultState();
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
		return updateAt(null, source.getEntityWorld(), source.posX, source.posY, source.posZ, 0);
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
