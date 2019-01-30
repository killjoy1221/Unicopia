package com.minelittlepony.unicopia.edibles;

import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public enum Toxicity {
    SAFE(0, 0),
    FAIR(1, 30),
    SEVERE(5, 160),
    LETHAL(10, 900);

    private final int level;
    private final int duration;

    private static final Toxicity[] values = values();

    Toxicity(int level, int duration) {
        this.level = level;
        this.duration = duration;
    }

    public boolean toxicWhenRaw() {
        return isLethal() || this != SAFE;
    }

    public boolean toxicWhenCooked() {
        return isLethal() || this == SEVERE;
    }

    public boolean isLethal() {
        return this == LETHAL;
    }

    public PotionEffect getPoisonEffect() {
        return new PotionEffect(MobEffects.POISON, duration, level);
    }

    public String getTranslationKey() {
        return String.format("toxicity.%s.name", name().toLowerCase());
    }

    public static Toxicity byMetadata(int metadata) {
        return values[metadata % values.length];
    }
}