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
import com.blazeloader.event.listeners.args.ContainerOpenedEventArgs;
import com.blazeloader.event.listeners.args.FallEventArgs;
import com.blazeloader.event.listeners.args.LoginEventArgs;
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
import net.minecraft.client.particle.Particle;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult.Type;;

public class BlazeModUnicopia implements BLMod, OverrideListener, EntityConstructingListener, PacketChannelListener, ServerPlayerListener, ServerCommandProvider, StartupListener, PlayerInteractionListener, TickListener, PlayerListener {
	
	private static final UnicopiaPacketChannel packetHandler = new UnicopiaPacketChannel(Unicopia.CHANNEL);
	
	private final Unicopia instance = new Unicopia();
	private final EventHandler eventHandler = new EventHandler(); 
	private final UKeyHandler keyHandler = new UKeyHandler();
	private final List<String> channels = Arrays.asList(Unicopia.CHANNEL);
	
	@Override
	public String getModId() {
		return Unicopia.MODID;
	}
	
	@Override
	public String getModDescription() {
		return "";
	}
	
	@Override
	public ModVersion getModVersion() {
		return null;
	}
	
	@Override
	public String getName() {
		return Unicopia.NAME;
	}
	
	@Override
	public String getVersion() {
		return Unicopia.VERSION;
	}
	
	@Override
	public void init(File configPath) {
		instance.preInit(configPath);
	}
	
	@Override
	public void start() {
		packetHandler.init();
		instance.init();
		instance.postInit();
	}
	
	@Override
	public void stop() {}
	
	@Override
	public void onTick() {
		if (Unicopia.isClient()) {
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
	
	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {}
	
	@Override
	public void onEntityConstructed(Entity entity) {
		eventHandler.onEntityConstructing(entity);
	}
	
	@Override
	public void onPlayerRespawn(EntityPlayerMP player, EntityPlayerMP oldPlayer, int newDimension, boolean playerWonTheGame) {}
	
	@Override
	public void onPlayerLogout(EntityPlayerMP player) {}
	
	@Override
	public void onPlayerConnect(EntityPlayerMP player, GameProfile profile) {}
	
	@Override
	public void onPlayerLoggedIn(EntityPlayerMP player) {
		eventHandler.onEntityJoinWorld(player);
	}
	
	@Override
	public void provideCommands(ServerCommandManager commandManager) {
		instance.registerCommands(commandManager);
	}
	
	@Override
	public void onPlayerClickedAir(EntityPlayerMP player, MouseButton button, BlockPos tracePos, EnumFacing traceSideHit, Type traceHitType) { }
	
	@Override
	public boolean onPlayerClickedBlock(EntityPlayerMP player, MouseButton button, EnumHand hand, ItemStack stack, BlockPos hitPos, EnumFacing sideHit) {
		if (button == MouseButton.RIGHT) {
			if (player != null && hand == EnumHand.MAIN_HAND) {
				ItemStack currentItem = player.getHeldItem(hand);
				if (currentItem != null && currentItem.getItem() != null) {
					eventHandler.onPlayerRightClick(player, currentItem);
				}
			}
		}
		return true;
	}
	
	@Override
	public void onPlayerTryLoginMP(LoginEventArgs args) { }
	
	@Override
	public void onPlayerLoginMP(PlayerList manager, EntityPlayerMP player) { }
	
	@Override
	public void onPlayerLogoutMP(PlayerList manager, EntityPlayerMP player) { }
	
	@Override
	public void onPlayerRespawnMP(PlayerList manager, EntityPlayerMP oldPlayer, int dimension, boolean causedByDeath) { }
	
	@Override
	public boolean onEntityCollideWithPlayer(Entity entity, EntityPlayer player) { return true; }
	
	@Override
	public void onPlayerFall(EntityPlayer player, FallEventArgs arg) {
		eventHandler.onPlayerFall(player, arg);
	}

	@Override
	public List<String> getChannels() {
		return channels;
	}

	@Override
	public void onCustomPayload(String channel, PacketBuffer data) {
		if (Unicopia.isClient()) {
			packetHandler.onPacketRecievedClient(channel, data);
		}
	}

	@Override
	public void onCustomPayload(EntityPlayerMP sender, String channel, PacketBuffer data) {
		packetHandler.onPacketRecievedServer(channel, sender, data);
	}
	
	@Override
	public Particle onSpawnParticle(int particleId, double x, double y, double z, double p1, double p2, double p3, Particle currParticle) {
		return null;
	}
	
	@Override
	public boolean onContainerOpened(AbstractClientPlayer player, ContainerOpenedEventArgs e) {
		if (e.inventoryId.contentEquals("unicopia:book")) {
			ApiGuiClient.openGUI(new GuiScreenSpellBook(player));
		}
		return false;
	}

	@Override
	public void onPlayerCollideWithBlock(IBlockState state, BlockPos pos, EntityPlayer player) { }
	
	@Override
	public boolean onPlayerSwapItems(EntityPlayerMP player) { return false; }
}
