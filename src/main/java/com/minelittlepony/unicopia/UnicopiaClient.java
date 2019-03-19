package com.minelittlepony.unicopia;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.minelittlepony.gui.Button;
import com.minelittlepony.unicopia.entity.EntityFakeClientPlayer;
import com.minelittlepony.unicopia.extern.MineLP;
import com.minelittlepony.unicopia.forgebullshit.BuildInTexturesBakery;
import com.minelittlepony.unicopia.forgebullshit.ItemModels;
import com.minelittlepony.unicopia.gui.GuiScreenSettings;
import com.minelittlepony.unicopia.init.UEntities;
import com.minelittlepony.unicopia.init.UItems;
import com.minelittlepony.unicopia.init.UParticles;
import com.minelittlepony.unicopia.input.Keyboard;
import com.minelittlepony.unicopia.input.MouseControl;
import com.minelittlepony.unicopia.input.MovementControl;
import com.minelittlepony.unicopia.inventory.gui.GuiOfHolding;
import com.minelittlepony.unicopia.network.MsgRequestCapabilities;
import com.minelittlepony.unicopia.network.UNetworkHandler;
import com.minelittlepony.unicopia.player.IPlayer;
import com.minelittlepony.unicopia.player.PlayerSpeciesList;
import com.minelittlepony.unicopia.render.DisguiseRenderer;
import com.minelittlepony.util.gui.ButtonGridLayout;
import com.minelittlepony.util.lang.ClientLocale;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IInteractionObject;

import static com.minelittlepony.util.gui.ButtonGridLayout.*;

public class UnicopiaClient extends UClient {

    /**
     * The race preferred by the client - as determined by mine little pony.
     * Human if minelp was not installed.
     * <p>
     * This is not neccessarily the _actual_ race used for the player,
     * as the server may not allow certain race types, or the player may override
     * this option in-game themselves.
     */
    private static Race clientPlayerRace = getclientPlayerRace();

    private static Race getclientPlayerRace() {
        if (!UConfig.instance().ignoresMineLittlePony()
                && Minecraft.getMinecraft().player != null) {
            Race race = MineLP.getPlayerPonyRace();

            if (!race.isDefault()) {
                return race;
            }
        }


        return UConfig.instance().getPrefferedRace();
    }

    static void addUniButton(List<GuiButton> buttons) {
        ButtonGridLayout layout = new ButtonGridLayout(buttons);

        GuiButton uni = new Button(0, 0, 150, 20, ClientLocale.format("gui.unicopia"), b -> {
            Minecraft mc = Minecraft.getMinecraft();

            mc.displayGuiScreen(new GuiScreenSettings(mc.currentScreen));
        });

        List<Integer> possibleXCandidates = list(layout.getColumns());
        List<Integer> possibleYCandidates = list(layout.getRows());

        uni.y = last(possibleYCandidates, 1);

        if (layout.getRows()
                .filter(y -> layout.getRow(y).size() == 1).count() < 2) {
            uni.y += 25;
            uni.x = first(possibleXCandidates, 0);

            layout.getRow(last(possibleYCandidates, 0)).forEach(button -> {
                button.y = Math.max(button.y, uni.y + uni.height + 13);
            });
        } else {
            uni.x = first(possibleXCandidates, 2);
        }

        layout.getElements().add(uni);
    }

    private void registerModels() {
        ItemModels.registerAll(
                UItems.cloud,

//                UItems.apple_red,
                UItems.apple_green, UItems.apple_sweet, UItems.apple_sour,

                UItems.zap_apple,
                UItems.rotten_apple, UItems.cooked_zap_apple, UItems.dew_drop,

                UItems.tomato, UItems.cloudsdale_tomato, UItems.moss,

                UItems.cloud, UItems.cloud_matter, UItems.cloud_block, UItems.enchanted_cloud_block, UItems.packed_cloud_block,
                UItems.cloud_stairs,
                UItems.cloud_slab, UItems.enchanted_cloud_slab, UItems.packed_cloud_slab,
                UItems.cloud_fence, UItems.cloud_banister,
                UItems.cloud_farmland, UItems.mist_door, UItems.library_door, UItems.bakery_door, UItems.diamond_door, UItems.anvil,

                UItems.bag_of_holding, UItems.gem, UItems.corrupted_gem, UItems.spellbook, UItems.mug, UItems.enchanted_torch,
                UItems.staff_meadow_brook, UItems.staff_remembrance, UItems.alicorn_amulet,

                UItems.alfalfa_seeds, UItems.alfalfa_leaves,
                UItems.cereal, UItems.sugar_cereal, UItems.sugar_block,
                UItems.tomato_seeds,

                UItems.hive, UItems.chitin_shell, UItems.chitin_block, UItems.chissled_chitin, UItems.cuccoon, UItems.slime_layer,

                UItems.apple_seeds, UItems.apple_leaves,

                UItems.daffodil_daisy_sandwich, UItems.hay_burger, UItems.hay_fries, UItems.salad, UItems.wheat_worms,

                UItems.apple_cider, UItems.juice, UItems.burned_juice,

                UItems.record_crusade, UItems.record_pet, UItems.record_popular, UItems.record_funk);

        BuildInTexturesBakery.getBuiltInTextures().add(new ResourceLocation(Unicopia.MODID, "items/empty_slot_gem"));

    }

    @Override
    public void displayGuiToPlayer(EntityPlayer player, IInteractionObject inventory) {
        if (player instanceof EntityPlayerSP) {
            if ("unicopia:itemofholding".equals(inventory.getGuiID())) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiOfHolding(inventory));
            }
        } else {
            super.displayGuiToPlayer(player, inventory);
        }
    }

    @Override
    @Nullable
    public EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    @Nullable
    public EntityPlayer getPlayerByUUID(UUID playerId) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.player.getUniqueID().equals(playerId)) {
            return mc.player;
        }

        return mc.world.getPlayerEntityByUUID(playerId);
    }

    @Nonnull
    public EntityPlayer createPlayer(Entity observer, GameProfile profile) {
        return new EntityFakeClientPlayer(observer.world, profile);
    }

    @Override
    public boolean isClientPlayer(@Nullable EntityPlayer player) {
        if (getPlayer() == player) {
            return true;
        }

        if (getPlayer() == null || player == null) {
            return false;
        }

        return IPlayer.equal(getPlayer(), player);
    }

    @Override
    public int getViewMode() {
        return Minecraft.getMinecraft().gameSettings.thirdPersonView;
    }

    @Override
    public void postRenderEntity(Entity entity) {
        if (entity instanceof EntityPlayer) {
            IPlayer iplayer = PlayerSpeciesList.instance().getPlayer((EntityPlayer) entity);

            if (iplayer.getGravity().getGravitationConstant() < 0) {
                GlStateManager.translate(0, entity.height, 0);
                GlStateManager.scale(1, -1, 1);
                entity.prevRotationPitch *= -1;
                entity.rotationPitch *= -1;
            }
        }
    }

    @Override
    public boolean renderEntity(Entity entity, float renderPartialTicks) {

        if (DisguiseRenderer.instance().renderDisguise(entity, renderPartialTicks)) {
            return true;
        }

        if (entity instanceof EntityPlayer) {
            IPlayer iplayer = PlayerSpeciesList.instance().getPlayer((EntityPlayer) entity);

            if (iplayer.getGravity().getGravitationConstant() < 0) {
                GlStateManager.scale(1, -1, 1);
                GlStateManager.translate(0, -entity.height, 0);
                entity.prevRotationPitch *= -1;
                entity.rotationPitch *= -1;
            }

            if (DisguiseRenderer.instance().renderDisguiseToGui(iplayer)) {
                return true;
            }

            if (iplayer.isInvisible()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void preInit() {
        UEntities.preInit();
        UParticles.init();
    }

    @Override
    public void init() {
        clientPlayerRace = getclientPlayerRace();
    }

    @Override
    public void postInit() {
        registerModels();
    }

    public void tick() {
        EntityPlayer player = Unicopia.proxy.getPlayer();

        if (player != null && !player.isDead) {
            Race newRace = getclientPlayerRace();

            if (newRace != clientPlayerRace) {
                clientPlayerRace = newRace;

                UNetworkHandler.INSTANCE.sendToServer(new MsgRequestCapabilities(clientPlayerRace));
            }
        }

        Keyboard.getKeyHandler().onKeyInput();

        if (player instanceof EntityPlayerSP) {
            EntityPlayerSP sp = (EntityPlayerSP) player;

            MovementInput movement = sp.movementInput;

            if (!(movement instanceof MovementControl)) {
                sp.movementInput = new MovementControl(movement);
            }
        }

        Minecraft mc = Minecraft.getMinecraft();

        if (!(mc.mouseHelper instanceof MouseControl)) {
            mc.mouseHelper = new MouseControl();
        }
    }
}
