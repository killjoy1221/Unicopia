package com.minelittlepony.unicopia.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class UItemDecoration extends ItemBlock {

    public UItemDecoration(Block block) {
        this(block, block.getRegistryName());
    }

    public UItemDecoration(Block block, String domain, String name) {
        this(block, new ResourceLocation(domain, name));
    }

    public UItemDecoration(Block block, ResourceLocation res) {
        super(block);

        setTranslationKey(res.getPath());
        setRegistryName(res);
        setCreativeTab(block.getCreativeTab());
    }
}
