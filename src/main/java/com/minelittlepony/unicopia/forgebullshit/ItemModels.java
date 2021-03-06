package com.minelittlepony.unicopia.forgebullshit;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ItemModels {

    /**
     * Registers models and item variants for all of the provided textures.
     */
    public static void registerAll(Item...items) {
        for (Item i : items) {
            if (i instanceof IMultiItem && i.getHasSubtypes()) {
                IMultiItem multi = (IMultiItem)i;

                if (multi.variantsAreHidden()) {
                    registerAll(i, multi.getMaxMetadata(), new ModelResourceLocation(i.getRegistryName().toString()));
                } else {
                    registerAllVariants(i, multi.getVariants());
                }
            } else {
                registerAll(i, 1, new ModelResourceLocation(i.getRegistryName().toString()));
            }
        }
    }

    private static void registerAll(Item item, int maxMeta, ModelResourceLocation resource) {
        for (int i = 0; i < maxMeta; i++) {
            ModelLoader.setCustomModelResourceLocation(item, i, resource);
        }
    }

    /**
     * Registers a model for the given item and all associated variants.
     */
    public static void registerAllVariants(Item item, String... variants) {
        registerAllVariants(item, item.getRegistryName().getNamespace(), variants);
    }

    public static void registerAllVariants(Item item, String domain, String... variants) {
        for (int i = 0; i < variants.length; i++) {
            ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(domain + ":" + variants[i]));
        }
    }
}
