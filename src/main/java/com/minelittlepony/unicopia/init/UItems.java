package com.minelittlepony.unicopia.init;

import com.minelittlepony.unicopia.Unicopia;
import com.minelittlepony.unicopia.edibles.CookedToxicityDeterminent;
import com.minelittlepony.unicopia.edibles.MultiItemEdible;
import com.minelittlepony.unicopia.edibles.Toxicity;
import com.minelittlepony.unicopia.extern.Baubles;
import com.minelittlepony.unicopia.forgebullshit.OreReplacer;
import com.minelittlepony.unicopia.forgebullshit.UnFuckedItemSnow;
import com.minelittlepony.unicopia.item.*;
import com.minelittlepony.unicopia.item.override.ItemShear;
import com.minelittlepony.unicopia.item.override.ItemStick;
import com.minelittlepony.unicopia.spell.SpellRegistry;
import com.minelittlepony.unicopia.spell.SpellScorch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static com.minelittlepony.unicopia.Predicates.INTERACT_WITH_CLOUDS;
import static com.minelittlepony.unicopia.Predicates.MAGI;

@GameRegistry.ObjectHolder(Unicopia.MODID)
public class UItems {
//    public static final ItemApple apple_red = Hooks.dummy();
    public static final ItemApple apple_green = Hooks.dummy();
    public static final ItemApple apple_sweet = Hooks.dummy();
    public static final ItemApple apple_sour = Hooks.dummy();

    public static final ItemAppleMultiType zap_apple = Hooks.dummy();

    public static final ItemApple rotten_apple = Hooks.dummy();
    public static final ItemApple cooked_zap_apple = Hooks.dummy();

    public static final Item cloud_matter = Hooks.dummy();

    public static final Item dew_drop = Hooks.dummy();

    public static final ItemCloud cloud = Hooks.dummy();

    public static final Item cloud_block = Hooks.dummy();
    public static final Item enchanted_cloud_block = Hooks.dummy();
    public static final Item packed_cloud_block = Hooks.dummy();

    public static final Item cloud_stairs = Hooks.dummy();

    public static final Item cloud_farmland = Hooks.dummy();

    public static final Item cloud_fence = Hooks.dummy();
    public static final Item cloud_banister = Hooks.dummy();

    public static final Item anvil = Hooks.dummy();

    public static final Item record_crusade = Hooks.dummy();
    public static final Item record_pet = Hooks.dummy();
    public static final Item record_popular = Hooks.dummy();
    public static final Item record_funk = Hooks.dummy();

    public static final Item hive = Hooks.dummy();
    public static final Item chitin_shell = Hooks.dummy();
    public static final Item chitin_block = Hooks.dummy();
    public static final Item chissled_chitin = Hooks.dummy();
    public static final Item cuccoon = Hooks.dummy();
    public static final Item slime_layer = Hooks.dummy();

    public static final Item mist_door = Hooks.dummy();
    public static final Item library_door = Hooks.dummy();
    public static final Item bakery_door = Hooks.dummy();
    public static final Item diamond_door = Hooks.dummy();

    public static final Item sugar_block = Hooks.dummy();

    public static final Item cloud_slab = Hooks.dummy();
    public static final Item enchanted_cloud_slab = Hooks.dummy();
    public static final Item packed_cloud_slab = Hooks.dummy();

    public static final ItemSpell gem = Hooks.dummy();
    public static final ItemSpell corrupted_gem = Hooks.dummy();

    public static final ItemOfHolding bag_of_holding = Hooks.dummy();
    public static final ItemAlicornAmulet alicorn_amulet = Hooks.dummy();

    public static final ItemSpellbook spellbook = Hooks.dummy();
    public static final Item staff_meadow_brook = Hooks.dummy();
    public static final Item staff_remembrance = Hooks.dummy();

    public static final ItemMoss moss = Hooks.dummy();

    public static final Item alfalfa_seeds = Hooks.dummy();

    public static final Item enchanted_torch = Hooks.dummy();

    public static final Item alfalfa_leaves = Hooks.dummy();

    public static final Item cereal = Hooks.dummy();
    public static final Item sugar_cereal = Hooks.dummy();

    public static final ItemTomato tomato = Hooks.dummy();
    public static final ItemTomato cloudsdale_tomato = Hooks.dummy();
    public static final ItemTomatoSeeds tomato_seeds = Hooks.dummy();

    public static final Item apple_seeds = Hooks.dummy();
    public static final Item apple_leaves = Hooks.dummy();
//    public static final Item double_plant = Hooks.dummy();
//    public static final Item tall_grass = Hooks.dummy();
//    public static final Item yellow_flower = Hooks.dummy();
//    public static final Item red_flower = Hooks.dummy();

    public static final Item daffodil_daisy_sandwich = Hooks.dummy();
    public static final Item hay_burger = Hooks.dummy();
    public static final Item hay_fries = Hooks.dummy();
    public static final Item salad = Hooks.dummy();

    public static final Item wheat_worms = Hooks.dummy();

    public static final Item mug = Hooks.dummy();
    public static final Item apple_cider = Hooks.dummy();
    public static final Item juice = Hooks.dummy();
    public static final Item burned_juice = Hooks.dummy();

    @Mod.EventBusSubscriber(modid = Unicopia.MODID)
    public static class RegisterListener {

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
//                    new ItemApple(Unicopia.MODID, "apple_red"),
                    new ItemApple(Unicopia.MODID, "apple_green"),
                    new ItemApple(Unicopia.MODID, "apple_sweet"),
                    new ItemApple(Unicopia.MODID, "apple_sour"),
                    new ItemZapApple(Unicopia.MODID, "zap_apple")
                            .setSubTypes("zap_apple", "red", "green", "sweet", "sour", "zap"),
                    new ItemRottenApple(Unicopia.MODID, "rotten_apple"),
                    new ItemApple(Unicopia.MODID, "cooked_zap_apple"),
                    new Item()
                            .setCreativeTab(CreativeTabs.MATERIALS)
                            .setTranslationKey("cloud_matter")
                            .setRegistryName(Unicopia.MODID, "cloud_matter"),
                    new Item()
                            .setCreativeTab(CreativeTabs.MATERIALS)
                            .setTranslationKey("dew_drop")
                            .setRegistryName(Unicopia.MODID, "dew_drop"),
                    new ItemCloud(Unicopia.MODID, "cloud"),
                    new UItemBlock(UBlocks.cloud_block, INTERACT_WITH_CLOUDS),
                    new UItemBlock(UBlocks.enchanted_cloud_block, INTERACT_WITH_CLOUDS),
                    new UItemBlock(UBlocks.packed_cloud_block, INTERACT_WITH_CLOUDS),
                    new UItemBlock(UBlocks.cloud_stairs, INTERACT_WITH_CLOUDS),
                    new UItemBlock(UBlocks.cloud_farmland, INTERACT_WITH_CLOUDS),
                    new UItemBlock(UBlocks.cloud_fence, INTERACT_WITH_CLOUDS),
                    new UItemBlock(UBlocks.cloud_banister, INTERACT_WITH_CLOUDS),
                    new UItemBlock(UBlocks.anvil, INTERACT_WITH_CLOUDS).setTranslationKey("cloud_anvil"),
                    new URecord(Unicopia.MODID, "crusade", USounds.RECORD_CRUSADE),
                    new URecord(Unicopia.MODID, "pet", USounds.RECORD_PET),
                    new URecord(Unicopia.MODID, "popular", USounds.RECORD_POPULAR),
                    new URecord(Unicopia.MODID, "funk", USounds.RECORD_FUNK),
                    new ItemBlock(UBlocks.hive).setRegistryName(Unicopia.MODID, "hive"),
                    new Item()
                            .setCreativeTab(CreativeTabs.MATERIALS)
                            .setTranslationKey("chitin_shell")
                            .setRegistryName(Unicopia.MODID, "chitin_shell"),
                    new ItemBlock(UBlocks.chitin_block)
                            .setRegistryName(Unicopia.MODID, "chitin_block"),
                    new ItemBlock(UBlocks.chissled_chitin)
                            .setRegistryName(Unicopia.MODID, "chissled_chitin"),
                    new ItemBlock(UBlocks.cuccoon)
                            .setRegistryName(Unicopia.MODID, "cuccoon"),
                    new UnFuckedItemSnow(UBlocks.slime_layer)
                            .setRegistryName(Unicopia.MODID, "slime_layer"),
                    new ItemDoor(UBlocks.mist_door)
                            .setTranslationKey("mist_door")
                            .setRegistryName(Unicopia.MODID, "mist_door"),
                    new ItemDoor(UBlocks.library_door)
                            .setTranslationKey("library_door")
                            .setRegistryName(Unicopia.MODID, "library_door"),
                    new ItemDoor(UBlocks.bakery_door)
                            .setTranslationKey("bakery_door")
                            .setRegistryName(Unicopia.MODID, "bakery_door"),
                    new ItemDoor(UBlocks.diamond_door)
                            .setTranslationKey("diamond_door")
                            .setRegistryName(Unicopia.MODID, "diamond_door"),
                    new UItemDecoration(UBlocks.sugar_block),
                    new UItemSlab(UBlocks.cloud_slab, UBlocks.cloud_slab.doubleSlab, INTERACT_WITH_CLOUDS),
                    new UItemSlab(UBlocks.enchanted_cloud_slab, UBlocks.enchanted_cloud_slab.doubleSlab, INTERACT_WITH_CLOUDS),
                    new UItemSlab(UBlocks.packed_cloud_slab, UBlocks.packed_cloud_slab.doubleSlab, INTERACT_WITH_CLOUDS),
                    new ItemSpell(Unicopia.MODID, "gem"),
                    new ItemCurse(Unicopia.MODID, "corrupted_gem"),
                    new ItemOfHolding(Unicopia.MODID, "bag_of_holding"),
                    Baubles.isModActive()
                            ? Baubles.alicornAmulet() : new ItemAlicornAmulet(Unicopia.MODID, "alicorn_amulet"),
                    new ItemSpellbook(Unicopia.MODID, "spellbook"),
                    new ItemStaff(Unicopia.MODID, "staff_meadow_brook").setMaxDamage(2),
                    new ItemMagicStaff(Unicopia.MODID, "staff_remembrance", new SpellScorch()),
                    new ItemMoss(Unicopia.MODID, "moss"),
                    new ItemSeedFood(1, 4, UBlocks.alfalfa, Blocks.FARMLAND)
                            .setTranslationKey("alfalfa_seeds")
                            .setRegistryName(Unicopia.MODID, "alfalfa_seeds")
                            .setCreativeTab(CreativeTabs.MATERIALS),
                    new ItemBlock(UBlocks.enchanted_torch)
                            .setRegistryName(Unicopia.MODID, "enchanted_torch"),
                    new ItemFood(1, 3, false)
                            .setTranslationKey("alfalfa_leaves")
                            .setRegistryName(Unicopia.MODID, "alfalfa_leaves"),
                    new ItemCereal(Unicopia.MODID, "cereal", 9, 0.8F).setSugarAmount(1),
                    new ItemCereal(Unicopia.MODID, "sugar_cereal", 20, -2).setSugarAmount(110).setAlwaysEdible(),
                    new ItemTomato(Unicopia.MODID, "tomato", 4, 34),
                    new ItemTomato(Unicopia.MODID, "cloudsdale_tomato", 16, 4),
                    new ItemTomatoSeeds(Unicopia.MODID, "tomato_seeds"),
                    new UItemDecoration(UBlocks.apple_sapling, Unicopia.MODID, "apple_seeds"),
                    new ItemFruitLeaves(UBlocks.apple_leaves),
//                    new UItemFoodDelegate(Blocks.DOUBLE_PLANT, stack ->
//                            BlockDoublePlant.EnumPlantType.byMetadata(stack.getMetadata()).getTranslationKey()
//                    ).setFoodDelegate(new MultiItemEdible(new BushToxicityDeterminent()))
//                            .setTranslationKey("doublePlant"),
//                    new UItemFoodDelegate(Blocks.TALLGRASS, stack -> {
//                        switch (stack.getMetadata()) {
//                            case 0:
//                                return "shrub";
//                            case 1:
//                                return "grass";
//                            case 2:
//                                return "fern";
//                            default:
//                                return "";
//                        }
//                    }).setFoodDelegate(new MultiItemEdible(stack -> {
//                        switch (stack.getMetadata()) {
//                            default:
//                            case 0:
//                                return Toxicity.SAFE;
//                            case 1:
//                                return Toxicity.SAFE;
//                            case 2:
//                                return Toxicity.SEVERE;
//                        }
//                    })),
//                    new UItemFoodDelegate(Blocks.YELLOW_FLOWER, stack ->
//                            BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.YELLOW, stack.getMetadata()).getTranslationKey()
//                    ).setFoodDelegate(new MultiItemEdible(new FlowerToxicityDeterminent(BlockFlower.EnumFlowerColor.YELLOW)))
//                            .setTranslationKey("flower"),
//                    new UItemFoodDelegate(Blocks.RED_FLOWER, stack ->
//                            BlockFlower.EnumFlowerType.getType(BlockFlower.EnumFlowerColor.RED, stack.getMetadata()).getTranslationKey()
//                    ).setFoodDelegate(new MultiItemEdible(new FlowerToxicityDeterminent(BlockFlower.EnumFlowerColor.RED)))
//                            .setTranslationKey("rose"),
                    new MultiItemEdible(Unicopia.MODID, "daffodil_daisy_sandwich", 3, 2, CookedToxicityDeterminent.instance)
                            .setHasSubtypes(true),
                    new MultiItemEdible(Unicopia.MODID, "hay_burger", 3, 4, CookedToxicityDeterminent.instance)
                            .setHasSubtypes(true),
                    new MultiItemEdible(Unicopia.MODID, "hay_fries", 1, 5, stack -> Toxicity.SAFE),
                    new MultiItemEdible(Unicopia.MODID, "salad", 4, 2, CookedToxicityDeterminent.instance)
                            .setHasSubtypes(true)
                            .setContainerItem(Items.BOWL),
                    new MultiItemEdible(Unicopia.MODID, "wheat_worms", 1, 0, stack -> Toxicity.SEVERE),
                    new Item()
                            .setTranslationKey("mug")
                            .setRegistryName(Unicopia.MODID, "mug")
                            .setCreativeTab(CreativeTabs.MATERIALS)
                            .setFull3D(),
                    new MultiItemEdible(Unicopia.MODID, "apple_cider", 4, 2, stack -> Toxicity.MILD)
                            .setUseAction(EnumAction.DRINK)
                            .setContainerItem(mug)
                            .setFull3D(),
                    new MultiItemEdible(Unicopia.MODID, "juice", 2, 2, stack -> Toxicity.SAFE)
                            .setUseAction(EnumAction.DRINK)
                            .setContainerItem(Items.GLASS_BOTTLE),
                    new MultiItemEdible(Unicopia.MODID, "burned_juice", 3, 1, stack -> Toxicity.FAIR)
                            .setUseAction(EnumAction.DRINK)
                            .setContainerItem(Items.GLASS_BOTTLE)
            );
        }

    }

    public static void registerFuels() {
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(zap_apple), new ItemStack(cooked_zap_apple), 0.1F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(juice), new ItemStack(burned_juice), 0);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(cuccoon), new ItemStack(chitin_shell), 0.3F);
    }

    @SideOnly(Side.CLIENT)
    static void registerColors(ItemColors registry) {
        registry.registerItemColorHandler((stack, tint) -> {
            if (MAGI.test(Minecraft.getMinecraft().player)) {
                return SpellRegistry.instance().getSpellTintFromStack(stack);
            }

            return 0xffffff;
        }, gem, corrupted_gem);
    }

    public static class Shills {
        private static final ItemStick stick = new ItemStick();
        private static final ItemShear shears = new ItemShear();

        @Nullable
        public static Item getShill(Item itemIn) {
            if (itemIn == Items.SHEARS) {
                return shears;
            }

            if (itemIn == Items.STICK) {
                return stick;
            }

            return null;
        }
    }
}
