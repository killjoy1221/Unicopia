package com.sollace.unicopia.main;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.blazeloader.api.client.ApiClient;
import com.blazeloader.api.client.gui.ApiGuiClient;
import com.blazeloader.bl.mod.BLMod;
import com.blazeloader.event.listeners.EntityConstructingListener;
import com.blazeloader.event.listeners.PacketChannelListener;
import com.blazeloader.event.listeners.PlayerListener;
import com.blazeloader.event.listeners.StartupListener;
import com.blazeloader.event.listeners.TickListener;
import com.blazeloader.event.listeners.client.OverrideListener;
import com.blazeloader.util.version.type.ModVersion;
import com.mojang.authlib.GameProfile;
import com.mumfrey.liteloader.PlayerInteractionListener;
import com.mumfrey.liteloader.ServerCommandProvider;
import com.mumfrey.liteloader.ServerPlayerListener;
import com.sollace.unicopia.EventHandler;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.UnicopiaPacketChannel;
import com.sollace.unicopia.client.UKeyHandler;
import com.sollace.unicopia.client.gui.GuiScreenSpellBook;
import com.sollace.unicopia.Unicopia;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class BlazeModUnicopia implements BLMod, OverrideListener, EntityConstructingListener, PacketChannelListener, ServerPlayerListener, ServerCommandProvider, StartupListener, PlayerInteractionListener, TickListener, PlayerListener {
	
	private static final UnicopiaPacketChannel packetHandler = new UnicopiaPacketChannel(Unicopia.CHANNEL);
	
	private final Unicopia instance = new Unicopia();
	private final EventHandler eventHandler = new EventHandler(); 
	private final UKeyHandler keyHandler = new UKeyHandler();
	private final List<String> channels = Arrays.asList(instance.CHANNEL);
	
	public String getModId() {
		return instance.MODID;
	}
	
	public String getModDescription() {
		return "";
	}
	
	public ModVersion getModVersion() {
		return null;
	}
	
	public String getName() {
		return instance.NAME;
	}
	
	public String getVersion() {
		return instance.VERSION;
	}
	
	public void init(File configPath) {
		instance.preInit(configPath);
	}
	
	public void start() {
		packetHandler.init();
		instance.init();
		instance.postInit();
	}
	
	public void stop() {}
	
	public void onTick() {
		if (instance.isClient()) {
			EntityPlayer player = ApiClient.getPlayer();
			if (player != null) {
				PlayerExtension prop = PlayerExtension.get(player);
				if (prop != null) {
					keyHandler.onKeyInput();
					prop.onEntityUpdate(player);
				}
			}
		}
	}
	
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {}
	
	public void onEntityConstructed(Entity entity) {
		eventHandler.onEntityConstructing(entity);
	}
	
	public void onPlayerRespawn(EntityPlayerMP player, EntityPlayerMP oldPlayer, int newDimension, boolean playerWonTheGame) {}
	
	public void onPlayerLogout(EntityPlayerMP player) {}
	
	public void onPlayerConnect(EntityPlayerMP player, GameProfile profile) {}
	
	public void onPlayerLoggedIn(EntityPlayerMP player) {
		eventHandler.onEntityJoinWorld(player);
	}
	
	public void provideCommands(ServerCommandManager commandManager) {
		instance.registerCommands(commandManager);
	}
	
	public void onPlayerClickedAir(EntityPlayerMP player, MouseButton button, BlockPos tracePos, EnumFacing traceSideHit, MovingObjectType traceHitType) {
		onPlayerClickedBlock(player, button, tracePos, traceSideHit);
	}
	
	public boolean onPlayerClickedBlock(EntityPlayerMP player, MouseButton button, BlockPos hitPos, EnumFacing sideHit) {
		if (button == MouseButton.RIGHT) {
			if (player != null) {
				ItemStack currentItem = player.getCurrentEquippedItem();
				if (currentItem != null && currentItem.getItem() != null) {
					eventHandler.onPlayerRightClick(player, currentItem);
				}
			}
		}
		return true;
	}
	
	public void onPlayerTryLoginMP(LoginEventArgs args) {
	}
	
	public void onPlayerLoginMP(ServerConfigurationManager manager, EntityPlayerMP player) {
	}
	
	public void onPlayerLogoutMP(ServerConfigurationManager manager, EntityPlayerMP player) {
		
	}
	
	public void onPlayerRespawnMP(ServerConfigurationManager manager, EntityPlayerMP oldPlayer, int dimension, boolean causedByDeath) {
	}
	
	public boolean onEntityCollideWithPlayer(Entity entity, EntityPlayer player) {
		return true;
	}
	
	public void onPlayerFall(EntityPlayer player, FallEventArgs arg) {
		//eventHandler.onLivingFall(player);
		eventHandler.onPlayerFall(player, arg);
	}

	@Override
	public List<String> getChannels() {
		return channels;
	}

	@Override
	public void onCustomPayload(String channel, PacketBuffer data) {
		if (instance.isClient()) {
			packetHandler.onPacketRecievedClient(channel, data);
		}
	}

	@Override
	public void onCustomPayload(EntityPlayerMP sender, String channel, PacketBuffer data) {
		packetHandler.onPacketRecievedServer(channel, sender, data);
	}
	
	public S0EPacketSpawnObject onCreateSpawnPacket(Entity entity, boolean isHandled) {
		return null;
	}
	
	public boolean onAddEntityToTracker(EntityTracker tracker, Entity entity, boolean isHandled) {
		return false;
	}
	
	public EntityFX onSpawnParticle(int particleId, double x, double y, double z, double p1, double p2, double p3, EntityFX currParticle) {
		return null;
	}
	
	public boolean onContainerOpened(AbstractClientPlayer player, ContainerOpenedEventArgs e) {
		if (e.inventoryId.contentEquals("unicopia:book")) {
			ApiGuiClient.openGUI(new GuiScreenSpellBook(player));
		}
		return false;
	}

	@Override
	public void onPlayerCollideWithBlock(IBlockState state, BlockPos pos, EntityPlayer player) {
		
	}
}
