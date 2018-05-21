package com.sollace.unicopia.effect;

import java.util.List;

import com.blazeloader.api.privileged.ITasked;

import com.sollace.unicopia.entity.EntitySpell;
import com.sollace.util.Util;
import com.sollace.util.entity.EntityAIFollowOwner;

import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class SpellMinion extends Spell {
	private IMagicEffect piggyBackSpell;
	
	private boolean firstUpdate = true;
	
	public void setDead() {
		isDead = true;
		if (piggyBackSpell != null) {
			piggyBackSpell.setDead();
		}
	}
	
	public boolean getDead() {
		return isDead || (piggyBackSpell != null && piggyBackSpell.getDead());
	}
	
	public boolean updateAt(EntitySpell source, World w, double x, double y, double z, int level) {
		if (firstUpdate) {
			firstUpdate = false;
			((PathNavigateGround)source.getNavigator()).setCanSwim(false);
			((ITasked)source).getAITasks().addTask(1, new EntityAISwimming(source));
			((ITasked)source).getAITasks().addTask(2, new EntityAIFollowOwner<EntitySpell>(source, 1, 4, 6));
		}
		
		if (piggyBackSpell == null) {
			AxisAlignedBB bb = new AxisAlignedBB(x - 2, y - 2, z - 2, x + 2, y + 2, z + 2);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<EntitySpell> entities = (List)w.getEntitiesInAABBexcluding(source, bb, Util.SPELLS);
			for (EntitySpell i : entities) {
				IMagicEffect e = i.getEffect();
				if (e != null && !(e instanceof SpellMinion)) {
					piggyBackSpell = e;
					i.setEffect(null);
					break;
				}
			}
		}
		
		return piggyBackSpell != null && piggyBackSpell.updateAt(source, w, x, y, z, level);
	}
	
	public void renderAt(EntitySpell source, World w, double x, double y, double z, int level) {
		if (piggyBackSpell != null) piggyBackSpell.renderAt(source, w, x, y, z, level);
	}
	
	public boolean allowAI() {
		return true;
	}
	
	public void writeToNBT(NBTTagCompound compound) {
		if (piggyBackSpell != null) {
			NBTTagCompound effectTag = new NBTTagCompound();
			effectTag.setString("piggyback_id", SpellList.getName(piggyBackSpell));
			piggyBackSpell.writeToNBT(effectTag);
			compound.setTag("effect", effectTag);
        }
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("effect")) {
			NBTTagCompound effectTag = compound.getCompoundTag("effect");
			piggyBackSpell = SpellList.forName(effectTag.getString("piggyback_id"));
			if (piggyBackSpell != null) {
				piggyBackSpell.readFromNBT(effectTag);
			}
		}
	}
}
