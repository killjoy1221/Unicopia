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
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class SpellFire extends Spell implements IUseAction, IDispenceable {
	
	public static final StateMapList affected = new StateMapList();
	
	protected static final IStateMapping snowConversion = new IStateMapping() {
		public boolean canConvert(IBlockState state) {
			Block id = state.getBlock();
			return id == Blocks.snow_layer || id == Blocks.snow;
		}
		public IBlockState getConverted(IBlockState state) {
			return Blocks.air.getDefaultState();
		}
	};
	
	static {
		affected.add(Blocks.obsidian, Blocks.lava);
		affected.add(snowConversion);
		affected.add(new IStateMapping() {
			public boolean canConvert(IBlockState state) {
				return state.getBlock() instanceof BlockBush;
			}
			public IBlockState getConverted(IBlockState state) {
				return Blocks.air.getDefaultState();
			}
		});
		affected.add(Blocks.grass, Blocks.dirt);
		affected.add(Blocks.mossy_cobblestone, Blocks.cobblestone);
		affected.add(Blocks.cobblestone_wall, Blocks.cobblestone_wall).setDataRelated().setData(BlockWall.EnumType.MOSSY.getMetadata(), BlockWall.EnumType.NORMAL.getMetadata());
		affected.add(Blocks.stonebrick, Blocks.stonebrick).setDataRelated().setData(BlockStoneBrick.MOSSY_META, BlockStoneBrick.DEFAULT_META);
		affected.add(Blocks.monster_egg, Blocks.monster_egg).setDataRelated().setData(BlockSilverfish.EnumType.MOSSY_STONEBRICK.getMetadata(), BlockSilverfish.EnumType.STONEBRICK.getMetadata());
		affected.add(Blocks.dirt, Blocks.dirt).setDataRelated().setData(BlockDirt.DirtType.PODZOL.getMetadata(), 0);
	}
	
	public void renderAt(EntitySpell source, World w, double x, double y, double z, int level) {
		for (int i = 0; i < 2; ++i) {
            source.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x + Math.random() - 0.5, y + Math.random() - 0.5, z + Math.random() - 0.5, 0, 0, 0);
        }
	}
	
	public ActionResult onUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		boolean result = false;
		if (player == null || player.isSneaking()) {
			result = applyBlocks(player, world, pos);
		} else {
			Iterable<BlockPos> iter = BlockPos.getAllInBoxMutable(pos.add(-4, -4, -4), pos.add(4, 4, 4));
			
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
		Iterable<BlockPos> iter = BlockPos.getAllInBoxMutable(pos.add(-4, -4, -4), pos.add(4, 4, 4));
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
		
		if (id != Blocks.air) {
			if (id == Blocks.ice || id == Blocks.packed_ice) {
				world.setBlockState(pos, (world.provider.doesWaterVaporize() ? Blocks.air : Blocks.water).getDefaultState());
				playEffect(world, pos);
				return true;
			} else if (id == Blocks.netherrack) {
				if (world.getBlockState(pos.up()).getBlock().getMaterial() == Material.air) {
					if (world.rand.nextInt(300) == 0) {
						world.setBlockState(pos.up(), Blocks.fire.getDefaultState());
					}
					return true;
				}
			} else if (id == Blocks.redstone_wire) {
				int power = world.rand.nextInt(5) == 3 ? 15 : 3;
				sendPower(world, pos, power, 3, 0);
				return true;
			} else if (id == Blocks.sand && world.rand.nextInt(10) == 0) {
				if (isSurroundedBySand(world, pos)) {
					world.setBlockState(pos, Blocks.glass.getDefaultState());
					playEffect(world, pos);
					return true;
				}
			} else if (id instanceof BlockLeaves) {
				if (world.getBlockState(pos.up()).getBlock().getMaterial() == Material.air) {
					world.setBlockState(pos.up(), Blocks.fire.getDefaultState());
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
		Block id = w.getBlockState(pos).getBlock();
		if (i < max && id == Blocks.redstone_wire) {
			i++;
			w.setBlockState(pos, id.getStateFromMeta(power));
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
		world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
		
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
		return id == Blocks.sand || id == Blocks.glass;
	}
}
