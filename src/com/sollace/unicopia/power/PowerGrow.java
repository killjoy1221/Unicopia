package com.sollace.unicopia.power;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import com.blazeloader.api.world.ApiWorld;
import com.blazeloader.api.world.AuxilaryEffects;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Race;
import com.sollace.unicopia.Unicopia;
import com.sollace.unicopia.client.ClientSide;

public class PowerGrow extends Power<Power.LocationData> {

	public PowerGrow(String name, int key) {
		super(name, key);
	}
	
	public int getWarmupTime(PlayerExtension player) {
		return 10;
	}
	
	public int getCooldownTime(PlayerExtension player) {
		return 50;
	}

	public boolean canActivate(World w, EntityPlayer player) {
		return true;
	}
	
	public boolean canUse(Race playerSpecies) {
		return playerSpecies == Race.EARTH;
	}

	public LocationData tryActivate(EntityPlayer player, World w) {
		RayTraceResult ray = ClientSide.getMousOver();
		if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
			return new LocationData(ray.getBlockPos());
		}
		return null;
	}

	public LocationData fromBytes(ByteBuf buf) {
		LocationData result = new LocationData();
		result.fromBytes(buf);
		return result;
	}
	
	public void apply(EntityPlayer player, LocationData data) {
		int diameter = 2;
		int count = 0;
		for (int x = -diameter; x < diameter; x++) {
			for (int y = -diameter; y < diameter; y++) {
				for (int z = -diameter; z < diameter; z++) {
					BlockPos pos = new BlockPos(data.x + x, data.y + y, data.z + z);
					IBlockState state = player.world.getBlockState(pos);
					count += applySingle(player.world, state, pos);
				}
			}
		}
		if (count > 0) TakeFromPlayer(player, count * 5);
	}
	
	protected int applySingle(World w, IBlockState state, BlockPos pos) {
		if (state != null && state.getBlock() instanceof IGrowable && !(state.getBlock() instanceof BlockGrass)) {
			IGrowable g = ((IGrowable)state.getBlock());
            if (g.canGrow(w, pos, state, w.isRemote) && g.canUseBonemeal(w, w.rand, pos, state)) {
        		while (g.canGrow(w, pos, state, w.isRemote)) {
                    g.grow(w, w.rand, pos, state);
                    ApiWorld.playAuxSFX(w, AuxilaryEffects.BONEMEAL, pos, 0);
                    if (g instanceof BlockDoublePlant) ApiWorld.playAuxSFX(w, AuxilaryEffects.BONEMEAL, pos.up(), 0);
                    state = w.getBlockState(pos);
                }
            	return 1;
            }
		}
		return 0;
	}
	
	public void preApply(EntityPlayer player) {
		spawnParticles(Unicopia.Particles.unicorn.getData(), player, 1);
	}
	
	public void postApply(EntityPlayer player) {
		
	}

}
