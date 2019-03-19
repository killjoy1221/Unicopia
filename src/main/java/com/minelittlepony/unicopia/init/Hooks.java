package com.minelittlepony.unicopia.init;

import com.minelittlepony.unicopia.UClient;
import com.minelittlepony.unicopia.Unicopia;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

@EventBusSubscriber(modid = Unicopia.MODID)
class Hooks {

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    static <T extends E, E extends IForgeRegistryEntry<E>> T dummy() {
        return null;
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        UEffects.init(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        USounds.init(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        UEntities.init(event.getRegistry());
    }
}
