package com.sollace.unicopia.power;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.blazeloader.api.particles.ApiParticles;
import com.blazeloader.api.particles.ParticleData;
import com.blazeloader.api.world.AuxilaryEffects;
import com.blazeloader.util.shape.IShape;
import com.blazeloader.util.shape.Sphere;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Race;
import com.sollace.unicopia.Settings;
import com.sollace.unicopia.Unicopia.UItems;
import com.sollace.unicopia.server.PlayerSpeciesRegister;
import com.sollace.util.MagicalDamageSource;
import com.sollace.util.VecHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class PowerStomp extends Power<PowerStomp.Data> {

	public PowerStomp(String name, int key) {
		super(name, key);
	}
	
	public int getWarmupTime(PlayerExtension player) {
		return 0;
	}
	
	public int getCooldownTime(PlayerExtension player) {
		return 500;
	}
	
	public boolean canActivate(World w, EntityPlayer player) {
		return player.capabilities.isCreativeMode || player.getFoodStats().getFoodLevel() > 15;
	}
	
	public boolean canUse(Race playerSpecies) {
		return playerSpecies.canUseEarth();
	}
	
	public Data tryActivate(EntityPlayer player, World w) {
		RayTraceResult mop = VecHelper.getObjectMouseOver(player, 2, 1);
		if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos pos = mop.getBlockPos();
			IBlockState state = w.getBlockState(pos);
			if (state.getBlock() instanceof BlockLog) {
				pos = getBaseOfTree(w, state, pos);
				if (measureTree(w, state, pos) > 0) {
					return new Data(pos.getX(), pos.getY(), pos.getZ(), 1);
				}
			}
		}
		
		if (!player.onGround && !player.capabilities.isFlying) {
			player.addVelocity(0, -6, 0);
			return new Data(0, 0, 0, 0);
		}
		return null;
	}
	
	private void removeTree(World w, BlockPos pos) {
		IBlockState log = w.getBlockState(pos);
		int size = measureTree(w, log, pos);
		if (size > 0) {
			pos = ascendTrunk(new ArrayList<BlockPos>(), w, pos, log, 0);
			removeTreePart(w, log, pos, 0);
		}
	}
	
	private BlockPos ascendTrunk(List<BlockPos> done, World w, BlockPos pos, IBlockState log, int level) {
		if (level < 3 && !done.contains(pos)) {
			done.add(pos);
			BlockPos result = ascendTree(w, log, pos, true);
			if (variantAndBlockEquals(w.getBlockState(pos.east()), log)) result = ascendTrunk(done, w, pos.east(), log, level + 1);
			if (variantAndBlockEquals(w.getBlockState(pos.west()), log)) result = ascendTrunk(done, w, pos.west(), log, level + 1);
			if (variantAndBlockEquals(w.getBlockState(pos.north()), log)) result = ascendTrunk(done, w, pos.north(), log, level + 1);
			if (variantAndBlockEquals(w.getBlockState(pos.south()), log)) result = ascendTrunk(done, w, pos.south(), log, level + 1);
			return result;
		}
		return pos;
	}
	
	private void removeTreePart(World w, IBlockState log, BlockPos pos, int level) {
		if (level < 10 && isWoodOrLeaf(w, log, pos)) {
			if (level < 5) {
				w.destroyBlock(pos, true);
			} else {
				IBlockState state = w.getBlockState(pos);
				state.getBlock().dropBlockAsItem(w, pos, state, 0);
				w.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
			}
			removeTreePart(w, log, pos.up(), level + 1);
			removeTreePart(w, log, pos.north(), level + 1);
			removeTreePart(w, log, pos.south(), level + 1);
			removeTreePart(w, log, pos.east(), level + 1);
			removeTreePart(w, log, pos.west(), level + 1);
		}
	}
	
	private BlockPos ascendTree(World w, IBlockState log, BlockPos pos, boolean remove) {
		int breaks = 0;
		IBlockState state;
		while (variantAndBlockEquals(w.getBlockState(pos.up()), log)) {
			if (isLeaves(w.getBlockState(pos.north()), log)) break;
			if (isLeaves(w.getBlockState(pos.south()), log)) break;
			if (isLeaves(w.getBlockState(pos.east()), log)) break;
			if (isLeaves(w.getBlockState(pos.west()), log)) break;
			if (remove) {
				if (breaks < 10) {
					w.destroyBlock(pos, true);
				} else {
					state = w.getBlockState(pos);
					state.getBlock().dropBlockAsItem(w, pos, state, 0);
					w.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				}
				breaks++;
			}
			pos = pos.up();
		}
		return pos;
	}
		
	private void dropApples(World w, BlockPos pos) {
		IBlockState log = w.getBlockState(pos);
		int size = measureTree(w, log, pos);
		if (size > 0) {
			dropApplesPart(new ArrayList<BlockPos>(), w, log, pos, 0);
		}
	}
	
	
	private void dropApplesPart(List<BlockPos> done, World w, IBlockState log, BlockPos pos, int level) {
		if (!done.contains(pos)) {
			done.add(pos);
			pos = ascendTree(w, log, pos, false);
			if (level < 10 && isWoodOrLeaf(w, log, pos)) {
				IBlockState state = w.getBlockState(pos);
				if (state.getBlock() instanceof BlockLeaves && w.getBlockState(pos.down()).getMaterial() == Material.AIR) {
					w.playEvent(2001, pos, Block.getStateId(state));
					EntityItem item = new EntityItem(w);
					item.setPosition(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5);
					item.setItem(new ItemStack(Items.APPLE, 1, getAppleMeta(w, log)));
					w.spawnEntity(item);
				}
				dropApplesPart(done, w, log, pos.up(), level + 1);
				dropApplesPart(done, w, log, pos.north(), level + 1);
				dropApplesPart(done, w, log, pos.south(), level + 1);
				dropApplesPart(done, w, log, pos.east(), level + 1);
				dropApplesPart(done, w, log, pos.west(), level + 1);
			}
		}
	}
	
	private int getAppleMeta(World w, IBlockState log) {
		if (Settings.getLegacyMode()) {
			return 0;
		}
		return UItems.apple.getRandomAppleMetadata(w.rand, getVariant(log));
	}
	
	private int measureTree(World w, IBlockState log, BlockPos pos) {
		List<BlockPos> logs = new ArrayList<BlockPos>();
		List<BlockPos> leaves = new ArrayList<BlockPos>();
		countParts(logs, leaves, w, log, pos);
		return logs.size() <= (leaves.size() / 2) ? logs.size() + leaves.size() : 0;
	}
	
	private BlockPos getBaseOfTree(World w, IBlockState log, BlockPos pos) {
		return getBaseOfTreePart(new ArrayList<BlockPos>(), w, log, pos);
	}
	
	private BlockPos getBaseOfTreePart(List<BlockPos> done, World w, IBlockState log, BlockPos pos) {
		if (done.contains(pos) || !variantAndBlockEquals(w.getBlockState(pos), log)) return null;
		done.add(pos);
		while (variantAndBlockEquals(w.getBlockState(pos.down()), log)) {
			pos = pos.down();
			done.add(pos);
		}
		BlockPos adjacent = getBaseOfTreePart(done, w, log, pos.north());
		if (adjacent != null && adjacent.getY() < pos.getY()) pos = adjacent;
		adjacent = getBaseOfTreePart(done, w, log, pos.south());
		if (adjacent != null && adjacent.getY() < pos.getY()) pos = adjacent;
		adjacent = getBaseOfTreePart(done, w, log, pos.east());
		if (adjacent != null && adjacent.getY() < pos.getY()) pos = adjacent;
		adjacent = getBaseOfTreePart(done, w, log, pos.west());
		if (adjacent != null && adjacent.getY() < pos.getY()) pos = adjacent;
		
		if (!done.contains(pos)) done.add(pos);
		return pos;
	}
	
	private boolean isWoodOrLeaf(World w, IBlockState log, BlockPos pos) {
		IBlockState state = w.getBlockState(pos);
		return variantAndBlockEquals(state, log) || (isLeaves(state, log) && ((Boolean)state.getValue(BlockLeaves.DECAYABLE)).booleanValue());
	}
	
	private void countParts(List<BlockPos> logs, List<BlockPos> leaves, World w, IBlockState log, BlockPos pos) {
		if (!logs.contains(pos) && !leaves.contains(pos)) {
			IBlockState state = w.getBlockState(pos);
			boolean yay = false;
			if (state.getBlock() instanceof BlockLeaves && ((Boolean)state.getValue(BlockLeaves.DECAYABLE)).booleanValue() && variantEquals(state, log)) {
				leaves.add(pos);
				yay = true;
			} else if (variantAndBlockEquals(state, log)) {
				logs.add(pos);
				yay = true;
			}
			if (yay) {
				countParts(logs, leaves, w, log, pos.up());
				countParts(logs, leaves, w, log, pos.north());
				countParts(logs, leaves, w, log, pos.south());
				countParts(logs, leaves, w, log, pos.east());
				countParts(logs, leaves, w, log, pos.west());
			}
		}
	}
	
	private boolean isLeaves(IBlockState state, IBlockState log) {
		return state.getBlock() instanceof BlockLeaves && variantEquals(state, log);
	}
	
	private boolean variantAndBlockEquals(IBlockState one, IBlockState two) { 
		return (one.getBlock() == two.getBlock()) && variantEquals(one, two);
	}
	
	private boolean variantEquals(IBlockState one, IBlockState two) { 
		return getVariant(one) == getVariant(two);
	}
	
	/*public static IBlockState withFacing(IBlockState state, EnumFacing facing) {
		Entry<IProperty, Object> property = getFacingProperty(state);
		return property == null ? state : state.withProperty(property.getKey(), facing);
	}
	
	public static Entry<IProperty, Object> getFacingProperty(IBlockState state) {
		Entry<IProperty, Object> result = null;
		for (Entry<IProperty, Object> i : (ImmutableSet<Entry<IProperty, Object>>)state.getProperties().entrySet()) {
			if (i.getKey() instanceof PropertyDirection) {
				result = i;
				if (i.getKey().getName().contentEquals("facing")) {
					return result;
				}
			}
		}
		return result;
	}*/
	
	private Object getVariant(IBlockState state) {
		for (Entry<IProperty<?>, ?> i : state.getProperties().entrySet()) {
			if (i.getKey().getName().contentEquals("variant")) {
				return i.getValue();
			}
		}
		return null;
	}
	
	public Data fromBytes(ByteBuf buf) {
		Data result = new Data();
		result.fromBytes(buf);
		return result;
	}
	
	public void apply(EntityPlayer player, Data data) {
		double rad = 4;
		if (data.hitType == 0) {
			player.addVelocity(0, -6, 0);
			BlockPos pos = player.getPosition();
			AxisAlignedBB box = new AxisAlignedBB(player.posX - rad, player.posY - rad, player.posZ - rad, player.posX + rad, player.posY + rad, player.posZ + rad);
			List<Entity> entities = player.world.getEntitiesWithinAABBExcludingEntity(player, box);
			for (Entity i : entities) {
				double dist = Math.sqrt(i.getDistanceSq(pos));
				if (dist <= rad + 3) {
					i.addVelocity(i.posX - player.posX, i.posY - player.posY, i.posZ - player.posZ);
					DamageSource damage = MagicalDamageSource.causePlayerDamage("smash", player);
					float amount = 4 / (float)dist;
					if (i instanceof EntityPlayer) {
						Race race = PlayerSpeciesRegister.getPlayerSpecies((EntityPlayer)i);
						if (race.canUseEarth()) amount /= 3;
						if (race.canFly()) amount *= 4;
					}
					i.attackEntityFrom(damage, amount);
				}
			}
			Iterable<BlockPos> area = BlockPos.getAllInBox(pos.add(-rad, -rad, -rad), pos.add(rad, rad, rad));
			for (BlockPos i : area) {
				if (i.distanceSqToCenter(player.posX, player.posY, player.posZ) <= rad*rad) {
					spawnEffect(player.world, i);
				}
			}
			for (int i = 1; i < 202; i+= 2) {
				spawnParticleRing(player, i);
			}
			TakeFromPlayer(player, 4);
		} else if (data.hitType == 1) {
			if (player.world.rand.nextInt(30) == 0) {
				removeTree(player.world, new BlockPos(data.x, data.y, data.z));
			} else {
				dropApples(player.world, new BlockPos(data.x, data.y, data.z));
			}
			TakeFromPlayer(player, 1);
		}
	}
	
	private void spawnEffect(World w, BlockPos pos) {
		IBlockState state = w.getBlockState(pos);
		if (state.getBlock() != Blocks.AIR) {
			if (w.getBlockState(pos.up()).getBlock() == Blocks.AIR) {
				w.playEvent(AuxilaryEffects.BLOCK_BREAK.getId(), pos, Block.getStateId(state));
			}
		}
	}
	
	public void preApply(EntityPlayer player) {
		player.spawnRunningParticles();
	}
	
	public void postApply(EntityPlayer player) {
		PlayerExtension prop = PlayerExtension.get(player);
		int timeDiff = getCooldownTime(prop) - prop.getCooldownRemaining();
		if (player.world.getWorldTime() % 1 == 0 || timeDiff == 0) {
			spawnParticleRing2(player, timeDiff);
		}
	}
	
	private void spawnParticleRing(EntityPlayer player, int timeDiff) {
		int animationTicks = (int)(timeDiff / 10);
		if (animationTicks < 6) {
			IShape shape = new Sphere(true, animationTicks, 1, 0, 1);
			
			double y = 0.5 + (Math.sin(animationTicks) * 1.5);
			
			ParticleData data = ParticleData.get(EnumParticleTypes.BLOCK_CRACK, false, Block.getStateId(Blocks.DIRT.getDefaultState()));
			
			data.setVel(0, y * 5, 0);
			
			ApiParticles.spawnParticleShape(data, player.world, player.posX, player.posY + y, player.posZ, shape, 1);
		}
	}
	
	private void spawnParticleRing2(EntityPlayer player, int timeDiff) {
		int animationTicks = (int)(timeDiff / 10);
		if (animationTicks < 6) {
			IShape shape = new Sphere(true, animationTicks, 1, 0, 1);
			
			double y = 0.5 + (Math.sin(animationTicks) * 1.5);
			
			ParticleData data = ParticleData.get(EnumParticleTypes.BLOCK_CRACK, false, Block.getStateId(Blocks.DIRT.getDefaultState()));
			
			ApiParticles.spawnParticleShape(data, player.world, player.posX, player.posY + y, player.posZ, shape, 1);
		}
	}
	
	protected static class Data extends Power.LocationData {
		
		int hitType;
		
		public Data() {
			super();
		}
		
		public Data(int x, int y, int z, int hit) {
			super(x, y, z);
			hitType = hit;
		}
		
		public void toBytes(ByteBuf buf) {
			super.toBytes(buf);
			buf.writeInt(hitType);
		}
		
		public void fromBytes(ByteBuf buf) {
			super.fromBytes(buf);
			hitType = buf.readInt();
		}
	}
}
