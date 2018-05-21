package com.sollace.unicopia.effect;

import java.util.List;

import com.blazeloader.api.world.ApiWorld;
import com.sollace.unicopia.Unicopia.Materials;
import com.sollace.util.IStateMapping;
import com.sollace.util.MagicalDamageSource;
import com.sollace.util.StateMapList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class SpellIce extends Spell implements IUseAction, IDispenceable {
	
	public static final StateMapList affected = new StateMapList();
	
	static {
		affected.add(new IStateMapping() {
			public boolean canConvert(IBlockState state) {
				Block id = state.getBlock();
				return id == Blocks.FLOWING_WATER || id == Blocks.WATER;
			}
			public IBlockState getConverted(IBlockState state) {
				return Blocks.ICE.getDefaultState();
			}
		});
		affected.add(new IStateMapping() {
			public boolean canConvert(IBlockState state) {
				Block id = state.getBlock();
				return id == Blocks.FLOWING_LAVA || id == Blocks.LAVA;
			}
			public IBlockState getConverted(IBlockState state) {
				return Blocks.OBSIDIAN.getDefaultState();
			}
		});
		affected.add(Blocks.FIRE, Blocks.AIR);
	}
	
	protected int rad = 3;
	
	@Override
	public ActionResult onDispenced(BlockPos pos, EnumFacing facing, IBlockSource source) {
		pos = pos.add(facing.getFrontOffsetX() * rad, facing.getFrontOffsetY() * rad, facing.getFrontOffsetZ() * rad);
		return applyBlocks(null, source.getWorld(), pos) ? ActionResult.NONE : ActionResult.DEFAULT;
	}

	@Override
	public ActionResult onUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player != null && player.isSneaking()) {
			applyBlockSingle(world, pos);
		} else {
			applyBlocks(player, world, pos);
		}
		return ActionResult.DEFAULT;
	}
	
	@Override
	public ActionResult onUse(ItemStack stack, EntityPlayer player, World world, Entity hitEntity) {
		if (hitEntity != null) {
			applyEntitySingle(player, hitEntity);
			return ActionResult.DEFAULT;
		}
		return ActionResult.NONE;
	}
	
	private boolean applyBlocks(EntityPlayer owner, World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block id = state.getBlock();
		if (id == Blocks.REDSTONE_WIRE) {
			world.setBlockState(pos, Blocks.REDSTONE_WIRE.getDefaultState(), 3);
		} else {
			int x, y, z;
			
			for (x = -rad; x < rad; x++) {
				for (y = -rad; y < rad; y++) {
					for (z = -rad; z < rad; z++) {
						applyBlockSingle(world, pos.add(x, y, z));
					}
				}
			}
			
			return applyEntities(owner, world, pos);
		}
		
		return false;
	}
	
	protected boolean applyEntities(EntityPlayer owner, World world, BlockPos pos) {
		List<Entity> entities = ApiWorld.getEntitiesOfTypeNear(world, null, pos.getX(), pos.getY(), pos.getZ(), 3);
		for (Entity i : entities) {
			applyEntitySingle(owner, i);
		}
		return entities.size() > 0;
	}
	
	protected void applyEntitySingle(EntityPlayer owner, Entity e) {
		if (e instanceof EntityTNTPrimed) {
			e.setDead();
			e.getEntityWorld().setBlockState(e.getPosition(), Blocks.TNT.getDefaultState());
		} else {
			if (e.isBurning()) {
				e.extinguish();
			} else {
				DamageSource d = MagicalDamageSource.causePlayerDamage("cold", owner);
				e.attackEntityFrom(d, 2);
			}
		}
	}
	
	private void applyBlockSingle(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block id = state.getBlock();
		
		IBlockState converted = affected.getConverted(state);
		if (converted != null) {
			world.setBlockState(pos, converted, 3);
		} else {
			if (id == Blocks.SNOW_LAYER) {
				state = state.cycleProperty(BlockSnow.LAYERS);
				if (state.getValue(BlockSnow.LAYERS) >= 7) {
					world.setBlockState(pos, Blocks.SNOW.getDefaultState(), 3);
				} else {
					world.setBlockState(pos, state, 3);
				}
			} else if (state.getMaterial() != Materials.cloud && state.isTopSolid() || (id == Blocks.SNOW) || (id instanceof BlockLeaves)) {
				incrementIce(world, pos.up());
			} else if (id == Blocks.ICE && world.rand.nextInt(10) == 0) {
				if (isSurroundedByIce(world, pos)) {
					world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState());
				}
			}
		}
		world.spawnParticle(EnumParticleTypes.WATER_SPLASH, pos.getX() + world.rand.nextFloat(), pos.getY() + 1, pos.getZ() + world.rand.nextFloat(), 0, 0, 0);
	}
	
	public static boolean isSurroundedByIce(World w, BlockPos pos) {
		return isIce(w, pos.up()) && isIce(w, pos.down()) &&
				isIce(w, pos.north()) && isIce(w, pos.south()) &&
				isIce(w, pos.east()) && isIce(w, pos.west());
	}
	
	public static boolean isIce(World world, BlockPos pos) {
		Block id = world.getBlockState(pos).getBlock();
		return id == Blocks.ICE || id == Blocks.PACKED_ICE;
	}
	
	private void incrementIce(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block id = state.getBlock();
		if (id == Blocks.AIR || (id instanceof BlockBush)) {
			world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState(), 3);
		}
	}
}
