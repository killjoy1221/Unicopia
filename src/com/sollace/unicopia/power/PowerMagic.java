package com.sollace.unicopia.power;

import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Race;
import com.sollace.unicopia.effect.SpellList;
import com.sollace.unicopia.effect.SpellShield;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PowerMagic extends Power<PowerMagic.MagicData> {
	
	public PowerMagic(String name, int key) {
		super(name, key);
	}
	
	public int getWarmupTime(PlayerExtension player) {
		return "Shield".contentEquals(SpellList.getName(player.getEffect())) ? 0 : 20;
	}
	
	public int getCooldownTime(PlayerExtension player) {
		return 0;
	}
	
	public boolean canActivate(World w, EntityPlayer player) {
		return true;
	}
	
	public boolean canUse(Race playerSpecies) {
		return playerSpecies.canCast();
	}
	
	public MagicData tryActivate(EntityPlayer player, World w) {
		return new MagicData(0);
	}
	
	public MagicData fromBytes(ByteBuf buf) {
		MagicData result = new MagicData(0);
		result.fromBytes(buf);
		return result;
	}
	
	public void apply(EntityPlayer player, MagicData data) {
		PlayerExtension prop = PlayerExtension.get(player);
		if (prop.getEffect() instanceof SpellShield) {
			prop.addEffect(null);
		} else {
			prop.addEffect(new SpellShield(data.type));
		}
	}
	
	public void preApply(EntityPlayer player) {
		
	}
	
	public void postApply(EntityPlayer player) {
		
	}
	
	protected class MagicData implements IData {
		
		private int type;
		
		public MagicData(int strength) {
			type = strength;
		}
		
		public void toBytes(ByteBuf buf) {
			buf.writeInt(type);
		}
		
		public void fromBytes(ByteBuf buf) {
			type = buf.readInt();
		}
	}
}
