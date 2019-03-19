package com.minelittlepony.unicopia.init;

import javax.annotation.Nullable;

import com.minelittlepony.unicopia.CloudType;
import com.minelittlepony.unicopia.Unicopia;
import com.minelittlepony.unicopia.block.BlockAlfalfa;
import com.minelittlepony.unicopia.block.BlockChiselledChitin;
import com.minelittlepony.unicopia.block.BlockChitin;
import com.minelittlepony.unicopia.block.BlockFruitLeaves;
import com.minelittlepony.unicopia.block.BlockGlowingGem;
import com.minelittlepony.unicopia.block.BlockGrowingCuccoon;
import com.minelittlepony.unicopia.block.BlockHiveWall;
import com.minelittlepony.unicopia.block.BlockSlimeLayer;
import com.minelittlepony.unicopia.block.BlockStick;
import com.minelittlepony.unicopia.block.BlockCloudAnvil;
import com.minelittlepony.unicopia.block.BlockCloudBanister;
import com.minelittlepony.unicopia.block.BlockCloudSlab;
import com.minelittlepony.unicopia.block.BlockCloudStairs;
import com.minelittlepony.unicopia.block.BlockDutchDoor;
import com.minelittlepony.unicopia.block.BlockSugar;
import com.minelittlepony.unicopia.block.BlockTomatoPlant;
import com.minelittlepony.unicopia.block.IColourful;
import com.minelittlepony.unicopia.block.UPot;
import com.minelittlepony.unicopia.block.USapling;
import com.minelittlepony.unicopia.item.ItemApple;
import com.minelittlepony.unicopia.block.BlockCloudDoor;
import com.minelittlepony.unicopia.block.BlockDiamondDoor;
import com.minelittlepony.unicopia.block.BlockCloudFarm;
import com.minelittlepony.unicopia.block.BlockCloudFence;
import com.minelittlepony.unicopia.block.BlockCloud;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@GameRegistry.ObjectHolder(Unicopia.MODID)
public class UBlocks {
    public static final BlockCloud cloud_block = Hooks.dummy();
    public static final BlockCloud enchanted_cloud_block = Hooks.dummy();
    public static final BlockCloud packed_cloud_block = Hooks.dummy();

    public static final BlockCloudStairs cloud_stairs = Hooks.dummy();

    public static final BlockCloudSlab.Single<?> cloud_slab = Hooks.dummy();
    public static final BlockCloudSlab.Single<?> enchanted_cloud_slab = Hooks.dummy();
    public static final BlockCloudSlab.Single<?> packed_cloud_slab = Hooks.dummy();

    public static final BlockCloudDoor mist_door = Hooks.dummy();
    public static final Block library_door = Hooks.dummy();
    public static final Block bakery_door = Hooks.dummy();
    public static final Block diamond_door = Hooks.dummy();

    public static final BlockGlowingGem enchanted_torch = Hooks.dummy();

    public static final BlockCloudAnvil anvil = Hooks.dummy();

    public static final BlockCloudFence cloud_fence = Hooks.dummy();
    public static final BlockCloudBanister cloud_banister = Hooks.dummy();

    public static final BlockAlfalfa alfalfa = Hooks.dummy();

    public static final BlockStick stick = Hooks.dummy();
    public static final BlockTomatoPlant tomato_plant = Hooks.dummy();

    public static final BlockCloudFarm cloud_farmland = Hooks.dummy();

    public static final BlockHiveWall hive = Hooks.dummy();
    public static final BlockChitin chitin_block = Hooks.dummy();
    public static final Block chissled_chitin = Hooks.dummy();

    public static final BlockGrowingCuccoon cuccoon = Hooks.dummy();
    public static final BlockSlimeLayer slime_layer = Hooks.dummy();

    public static final Block sugar_block = Hooks.dummy();
    public static final UPot flower_pot = Hooks.dummy();

    public static final USapling apple_sapling = Hooks.dummy();
    public static final Block apple_leaves = Hooks.dummy();

    @Mod.EventBusSubscriber(modid = Unicopia.MODID)
    public static class RegisterListener {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {

            BlockCloud cloud_block;
            BlockCloud enchanted_cloud_block;
            BlockCloud packed_cloud_block;
            USapling apple_sapling;

            event.getRegistry().registerAll(
                    cloud_block = new BlockCloud(UMaterials.cloud, CloudType.NORMAL, Unicopia.MODID, "cloud_block"),
                    enchanted_cloud_block=new BlockCloud(UMaterials.cloud, CloudType.ENCHANTED, Unicopia.MODID, "enchanted_cloud_block"),
                    packed_cloud_block=new BlockCloud(UMaterials.cloud, CloudType.PACKED, Unicopia.MODID, "packed_cloud_block"),
                    new BlockCloudStairs(cloud_block.getDefaultState(), Unicopia.MODID, "cloud_stairs"),
                    new BlockCloudSlab.Single<>(cloud_block, UMaterials.cloud, Unicopia.MODID, "cloud_slab"),
                    new BlockCloudSlab.Single<>(enchanted_cloud_block, UMaterials.cloud, Unicopia.MODID, "enchanted_cloud_slab"),
                    new BlockCloudSlab.Single<>(packed_cloud_block, UMaterials.cloud, Unicopia.MODID, "packed_cloud_slab"),
                    new BlockCloudDoor(UMaterials.cloud, Unicopia.MODID, "mist_door", () -> UItems.mist_door),
                    new BlockDutchDoor(Material.WOOD, Unicopia.MODID, "library_door", () -> UItems.library_door)
                            .setSoundType(SoundType.WOOD)
                            .setHardness(3),
                    new BlockDutchDoor(Material.WOOD, Unicopia.MODID, "bakery_door", () -> UItems.bakery_door)
                            .setSoundType(SoundType.WOOD)
                            .setHardness(3),
                    new BlockDiamondDoor(Unicopia.MODID, "diamond_door", () -> UItems.diamond_door),
                    new BlockGlowingGem(Unicopia.MODID, "enchanted_torch"),
                    new BlockCloudAnvil(Unicopia.MODID, "anvil"),
                    new BlockCloudFence(UMaterials.cloud, CloudType.NORMAL, Unicopia.MODID, "cloud_fence"),
                    new BlockCloudBanister(UMaterials.cloud, Unicopia.MODID, "cloud_banister"),
                    new BlockAlfalfa(Unicopia.MODID, "alfalfa"),
                    new BlockStick(Unicopia.MODID, "stick"),
                    new BlockTomatoPlant(Unicopia.MODID, "tomato_plant"),
                    new BlockCloudFarm(Unicopia.MODID, "cloud_farmland"),
                    new BlockHiveWall(Unicopia.MODID, "hive"),
                    new BlockChitin(Unicopia.MODID, "chitin_block"),
                    new BlockChiselledChitin(Unicopia.MODID, "chissled_chitin"),
                    new BlockGrowingCuccoon(Unicopia.MODID, "cuccoon"),
                    new BlockSlimeLayer(Unicopia.MODID, "slime_layer"),
                    new BlockSugar(Unicopia.MODID, "sugar_block"),
                    new UPot(Unicopia.MODID, "flower_pot"),

                    apple_sapling = new USapling(Unicopia.MODID, "apple_sapling")
                            .setTreeGen((w, s, m) -> new WorldGenTrees(true, 5, Blocks.LOG.getDefaultState(), UBlocks.apple_leaves.getDefaultState(), false)),
                    new BlockFruitLeaves(Unicopia.MODID, "apple_leaves", apple_sapling)
                            .setBaseGrowthChance(1200)
                            .setTint(0xFFEE81)
                            .setHarvestFruit(ItemApple::getRandomItemStack)
                            .setUnharvestFruit(w -> new ItemStack(UItems.rotten_apple))
            );
        }
    }

    @SideOnly(Side.CLIENT)
    static void registerColors(ItemColors items, BlockColors blocks) {
        items.registerItemColorHandler((stack, tint) -> {
            @SuppressWarnings("deprecation")
            IBlockState state = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());

            return blocks.colorMultiplier(state, null, null, tint);
        }, apple_leaves);
        blocks.registerBlockColorHandler((state, world, pos, tint) -> {
            Block block = state.getBlock();

            if (block instanceof IColourful) {
                return ((IColourful) block).getCustomTint(state, tint);
            }

            if (world != null && pos != null) {
                return BiomeColorHelper.getFoliageColorAtPos(world, pos);
            }

            return ColorizerFoliage.getFoliageColorBasic();
        }, apple_leaves);
    }

    public static class Shills {
        @Nullable
        public static Block getShill(Block blockIn) {
            if (blockIn == Blocks.FLOWER_POT) {
                return flower_pot;
            }

            return null;
        }
    }
}
