package com.sollace.util;

import java.util.UUID;

import com.blazeloader.util.data.INBTWritable;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.ResourceLocation;

public class PlayerIdent implements INBTWritable {
	private GameProfile gameProfile;
	private UUID uuid;
	private NetworkPlayerInfo playerInfo;
	
	public PlayerIdent() {
		
	}
	
	public PlayerIdent(EntityPlayer player) {
		gameProfile = player.getGameProfile();
	}
	
	public PlayerIdent(String username) {
		this(UUID.fromString(PreYggdrasilConverter.func_152719_a(username)), username);
	}
	
	public PlayerIdent(UUID uuid, String username) {
		gameProfile = new GameProfile(uuid, username);
	}
		
	public UUID getUniqueID() {
		if (uuid == null) {
			uuid = EntityPlayer.getUUID(getGameProfile());
		}
		return uuid;
	}
	
	public GameProfile getGameProfile() {
		return gameProfile;
	}
	
    public boolean hasPlayerInfo() {
        return getPlayerInfo() != null;
    }
	
	public NetworkPlayerInfo getPlayerInfo() {
		if (playerInfo == null) {
            playerInfo = new DummyNetworkPlayerInfo(getGameProfile());
        }
        return playerInfo;
	}
	
    public boolean hasSkin() {
        NetworkPlayerInfo info = getPlayerInfo();
        return info != null && info.hasLocationSkin();
    }
    
    public ResourceLocation getLocationSkin() {
        NetworkPlayerInfo info = getPlayerInfo();
        return info == null ? DefaultPlayerSkin.getDefaultSkin(getUniqueID()) : info.getLocationSkin();
    }
    
    public ResourceLocation getLocationCape() {
        NetworkPlayerInfo info = getPlayerInfo();
        return info == null ? null : info.getLocationCape();
    }
    
    private class DummyNetworkPlayerInfo extends NetworkPlayerInfo {
    	ResourceLocation locationSkin;
    	private GameProfile gameProfile;
    	
    	public DummyNetworkPlayerInfo(GameProfile profile) {
    		super(profile);
    		gameProfile = profile;
    	}
    	
    	public boolean hasLocationSkin() {
            return locationSkin != null;
        }
    	
        public ResourceLocation getLocationSkin() {
        	if (locationSkin == null) {
        		locationSkin = AbstractClientPlayer.getLocationSkin(getGameProfile().getName());
        		AbstractClientPlayer.getDownloadImageSkin(locationSkin, gameProfile.getName());
        	}
        	return locationSkin;
        }
    }
    
	public void writeToNBT(NBTTagCompound tagCompound) {
		tagCompound.setLong("UUIDMost", getUniqueID().getMostSignificantBits());
		tagCompound.setLong("UUIDLeast", getUniqueID().getLeastSignificantBits());
		tagCompound.setString("Name", this.getGameProfile().getName());
	}
	
	public void readFromNBT(NBTTagCompound tagCompound) {
		uuid = new UUID(tagCompound.getLong("UUIDMost"), tagCompound.getLong("UUIDLeast"));
		gameProfile = new GameProfile(uuid, tagCompound.getString("Name"));
	}
}
