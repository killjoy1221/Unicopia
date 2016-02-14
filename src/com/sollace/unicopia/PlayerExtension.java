package com.sollace.unicopia;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;

import com.blazeloader.api.client.ApiClient;
import com.blazeloader.api.client.render.SkinType;
import com.blazeloader.api.entity.properties.EntityPropertyManager;
import com.blazeloader.api.entity.properties.IEntityProperties;
import com.google.common.collect.Lists;
import com.sollace.unicopia.effect.SpellList;
import com.sollace.unicopia.disguise.Disguise;
import com.sollace.unicopia.effect.IMagicEffect;
import com.sollace.unicopia.power.Power;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class PlayerExtension implements IEntityProperties {
	public static SkinType skinType;
	
	private static final AttributeModifier earthPonyStrength = new AttributeModifier(UUID.fromString("290aba76-6c49-4f12-9279-8194818b8f2c"), "Earth Pony Strength", 0.6, 2);
	
	private List<Integer> unlockedPages = new ArrayList<Integer>();
	private int maxUnlockedPage = 0;
	
	private EntityPlayer player;
	private final Disguise disguise = new Disguise();
	
	private IMagicEffect effect;
	
	public int ticksSinceLanding = 0;
	
	public float nextStepDistance = 1;
	public boolean isFlying = false;
	
	private boolean abilityTriggered = false;
	private int abilityWarmup = 0;
	private int abilityCooldown = 0;
	private Power activeAbility = null;
	
	private PlayerExtension(EntityPlayer p) {
		player = p;
	}
	
	public void setOwningEntity(Entity owner) {
		player = (EntityPlayer)owner;
	}
	
	public Disguise getDisguise() {
		return disguise;
	}
	
	public void setDisguise(EntityLivingBase entity) {
		disguise.set(player, entity);
	}
	
	public EntityPlayer getPlayer() {
		return player;
	}
	
	public int getTotalPagesUnlocked() {
		return unlockedPages.size();
	}
	
	public boolean unlockPage(int pageIndex) {
		if (!hasPageUnlock(pageIndex)) {
			if (pageIndex > maxUnlockedPage) maxUnlockedPage = pageIndex;
			unlockedPages.add(Integer.valueOf(pageIndex));
			return true;
		}
		return false;
	}
	
	public boolean hasPageUnlock(int pageIndex) {
		return pageIndex == 0 || pageIndex == 7 || unlockedPages.contains(Integer.valueOf(pageIndex));
	}
	
	public int getMaxUnlocked() {
		return maxUnlockedPage;
	}
	
	public void addEffect(IMagicEffect effect) {
		this.effect = effect;
	}
	
	public IMagicEffect getEffect() {
		return effect;
	}
	
	public void writeToNBT(NBTTagCompound compound) {
		if (activeAbility != null) {
			NBTTagCompound ability = new NBTTagCompound();
			ability.setString("name", activeAbility.getKeyName());
			ability.setInteger("warmup", abilityWarmup);
			ability.setInteger("cooldown", abilityCooldown);
			ability.setBoolean("triggered", abilityTriggered);
			compound.setTag("pony_ability", ability);
		}
		
		if (disguise.isActive() && PlayerSpeciesRegister.getPlayerSpecies(player) == Race.CHANGELING) {
			NBTTagCompound disguise = new NBTTagCompound();
			this.disguise.writeToNBT(disguise);
			compound.setTag("disguise", disguise);
		}
		
		compound.setBoolean("pony_ability_flying", player.capabilities.isFlying);
		
		if (getTotalPagesUnlocked() > 0) {
			compound.setIntArray("unlocked_pages", ArrayUtils.toPrimitive(unlockedPages.toArray(new Integer[unlockedPages.size()])));
		}
		
		if (effect != null) {
			NBTTagCompound effectTag = new NBTTagCompound();
			effectTag.setString("effect_id", SpellList.getName(effect));
			effect.writeToNBT(effectTag);
			compound.setTag("pony_effect", effectTag);
		}
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("pony_ability")) {
			NBTTagCompound ability = compound.getCompoundTag("pony_ability");
			activeAbility = Power.powerFromName(ability.getString("name"));
			abilityWarmup = ability.getInteger("warmup");
			abilityCooldown = ability.getInteger("cooldown");
			abilityTriggered = ability.getBoolean("triggered");
		}
		
		if (compound.hasKey("disguise")) {
			this.disguise.readFromNBT(compound.getCompoundTag("disguise"));
		}
		
		isFlying = compound.getBoolean("pony_ability_flying");
		
		maxUnlockedPage = 0;
		if (compound.hasKey("unlocked_pages")) {
			unlockedPages = Lists.newArrayList(ArrayUtils.toObject(compound.getIntArray("unlocked_pages")));
			for (Integer i : unlockedPages) {
				if (i > maxUnlockedPage) maxUnlockedPage = i;
			}
		} else {
			unlockedPages.clear();
		}
		
		if (compound.hasKey("pony_effect")) {
			NBTTagCompound effectTag = compound.getCompoundTag("pony_effect");
			effect = SpellList.forName(effectTag.getString("effect_id"));
			if (effect != null) {
				effect.readFromNBT(effectTag);
			}
		}
	}
	
	public void entityInit(Entity entity, World world) {}
	
	public void addEntityCrashInfo(CrashReportCategory catagory) {}
	
	public void tryUseAbility(Power p) {
		if ((abilityTriggered && abilityCooldown <= 0) || (p != null && activeAbility != p)) {
			abilityTriggered = false;
			activeAbility = p;
			abilityWarmup = 0;
			abilityCooldown = p == null ? 0 : p.getCooldownTime(this);
		}
	}
	
	public int getCooldownRemaining() {
		return abilityCooldown;
	}
	
	public void onEntityUpdate(Entity e) {
		player = (EntityPlayer)e;
		Race species = PlayerSpeciesRegister.getPlayerSpecies(player);
		if (activeAbility != null && activeAbility.canUse(species)) {
			if (!abilityTriggered) {
				if (abilityWarmup < activeAbility.getWarmupTime(this)) {
					activeAbility.preApply(player);
					abilityWarmup++;
				} else {
					if (player.getCommandSenderName().contentEquals(ApiClient.getPlayer().getCommandSenderName())) {
						if (activeAbility.canActivate(player.worldObj, player)) {
							abilityTriggered = activeAbility.Activated(player, player.worldObj);
							if (!abilityTriggered) {
								activeAbility = null;
								abilityCooldown = 0;
							}
						} else {
							activeAbility = null;
							abilityCooldown = 0;
						}
					}
				}
			} else {
				if (abilityCooldown > 0) {
					activeAbility.postApply(player);
					abilityCooldown--;
				}
			}
		}
		
		if (player.getHealth() == 0) {
			disguise.unset();
		}
		
		if (disguise.isActive()) {
			if (species == Race.CHANGELING) {
				disguise.tick(player);
			}
		}
		
		updateFlightState(species);
		if (!species.canCast()) {
			effect = null;
		}
		if (effect != null) {
			if (player.worldObj.isRemote && player.worldObj.getWorldTime() % 10 == 0) {
				effect.render(player);
			}
			if (!effect.update(player)) {
				effect = null;
			}
		}
		applyModifiers(species);
	}
	
	protected void applyModifiers(Race species) {
		IAttributeInstance strength = player.getEntityAttribute(SharedMonsterAttributes.attackDamage);
		if (species.canUseEarth()) {
			if (strength.getModifier(earthPonyStrength.getID()) == null) {
				strength.applyModifier(earthPonyStrength);
			}
		} else {
			if (strength.getModifier(earthPonyStrength.getID()) != null) {
				strength.removeModifier(earthPonyStrength);
			}
		}
	}
	
	public static final PlayerExtension register(EntityPlayer player) {
		PlayerExtension result = new PlayerExtension(player);
		EntityPropertyManager.registerEntityProperties(player, result);
		return result;
	}
	
	public static final PlayerExtension get(EntityPlayer player) {
		PlayerExtension result = EntityPropertyManager.getEntityPropertyObject(player, PlayerExtension.class);
		if (result == null) return register(player);
		return result;
	}
	
	protected void updateFlightState(Race species) {
		if (!player.capabilities.isCreativeMode) {
			if (species.canFly()) {
				if (ticksSinceLanding < 2) ticksSinceLanding++;
				if (disguise.canFly()) {
					player.capabilities.allowFlying = true;
					if (player.capabilities.isFlying) {
						ticksSinceLanding = 0;
						player.addExhaustion(player.worldObj.rand.nextInt(15) < 5 ? 0.03f : 0.015f);
					}
				} else {
					player.capabilities.allowFlying = player.capabilities.isFlying = false;
				}
			}
		}
		if (player.capabilities.isFlying) {
			player.fallDistance = 0;
		}
	}
	
	public void updateIsFlying(Race species) {
		updateIsFlying(species, isFlying);
	}
	
	public void updateIsFlying(Race species, boolean flying) {
		if (!player.capabilities.isCreativeMode) {
			player.capabilities.allowFlying = species.canFly() && disguise.canFly();
			if (player.capabilities.allowFlying) {
				player.capabilities.isFlying |= flying;
				isFlying = player.capabilities.isFlying;
				if (isFlying) {
					ticksSinceLanding = 0;
				}
			} else {
				player.capabilities.isFlying = false;
				isFlying = false;
			}
		}
	}
	
	public void fall(float distance, float damageMultiplier) {
        if (distance <= 0) return;
        PotionEffect potioneffect = player.getActivePotionEffect(Potion.jump);
        float potion = potioneffect != null ? potioneffect.getAmplifier() + 1 : 0;
        int i = MathHelper.ceiling_float_int((distance - 8.0F - potion) * damageMultiplier);
        if (i > 0) {
            int j = MathHelper.floor_double(player.posX);
            int k = MathHelper.floor_double(player.posY - 0.20000000298023224D);
            int l = MathHelper.floor_double(player.posZ);
            Block block = player.worldObj.getBlockState(new BlockPos(j, k, l)).getBlock();
            
            if (block.getMaterial() != Material.air && block.getMaterial() != Unicopia.Materials.cloud) {
                player.playSound(getFallSound(i), 1, 1);
                player.attackEntityFrom(DamageSource.fall, i);
                Block.SoundType soundtype = block.stepSound;
                player.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.5f, soundtype.getFrequency() * 0.75f);
            }
        }
	}
	
    protected String getFallSound(int distance) {
        return distance > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small";
    }
    
    public SkinType getSkinType() {
    	if (disguise.isActive() && !disguise.isPlayer() && player.hurtTime <= 0) {
	    	if (PlayerSpeciesRegister.getPlayerSpecies(player) == Race.CHANGELING) {
	    		return skinType;
	    	}
    	}
    	return null;
    }
    
    public Boolean isWearing(EnumPlayerModelParts part) {
    	if (part == EnumPlayerModelParts.CAPE && disguise.isPlayer()) return disguise.getPlayer().hasCape();
    	return null;
    }
    
    public NetworkPlayerInfo getPlayerInfo() {
    	if (disguise.isPlayer()) {
    		return disguise.getPlayer().getPlayerInfo();
    	}
    	return null;
    }
}
