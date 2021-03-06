package com.minelittlepony.unicopia.item;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.minelittlepony.unicopia.Unicopia;
import com.minelittlepony.unicopia.edibles.IEdible;
import com.minelittlepony.unicopia.edibles.Toxicity;
import com.minelittlepony.unicopia.init.UItems;
import com.minelittlepony.util.collection.Pool;
import com.minelittlepony.util.collection.Weighted;

import net.minecraft.block.BlockPlanks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemApple extends ItemFood implements IEdible {

    public static final Pool<Object, Weighted<Supplier<ItemStack>>> typeVariantMap = Pool.of(BlockPlanks.EnumType.OAK,
            BlockPlanks.EnumType.OAK, new Weighted<Supplier<ItemStack>>()
                    .put(1, () -> new ItemStack(UItems.rotten_apple))
                    .put(2, () -> new ItemStack(UItems.apple_green))
                    .put(3, () -> new ItemStack(Items.APPLE)),
            BlockPlanks.EnumType.SPRUCE, new Weighted<Supplier<ItemStack>>()
                    .put(1, () -> new ItemStack(UItems.apple_sour))
                    .put(2, () -> new ItemStack(UItems.apple_green))
                    .put(3, () -> new ItemStack(UItems.apple_sweet))
                    .put(4, () -> new ItemStack(UItems.rotten_apple)),
            BlockPlanks.EnumType.BIRCH, new Weighted<Supplier<ItemStack>>()
                    .put(1, () -> new ItemStack(UItems.rotten_apple))
                    .put(2, () -> new ItemStack(UItems.apple_sweet))
                    .put(5, () -> new ItemStack(UItems.apple_green)),
            BlockPlanks.EnumType.JUNGLE, new Weighted<Supplier<ItemStack>>()
                    .put(5, () -> new ItemStack(UItems.apple_green))
                    .put(2, () -> new ItemStack(UItems.apple_sweet))
                    .put(1, () -> new ItemStack(UItems.apple_sour)),
            BlockPlanks.EnumType.ACACIA, new Weighted<Supplier<ItemStack>>()
                    .put(1, () -> new ItemStack(UItems.rotten_apple))
                    .put(2, () -> new ItemStack(UItems.apple_sweet))
                    .put(5, () -> new ItemStack(UItems.apple_green)),
            BlockPlanks.EnumType.DARK_OAK, new Weighted<Supplier<ItemStack>>()
                    .put(1, () -> new ItemStack(UItems.rotten_apple))
                    .put(2, () -> new ItemStack(UItems.apple_sweet))
                    .put(5, () -> new ItemStack(UItems.zap_apple)
                    )
    );

    public static ItemStack getRandomItemStack(Object variant) {
        return typeVariantMap.getOptional(variant)
                .flatMap(Weighted::get)
                .map(Supplier::get)
                .orElse(ItemStack.EMPTY);
    }

    public ItemApple(String domain, String name) {
        super(4, 3, false);

        setTranslationKey(name);

        setRegistryName(domain, name);
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(getToxicityLevel(stack).getTooltip());
    }

    @Override
    public Toxicity getToxicityLevel(ItemStack stack) {
        return Toxicity.SAFE;
    }

    @Mod.EventBusSubscriber(modid = Unicopia.MODID)
    public static class AppleEvents {

        @SubscribeEvent
        public static void onAddInfo(ItemTooltipEvent event) {
            if (event.getItemStack().getItem() == Items.APPLE) {
                event.getToolTip().add(Toxicity.SAFE.getTooltip());
            }
        }

        @SubscribeEvent
        public static void onItemExpire(ItemExpireEvent event) {
            EntityItem item = event.getEntityItem();
            Item apple = item.getItem().getItem();
            if (apple == Items.APPLE || apple instanceof ItemApple && apple != UItems.rotten_apple) {
                if (!item.world.isRemote) {

                    EntityItem neu = new EntityItem(item.world);
                    neu.copyLocationAndAnglesFrom(item);
                    neu.setItem(new ItemStack(UItems.rotten_apple));

                    item.world.spawnEntity(neu);

                    item.getItem().shrink(1);

                    event.setExtraLife(300);
                    event.setCanceled(true);

                } else {
                    float bob = MathHelper.sin(((float) item.getAge() + 1) / 10F + item.hoverStart) * 0.1F + 0.1F;

                    for (int i = 0; i < 3; i++) {
                        item.world.spawnParticle(EnumParticleTypes.SPELL_MOB, item.posX, item.posY + bob, item.posZ,
                                item.world.rand.nextGaussian() - 0.5F,
                                item.world.rand.nextGaussian() - 0.5F,
                                item.world.rand.nextGaussian() - 0.5F);
                    }
                }
            }
        }
    }
}
