package com.sollace.unicopia;

import java.io.File;
import java.util.LinkedHashMap;

import org.lwjgl.input.Keyboard;

import com.blazeloader.api.achievement.ApiAchievement;
import com.blazeloader.api.block.ApiBlock;
import com.blazeloader.api.client.ApiClient;
import com.blazeloader.api.client.MCColor;
import com.blazeloader.api.client.ModStateMap;
import com.blazeloader.api.client.particles.ApiParticlesClient;
import com.blazeloader.api.client.render.ApiRenderBlock;
import com.blazeloader.api.client.render.ApiRenderItem;
import com.blazeloader.api.client.render.ApiRenderPlayer;
import com.blazeloader.api.entity.ApiEntity;
import com.blazeloader.api.item.ApiItem;
import com.blazeloader.api.particles.ApiParticles;
import com.blazeloader.api.particles.IParticle;
import com.blazeloader.api.recipe.ApiCrafting;
import com.blazeloader.api.recipe.ApiRecipe;
import com.blazeloader.api.recipe.ICraftingManager;
import com.blazeloader.util.version.Versions;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.sollace.unicopia.block.BlockCloud;
import com.sollace.unicopia.block.BlockCloudSlab;
import com.sollace.unicopia.block.BlockCloudStairs;
import com.sollace.unicopia.client.ClientSide;
import com.sollace.unicopia.client.particle.EntityMagicFX;
import com.sollace.unicopia.client.particle.EntityRaindropFX;
import com.sollace.unicopia.client.render.RenderChangeling;
import com.sollace.unicopia.command.CommandDecloud;
import com.sollace.unicopia.command.CommandOverrideGameMode;
import com.sollace.unicopia.command.CommandSpecies;
import com.sollace.unicopia.effect.SpellAttractor;
import com.sollace.unicopia.effect.SpellFire;
import com.sollace.unicopia.effect.SpellIce;
import com.sollace.unicopia.effect.SpellInferno;
import com.sollace.unicopia.effect.SpellList;
import com.sollace.unicopia.effect.SpellMinion;
import com.sollace.unicopia.effect.SpellPortal;
import com.sollace.unicopia.effect.SpellShield;
import com.sollace.unicopia.enchanting.BasicCraftingEvent;
import com.sollace.unicopia.enchanting.MultiPageUnlockEvent;
import com.sollace.unicopia.enchanting.PagesList;
import com.sollace.unicopia.enchanting.SpellRecipe;
import com.sollace.unicopia.entity.EntityCloud;
import com.sollace.unicopia.entity.EntityCloudNatural;
import com.sollace.unicopia.entity.EntitySpell;
import com.sollace.unicopia.entity.EntitySpellbook;
import com.sollace.unicopia.item.ItemApple;
import com.sollace.unicopia.item.ItemCloud;
import com.sollace.unicopia.item.ItemSpell;
import com.sollace.unicopia.item.ItemSpellbook;
import com.sollace.unicopia.item.crafting.RecipeEnchanting;
import com.sollace.unicopia.power.PowerStomp;
import com.sollace.unicopia.power.PowerDisguise;
import com.sollace.unicopia.power.PowerFeed;
import com.sollace.unicopia.power.PowerGrow;
import com.sollace.unicopia.power.PowerMagic;
import com.sollace.unicopia.power.PowerRain;
import com.sollace.unicopia.power.PowerTeleport;
import com.sollace.unicopia.power.PowerThunder;
import com.sollace.util.BlockMaterial;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.command.CommandHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.biome.BiomeGenEnd;
import net.minecraft.world.biome.BiomeGenHell;

public class Unicopia {
	public static final String MODID = "unicopia";
	public static final String VERSION = "1.2.4b";
	public static final String NAME = "Unicopia";
	public static final String CHANNEL = "UNICOPIA";
	
	private static final int BRUSHES_ROYALBLUE = 4286945;
	private static final int BRUSHES_CHARTREUSE = 8388352;
	
	private static final ICraftingManager craftingManager = ApiCrafting.createCraftingManager();
	
	public static ICraftingManager getCraftingManager() {
		return craftingManager;
	}
	
	public static boolean isClient() {
		return Versions.isClient();
	}
	
	public void preInit(File configPath) {
		Settings.init(configPath);
		registerPowers();
		registerSpells();
		registerPages();
	}
	
	public void postInit() {
		if (!Settings.getLegacyMode()) {
			BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();
			SpawnListEntry cloudSpawn = new SpawnListEntry(EntityCloudNatural.class, 30, 2, 5);
			for (BiomeGenBase i : biomes) {
				if (i != null && !(i instanceof BiomeGenHell) && !(i instanceof BiomeGenEnd)) {
					i.getSpawnableList(EnumCreatureType.CREATURE).add(cloudSpawn);
				}
			}
		}
	}
	
	public void init() {
		Particles.unicorn = ApiParticles.registerParticle("unicorn", false, 0);
		Particles.rain = ApiParticles.registerParticle("rain", false, 0);
		
		if (isClient()) {
			ApiParticlesClient.registerParticleFactory(Particles.unicorn, new EntityMagicFX.Factory());
			ApiParticlesClient.registerParticleFactory(Particles.rain, new EntityRaindropFX.Factory());
			
			PlayerExtension.skinType = ApiRenderPlayer.setPlayerRenderer("disguised", new RenderChangeling(ApiClient.getRenderManager()));
		}
		
		if (!Settings.getLegacyMode()) {
			int entityId = Settings.getEntityId("cloud");
			
			ApiEntity.registerEntityType(EntityCloud.class, "cloud", entityId);
			ApiEntity.registerEntityEggInfo(entityId, new EntityList.EntityEggInfo(entityId, BRUSHES_ROYALBLUE, BRUSHES_CHARTREUSE));
			ApiEntity.registerEntityType(EntityCloudNatural.class, "cloud_natural", Settings.getEntityId("cloud2"));
			ApiEntity.registerEntityType(EntitySpell.class, "spell", Settings.getEntityId("spell"));
			ApiEntity.registerEntityType(EntitySpellbook.class, "spell_book", Settings.getEntityId("spellbook"));
			
			registerItemsAndBlocks();
			registerRecipes();
			registerEnchantments();
			
			if (isClient()) {
				ClientSide.RegisterRenderers();
			}
		}
	}
	
	private void registerItemsAndBlocks() {
		UItems.cloudMatter = ApiItem.registerItem(Settings.getItemId("cloudMatter"), MODID, "cloud_matter", (new Item()).setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("cloud_matter"));
		UItems.cloud = ApiItem.registerItem(Settings.getItemId("cloud"), MODID, "cloud", new ItemCloud("cloud"));
		ApiItem.registerItemVariantNames(UItems.cloud, MODID + ":cloud_small", MODID + ":cloud_med", MODID + ":cloud_large");
		
		Items.apple = UItems.apple = ApiItem.registerItem(260, new ResourceLocation("apple"), new ItemApple().setSubTypes("apple", "green", "sweet", "rotten", "zap", "zap_cooked").setTypeRarities(10, 20, 10, 30));
		ApiItem.registerItemVariantNames(Items.apple, UItems.apple.getVariants());
		
		UItems.spell = ApiItem.registerItem(Settings.getItemId("spell"), MODID, "spell", new ItemSpell("spell"));
		UItems.spellBook = ApiItem.registerItem(Settings.getItemId("spellBook"), MODID, "spellbook", new ItemSpellbook("spellbook"));
		
		int id = Settings.getBlockId("cloudBlock");
		UBlocks.cloud = ApiBlock.registerBlock(id, MODID, "cloudBlock", new BlockCloud(Materials.cloud, "cloudBlock"));
		ApiItem.registerItemBlock(UBlocks.cloud, new ItemMultiTexture(UBlocks.cloud, UBlocks.cloud, new Function<ItemStack, String>() {
            public String apply(ItemStack stack) {
                return BlockCloud.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName();
            }
        }));
		ApiBlock.registerBlockVarientNames(UBlocks.cloud, MODID + ":normal_cloud", MODID + ":packed_cloud", MODID + ":enchanted_cloud");
		
		UBlocks.stairsCloud = ApiBlock.registerBlock(Settings.getBlockId("cloudStairs"), MODID, "cloud_stairs", new BlockCloudStairs(UBlocks.cloud.getDefaultState(), "stairsCloud"));
		ApiItem.registerItemBlock(UBlocks.stairsCloud);
		
		UBlocks.double_cloud_slab = ApiBlock.registerBlock(Settings.getBlockId("doubleCloudSlab"), MODID, "double_cloud_slab", new BlockCloudSlab(true, Materials.cloud, "cloudSlab"));
		
		id = Settings.getBlockId("cloudSlab");
		UBlocks.cloud_slab = ApiBlock.registerBlock(id, MODID, "cloud_slab", new BlockCloudSlab(false, Materials.cloud, "cloudSlab"));
		ApiItem.registerItemBlock(UBlocks.cloud_slab, (new ItemSlab(UBlocks.cloud_slab, UBlocks.cloud_slab, UBlocks.double_cloud_slab)).setUnlocalizedName("cloudSlab"));
		ApiBlock.registerBlockVarientNames(UBlocks.cloud_slab, MODID + ":normal_cloud_slab", MODID + ":packed_cloud_slab", MODID + ":enchanted_cloud_slab");
		
		if (isClient()) {
			ApiRenderItem.registerItem(UItems.cloudMatter, MODID + ":cloud_matter");
			ApiRenderItem.registerItem(UItems.cloud, ItemCloud.CloudSize.SMALL.getMetadata(), MODID + ":cloud_small");
			ApiRenderItem.registerItem(UItems.cloud, ItemCloud.CloudSize.MEDIUM.getMetadata(), MODID + ":cloud_med");
			ApiRenderItem.registerItem(UItems.cloud, ItemCloud.CloudSize.LARGE.getMetadata(), MODID + ":cloud_large");
			
			String[] appleVariants = ((ItemApple)Items.apple).getVariants();
			for (int i = 1; i < appleVariants.length; i++) {
				ApiRenderItem.registerItem(Items.apple, i, appleVariants[i]);
			}
			
			ApiRenderItem.registerItem(UItems.spell, MODID + ":spell");
			ApiRenderItem.registerItem(UItems.spell, new ItemMeshDefinition() {
				public ModelResourceLocation getModelLocation(ItemStack stack) {
					return new ModelResourceLocation(MODID + ":spell", "inventory");
				}
			});
			
			ApiRenderItem.registerItem(UItems.spellBook, MODID + ":spellbook");
			
			ApiRenderBlock.registerBlockModelMapper(UBlocks.cloud, (new ModStateMap.Builder()).setModId(MODID).setProperty(BlockCloud.VARIANT).setSuffix("_cloud").build());
			ApiRenderBlock.registerBlock(UBlocks.cloud, BlockCloud.EnumType.NORMAL.getMetadata(), MODID + ":normal_cloud");
			ApiRenderBlock.registerBlock(UBlocks.cloud, BlockCloud.EnumType.PACKED.getMetadata(), MODID + ":packed_cloud");
			ApiRenderBlock.registerBlock(UBlocks.cloud, BlockCloud.EnumType.ENCHANTED.getMetadata(), MODID + ":enchanted_cloud");
			
			ApiRenderBlock.registerBlock(UBlocks.stairsCloud, MODID + ":cloud_stairs");
			
			ApiRenderBlock.registerBlockModelMapper(UBlocks.cloud_slab, (new ModStateMap.Builder()).setModId(MODID).setProperty(BlockCloud.VARIANT).setSuffix("_cloud_slab").build());
			ApiRenderBlock.registerBlock(UBlocks.cloud_slab, BlockCloud.EnumType.NORMAL.getMetadata(), MODID + ":normal_cloud_slab");
			ApiRenderBlock.registerBlock(UBlocks.cloud_slab, BlockCloud.EnumType.PACKED.getMetadata(), MODID + ":packed_cloud_slab");
			ApiRenderBlock.registerBlock(UBlocks.cloud_slab, BlockCloud.EnumType.ENCHANTED.getMetadata(), MODID + ":enchanted_cloud_slab");
			
			ApiRenderBlock.registerBlockModelMapper(UBlocks.double_cloud_slab, new StateMapperBase() {
	            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
	                LinkedHashMap states = Maps.newLinkedHashMap(state.getProperties());
	                String variant = BlockCloudSlab.VARIANT.getName((Comparable)states.remove(BlockCloudSlab.VARIANT));
	                return new ModelResourceLocation(MODID + ":" + variant + "_cloud_double_slab", "normal");
	            }
			});
		}
	}
	
	private void registerRecipes() {
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(UItems.cloud, 1, 0), "*",'*',UItems.cloudMatter);
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(UItems.cloud, 1, 1), "**","##",'*',UItems.cloudMatter,'#',new ItemStack(UItems.cloud,1,0));
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(UItems.cloud, 1, 2), "***","*#*",'*',UItems.cloudMatter,'#',new ItemStack(UItems.cloud,1,1));
		
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(UBlocks.cloud, 1, 0), "**","**", '*', UItems.cloudMatter);
		ApiRecipe.addShapelessCraftingRecipe(new ItemStack(UItems.cloudMatter, 4), new ItemStack(UBlocks.cloud, 1, 0));
		
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(UBlocks.cloud, 1, 1), "**", "**", '*', new ItemStack(UBlocks.cloud, 1, 0));
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(UBlocks.cloud, 4, 0), "*", '*', new ItemStack(UBlocks.cloud, 1, 1));
		
		addRecipeForEnchantedBook(new ItemStack(UBlocks.cloud, 1, 2), Enchantment.featherFalling, null, new ItemStack(UBlocks.cloud, 1, 1), new ItemStack(UItems.spell, 4));
		ApiRecipe.addShapelessCraftingRecipe(new ItemStack(UItems.spellBook), new ItemStack(UItems.spell, 1, 0), new ItemStack(Items.book, 1, 0));
		
		ApiCrafting.getVanillaCraftingManager().addRecipe(new RecipeEnchanting(new ItemStack(UBlocks.cloud, 1, 2), new ItemStack(UBlocks.cloud, 1, 1), Enchantment.featherFalling));
		ApiCrafting.getVanillaCraftingManager().addRecipe(new RecipeEnchanting(new ItemStack(UBlocks.cloud_slab, 1, 2), new ItemStack(UBlocks.cloud_slab, 1, 1), Enchantment.featherFalling));
		
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(UBlocks.cloud_slab, 1, 0), "***", '*', new ItemStack(UBlocks.cloud, 1, 0));
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(UBlocks.cloud_slab, 1, 1), "***", '*', new ItemStack(UBlocks.cloud, 1, 1));
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(UBlocks.cloud_slab, 1, 2),  "***", '*', new ItemStack(UBlocks.cloud, 1, 2));
		
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(UBlocks.stairsCloud, 0, 3), "  *", " **", "***", '*', UBlocks.cloud);
		
		ApiRecipe.addShapedCraftingRecipe(new ItemStack(UItems.spell, 2), " # ", "#*#", " # ", '#', new ItemStack(net.minecraft.init.Blocks.stone, 1, 3), '*', net.minecraft.init.Items.coal);
		
		ApiRecipe.addSmeltingRecipe(new ItemStack(Items.apple, 1, 3), new ItemStack(Items.apple, 1, 4), 0.1f);
		
		int zap = UItems.apple.getZapAppleMetadata();
		ApiRecipe.addShapelessCraftingRecipe(new ItemStack(Items.apple, 1, 6), new ItemStack(Items.apple, 1, zap), new ItemStack(Items.dye, 1, 1));
		ApiRecipe.addShapelessCraftingRecipe(new ItemStack(Items.apple, 1, 7), new ItemStack(Items.apple, 1, zap), new ItemStack(Items.dye, 1, 2));
		ApiRecipe.addShapelessCraftingRecipe(new ItemStack(Items.apple, 1, 8), new ItemStack(Items.apple, 1, zap), new ItemStack(Items.dye, 1, 14));
		ApiRecipe.addShapelessCraftingRecipe(new ItemStack(Items.apple, 1, 9), new ItemStack(Items.apple, 1, zap), Items.rotten_flesh);
	}
	
	private void addRecipeForEnchantedBook(ItemStack result, Enchantment enchant, ItemStack... args) {
		for (int i = enchant.getMinLevel(); i < enchant.getMaxLevel(); i++) {
			args[0] = new ItemStack(net.minecraft.init.Items.enchanted_book, 1);
			args[0].addEnchantment(Enchantment.featherFalling, i);
			ApiRecipe.addShapelessCraftingRecipe(result, (Object[])args);
		}
	}
	
	private void registerEnchantments() {
		craftingManager.addRecipe(new SpellRecipe("shield", Items.egg, Items.coal, Items.blaze_powder));
		craftingManager.addRecipe(new SpellRecipe("fire", Items.fire_charge, Items.fire_charge, Items.lava_bucket));
		craftingManager.addRecipe(new SpellRecipe("ice", Items.snowball, Items.snowball, Items.water_bucket));
		craftingManager.addRecipe(new SpellRecipe("inferno", Items.lava_bucket, new ItemStack(UItems.spell, 1, SpellList.getId("fire")), Items.blaze_rod));
		craftingManager.addRecipe(new SpellRecipe("portal", Items.ender_pearl, Items.ghast_tear, Items.redstone));
		craftingManager.addRecipe(new SpellRecipe("attract", new ItemStack(UItems.spell, 1, SpellList.getId("shield")), Items.rabbit_foot, Blocks.sponge));
		craftingManager.addRecipe(new SpellRecipe("minion", new ItemStack(UItems.spell), Items.egg, Items.water_bucket, Items.bone, Items.redstone));
	}
	
	private void registerPowers() {
		new PowerTeleport("unicopia.power.teleport", Keyboard.KEY_O); //UNICORN | ALICORN
		new PowerMagic("unicopia.power.magic", Keyboard.KEY_P); //UNICORN | ALICORN
		
		new PowerRain("unicopia.power.rain", Keyboard.KEY_K); //PEGASUS | ALICORN
		new PowerThunder("unicopia.power.thunder", Keyboard.KEY_L); //PEGASUS | ALICORN
		
		new PowerGrow("unicopia.power.grow", Keyboard.KEY_N); //EARTH
		new PowerStomp("unicopia.power.earth", Keyboard.KEY_M); //EARH | ALICORN
		
		//new PowerCloudBase("unicopia.power.cloud", Keyboard.KEY_P);
		new PowerFeed("unicopia.power.feed", Keyboard.KEY_N); //CHANGELING
		new PowerDisguise("unicopia.power.disguise", Keyboard.KEY_M); //CHANGELING
	}
	
	private void registerSpells() {
		SpellList.registerSpell("shield", SpellShield.class, MCColor.colorInteger(1f,1f,0));
		SpellList.registerSpell("fire", SpellFire.class, MCColor.BRUSHES_RED.value()); //MCColor.colorInteger(1,0.6f,0.6f)
		SpellList.registerSpell("ice", SpellIce.class, MCColor.BRUSHES_LIGHTBLUE.value()); //MCColor.colorInteger(0.6f,0.6f,1)
		SpellList.registerSpell("inferno", SpellInferno.class, MCColor.BRUSHES_DARKORANGE.value()); //MCColor.colorInteger(1,0.9f,0.6f)
		SpellList.registerSpell("portal", SpellPortal.class, MCColor.colorInteger(0.3f,0.6f,0.3f));
		SpellList.registerSpell("attract", SpellAttractor.class, MCColor.BRUSHES_DARKGREEN.value());
		SpellList.registerSpell("minion", SpellMinion.class, MCColor.BRUSHES_ANTIQUEWHITE.value());
	}
	
	private void registerPages() {
		Achievements.pageUnlocked = ApiAchievement.registerAchievement("unicopia.page.unlock", 0, 0, new ItemStack(Items.paper));
		PagesList.setTotalPages(10);
		PagesList.registerPageEvent(new BasicCraftingEvent(2, "ice"));
		PagesList.registerPageEvent(new BasicCraftingEvent(2, "fire"));
		PagesList.registerPageEvent(new BasicCraftingEvent(3, "shield"));
		PagesList.registerPageEvent(new BasicCraftingEvent(4, "portal"));
		PagesList.registerPageEvent(new MultiPageUnlockEvent(5, new int[] {2}, new int[] {3,4,6}));
		PagesList.registerPageEvent(new BasicCraftingEvent(6, "inferno"));
	}
	
	public void registerCommands(CommandHandler handler) {
		handler.registerCommand(new CommandOverrideGameMode());
		handler.registerCommand(new CommandSpecies());
		handler.registerCommand(new CommandDecloud());
		if (!isClient()) {
			MinecraftServer.getServer().setAllowFlight(true);
		}
	}
	
	public static class UItems {
		public static Item cloud;
		public static Item cloudMatter;
		public static Item spell;
		public static Item spellBook;
		public static ItemApple apple;
	}
	
	public static class UBlocks {
		public static Block cloud;
		public static Block stairsCloud;
		public static BlockCloudSlab double_cloud_slab;
		public static BlockCloudSlab cloud_slab;
	}
	
	public static class Particles {
		public static IParticle unicorn;
		public static IParticle rain;
	}
	
	public static class Materials {
		public static final Material cloud = (new BlockMaterial(MapColor.snowColor));
	}
	
	public static class Achievements {
		public static Achievement pageUnlocked;
	}
}
