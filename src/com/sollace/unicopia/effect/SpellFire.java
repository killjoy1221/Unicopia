package com.sollace.unicopia.effect;

import com.blazeloader.api.world.ApiWorld;
import com.sollace.unicopia.entity.EntitySpell;
import com.sollace.unicopia.entity.IMagicals;
import com.sollace.unicopia.server.PlayerSpeciesRegister;
import com.sollace.util.IStateMapping;
import com.sollace.util.MagicalDamageSource;
import com.sollace.util.StateMapList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class SpellFire extends Spell implements IUseAction, IDispenceable {
	
	public static final StateMapList affected = new StateMapList();
	
	protected static final IStateMapping snowConversion = new IStateMapping() {
		public boolean canConvert(IBlockState state) {
			Block id = state.getBlock();
			return id == Blocks.SNOW_LAYER || id == Blocks.SNOW;
		}
		public IBlockState getConverted(IBlockState state) {
			return Blocks.AIR.getDefaultState();
		}
	};
	
	static {
		affected.add(Blocks.OBSIDIAN, Blocks.LAVA);
		affected.add(snowConversion);
		affected.add(new IStateMapping() {
			public boolean canConvert(IBlockState state) {
				return state.getBlock() instanceof BlockBush;
			}
			public IBlockState getConverted(IBlockState state) {
				return Blocks.AIR.getDefaultState();
			}
		});
		affected.add(Blocks.GRASS, Blocks.DIRT);
		affected.add(Blocks.MOSSY_COBBLESTONE, Blocks.COBBLESTONE);
		affected.add(Blocks.COBBLESTONE_WALL, Blocks.COBBLESTONE_WALL).setDataRelated().setData(BlockWall.EnumType.MOSSY.getMetadata(), BlockWall.EnumType.NORMAL.getMetadata());
		affected.add(Blocks.STONEBRICK, Blocks.STONEBRICK).setDataRelated().setData(BlockStoneBrick.MOSSY_META, BlockStoneBrick.DEFAULT_META);
		affected.add(Blocks.MONSTER_EGG, Blocks.MONSTER_EGG).setDataRelated().setData(BlockSilverfish.EnumType.MOSSY_STONEBRICK.getMetadata(), BlockSilverfish.EnumType.STONEBRICK.getMetadata());
		affected.add(Blocks.DIRT, Blocks.DIRT).setDataRelated().setData(BlockDirt.DirtType.PODZOL.getMetadata(), 0);
	}
	
	public void renderAt(EntitySpell source, World w, double x, double y, double z, int level) {
		for (int i = 0; i < 2; ++i) {
            source.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, x + Math.random() - 0.5, y + Math.random() - 0.5, z + Math.random() - 0.5, 0, 0, 0);
        }
	}
	
	public ActionResult onUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		boolean result = false;
		if (player == null || player.isSneaking()) {
			result = applyBlocks(player, world, pos);
		} else {
			Iterable<MutableBlockPos> iter = BlockPos.getAllInBoxMutable(pos.add(-4, -4, -4), pos.add(4, 4, 4));
			
			for (BlockPos i : iter) {
				result |= applyBlocks(player, world, i);
			}
		}
		if (!result) result = applyEntities(player, world, pos);
		
		return result ? ActionResult.DEFAULT : ActionResult.NONE; 
	}
	
	public ActionResult onUse(ItemStack stack, EntityPlayer player, World world, Entity hitEntity) {
		return ((hitEntity != null) && (applyEntitySingle(player, world, hitEntity) == 1)) ? ActionResult.DEFAULT : ActionResult.NONE;
	}
	
	@Override
	public ActionResult onDispenced(BlockPos pos, EnumFacing facing, IBlockSource source) {
		pos = pos.add(facing.getFrontOffsetX() * 4, facing.getFrontOffsetY() * 4, facing.getFrontOffsetZ() * 4);
		Iterable<MutableBlockPos> iter = BlockPos.getAllInBoxMutable(pos.add(-4, -4, -4), pos.add(4, 4, 4));
		boolean result = false;
		for (BlockPos i : iter) {
			result |= applyBlocks(null, source.getWorld(), i);
		}
		if (!result) result = applyEntities(null, source.getWorld(), pos);
		return result ? ActionResult.NONE : ActionResult.DEFAULT;
	}
	
	protected boolean apply(EntityPlayer owner, World world, BlockPos pos) {
		return applyBlocks(owner, world, pos) || applyEntities(owner, world, pos);
	}
	
	protected boolean applyBlocks(EntityPlayer owner, World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block id = state.getBlock();
		
		if (id != Blocks.AIR) {
			if (id == Blocks.ICE || id == Blocks.PACKED_ICE) {
				world.setBlockState(pos, (world.provider.doesWaterVaporize() ? Blocks.AIR : Blocks.WATER).getDefaultState());
				playEffect(world, pos);
				return true;
			} else if (id == Blocks.NETHERRACK) {
				if (world.getBlockState(pos.up()).getMaterial() == Material.AIR) {
					if (world.rand.nextInt(300) == 0) {
						world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
					}
					return true;
				}
			} else if (id == Blocks.REDSTONE_WIRE) {
				int power = world.rand.nextInt(5) == 3 ? 15 : 3;
				sendPower(world, pos, power, 3, 0);
				return true;
			} else if (id == Blocks.SAND && world.rand.nextInt(10) == 0) {
				if (isSurroundedBySand(world, pos)) {
					world.setBlockState(pos, Blocks.GLASS.getDefaultState());
					playEffect(world, pos);
					return true;
				}
			} else if (id instanceof BlockLeaves) {
				if (world.getBlockState(pos.up()).getMaterial() == Material.AIR) {
					world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
					playEffect(world, pos);
					return true;
				}
			} else {
				state = affected.getConverted(state);
				if (state != null) {
					world.setBlockState(pos, state, 3);
					playEffect(world, pos);
					return true;
				}
			}
		}
	
		return false;
	}
	
	protected boolean applyEntities(EntityPlayer owner, World world, BlockPos pos) {
		int does = 0;
		for (Entity i : ApiWorld.getEntitiesNear(world, pos.getX(), pos.getY(), pos.getZ(), 3)) {
			does += applyEntitySingle(owner, world, i);
		}
		return does > 0;
	}
	
	
	protected int applyEntitySingle(Entity owner, World world, Entity e) {
		if ((!e.equals(owner) ||
				(owner instanceof EntityPlayer && !PlayerSpeciesRegister.getPlayerSpecies((EntityPlayer)owner).canCast())) && !(e instanceof EntityItem)
		&& !(e instanceof IMagicals)) {
			e.setFire(60);
			e.attackEntityFrom(getDamageCause(e, (EntityLivingBase)owner), 0.1f);
			playEffect(world, e.getPosition());
			return 1;
		}
		return 0;
	}
	
	protected DamageSource getDamageCause(Entity target, EntityLivingBase attacker) {
		return MagicalDamageSource.causeMobDamage("fire", attacker);
	}
	
	private void sendPower(World w, BlockPos pos, int power, int max, int i) {
		IBlockState state = w.getBlockState(pos);
		Block id = state.getBlock();
		if (i < max && id == Blocks.REDSTONE_WIRE) {
			i++;
			
			w.setBlockState(pos, state.withProperty(BlockRedstoneWire.POWER, power));
			
			sendPower(w, pos.up(), power, max, i);
			sendPower(w, pos.down(), power, max, i);
			sendPower(w, pos.north(), power, max, i);
			sendPower(w, pos.south(), power, max, i);
			sendPower(w, pos.east(), power, max, i);
			sendPower(w, pos.west(), power, max, i);
		}
	}
	
	protected void playEffect(World world, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.AMBIENT, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F, true);
		
		
        for (int i = 0; i < 8; ++i) {
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)x + Math.random(), (double)y + Math.random(), (double)z + Math.random(), 0.0D, 0.0D, 0.0D);
        }
	}
	
	public static boolean isSurroundedBySand(World w, BlockPos pos) {
		return isSand(w, pos.up()) && isSand(w, pos.down()) &&
				isSand(w, pos.north()) && isSand(w, pos.south()) &&
				isSand(w, pos.east()) && isSand(w, pos.west());
	}
	
	public static boolean isSand(World world, BlockPos pos) {
		Block id = world.getBlockState(pos).getBlock();
		return id == Blocks.SAND || id == Blocks.GLASS;
	}
}
