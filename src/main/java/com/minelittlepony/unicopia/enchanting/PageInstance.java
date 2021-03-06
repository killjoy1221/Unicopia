package com.minelittlepony.unicopia.enchanting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import net.minecraft.util.ResourceLocation;

class PageInstance implements IPage {

    int index;

    @Nullable
    ResourceLocation parent;

    @Nonnull
    ResourceLocation name;

    @Nonnull
    ResourceLocation texture;

    @Nullable
    IUnlockCondition<IUnlockEvent> condition;

    @Expose
    PageState state = PageState.LOCKED;

    PageInstance(ResourceLocation id, JsonObject json) {
        this.name = id;

        if (json.has("parent")) {
            parent = new ResourceLocation(json.get("parent").getAsString());
        }

        if (json.has("state")) {
            state = PageState.of(json.get("state").getAsString());
        }

        if (json.has("condition")) {
            condition = Pages.instance().createCondition(json.get("condition").getAsJsonObject());
        }

        String full = json.get("texture").getAsString();
        String[] loc = full.split(":");
        if (loc.length < 2) {
            loc = new String[] { "minecraft", full };
        }

        if ("minecraft".equals(loc[0]) && !"minecraft".equals(id.getNamespace())) {
            loc[0] = id.getNamespace();
        }

        texture = new ResourceLocation(loc[0], String.format("textures/pages/%s.png", loc[1]));
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public ResourceLocation getName() {
        return name;
    }

    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public PageState getDefaultState() {
        return state;
    }

    @Override
    public boolean canUnlock(IPageOwner owner, IUnlockEvent event) {
        return condition == null || condition.accepts(event) && condition.matches(owner, event);
    }

    @Override
    public IPage next() {
        int i = Math.min(Pages.instance().getTotalPages() - 1, index + 1);
        return Pages.instance().getByIndex(i);
    }

    @Override
    public IPage prev() {
        if (index <= 0) {
            return this;
        }

        return Pages.instance().getByIndex(index - 1);
    }

    @Override
    public int compareTo(IPage o) {
        return getIndex() - o.getIndex();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IPage && getName().equals(((IPage)o).getName());
    }
}
