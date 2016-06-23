package com.sollace.unicopia;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;

import com.blazeloader.api.client.render.SkinType;
import com.blazeloader.event.listeners.args.FallEventArgs;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;
import com.sollace.unicopia.network.RequestSpeciesPacket;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

public class EventHandler {
	
	public void onEntityConstructing(Entity entity) {
		if (entity instanceof EntityPlayer && PlayerExtension.get((EntityPlayer)entity) == null) {
			PlayerExtension.register((EntityPlayer)entity);
		}
	}
	
	public void onEntityJoinWorld(Entity entity) {
		if (entity instanceof EntityPlayer) {
			if (!entity.worldObj.isRemote && entity instanceof EntityPlayerMP) {
				UnicopiaPacketChannel.instance().sendToClient(new RequestSpeciesPacket.Message(), (EntityPlayerMP)entity);
			}
		}
	}
	
	
	/*public void onLivingFall(Entity entity) {
		int x = MathHelper.floor_double(entity.posX);
		int y = MathHelper.floor_double(entity.posY - 0.20000000298023224D);
		int z = MathHelper.floor_double(entity.posZ);
		BlockPos pos = new BlockPos(x, y, z);
		IBlockState state = entity.worldObj.getBlockState(pos);
		if (state.getBlock().getMaterial() == Unicopia.Materials.cloud) {
			state.getBlock().onEntityCollidedWithBlock(entity.worldObj, pos, state, entity);
		}
	}*/
	
	public void onPlayerFall(EntityPlayer player, FallEventArgs event) {
		if (player.worldObj.isRemote == false) {
			if (PlayerSpeciesRegister.getPlayerSpecies(player).canFly()) {
				PlayerExtension prop = PlayerExtension.get(player);
				float distance = event.getFallDistance();
				if (prop.ticksSinceLanding > 1) {
					prop.ticksSinceLanding = 0;
			        if (player.fallDistance >= 2.0F) {
			            player.addStat(StatList.distanceFallenStat, (int)Math.round(distance * 100));
			        }
			        prop.fall(distance, event.getDamageMultiplier());
				}
				event.cancel();
			}
		}
	}
	
	public void onPlayerRightClick(EntityPlayer player, ItemStack item) {
		if (item.getItem() instanceof ItemFood) {
			if (PlayerSpeciesRegister.getPlayerSpecies(player) == Race.CHANGELING) {
				player.addPotionEffect(new PotionEffect(Potion.weakness.id, 2000, 2));
				player.addPotionEffect(new PotionEffect(Potion.confusion.id, 2000, 2));
			}
		}
	}
	
	public static void onVillagerInteract(ReturnEventInfo<EntityVillager, Boolean> event, EntityPlayer player) {
		if (player.isSneaking()) event.setReturnValue(false);
	}
	
	public static void onIsWearing(ReturnEventInfo<EntityPlayer, Boolean> event, EnumPlayerModelParts part) {
        Boolean result = PlayerExtension.get(event.getSource()).isWearing(part);
        if (result != null) event.setReturnValue(result);
    }
	
	public static void onGetSkinType(ReturnEventInfo<AbstractClientPlayer, String> event) {
		SkinType result = PlayerExtension.get(event.getSource()).getSkinType();
		if (result != null) event.setReturnValue(result.toString());
	}
	
	public static void onGetPlayerInfo(ReturnEventInfo<AbstractClientPlayer, NetworkPlayerInfo> event) {
		NetworkPlayerInfo info = PlayerExtension.get(event.getSource()).getPlayerInfo();
		if (info != null) event.setReturnValue(info);
	}
	
	public static void onGetEyeHeight(ReturnEventInfo<EntityPlayer, Float> event) {
		PlayerExtension prop = PlayerExtension.get(event.getSource());
		if (prop.getDisguise().isActive() && !prop.getDisguise().isPlayer()) { //Eye Height for disguises
			event.setReturnValue(prop.getDisguise().getEntity().getEyeHeight());
		}
	}
}



