package com.minelittlepony.unicopia.enchanting;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.minelittlepony.util.AssetWalker;

import net.minecraft.util.ResourceLocation;

public class Pages {

    private static final Pages instance = new Pages();

    public static Pages instance() {
        return instance;
    }

    private final Map<ResourceLocation, PageInstance> pages = Maps.newHashMap();
    private List<PageInstance> pagesByIndex = Lists.newArrayList();

    private final Map<String, IConditionFactory> conditionFactories = Maps.newHashMap();

    private final AssetWalker assets = new AssetWalker(new ResourceLocation("unicopia", "pages"), this::addPage);

    Pages() {
        registerConditionFactory("unicopia:compound_condition", CompoundCondition::new);
        registerConditionFactory("unicopia:page_state", PageStateCondition::new);
        registerConditionFactory("unicopia:spell_crafting", SpellCraftingEvent.Condition::new);
    }

    public void load() {
        pages.clear();
        assets.walk();

        pagesByIndex = pages.values().stream().sorted(this::comparePages).collect(Collectors.toList());

        int i = 0;

        for (PageInstance page : pagesByIndex) {
            page.index = i++;
        }
    }

    protected int comparePages(PageInstance a, PageInstance b) {
        if (a.parent == null && b.parent == null) {
            return 0;
        }

        if (a.parent == null) {
            return -1;
        }

        if (b.parent == null) {
            return 1;
        }

        if (a.parent.equals(b.name)) {
            return 1;
        }

        if (b.parent.equals(a.name)) {
            return -1;
        }

        return a.name.compareTo(b.name);
    }

    void addPage(ResourceLocation id, JsonObject json) throws JsonParseException {
        pages.put(id, new PageInstance(id, json));
    }

    @SuppressWarnings("unchecked")
    <T extends IUnlockEvent> IUnlockCondition<T> createCondition(JsonObject json) {
        String key = json.get("key").getAsString();

        return (IUnlockCondition<T>)conditionFactories.get(key).create(json);
    }

    @Nullable
    public IPage getByName(ResourceLocation name) {
        return pages.get(name);
    }

    @Nullable
    public IPage getByIndex(int index) {
        return pagesByIndex.get(index);
    }

    public Stream<IPage> getUnlockablePages(Predicate<IPage> predicate) {
        return pages.values().stream().map(IPage.class::cast).filter(predicate);
    }

    public void triggerUnlockEvent(IPageOwner owner, IUnlockEvent event, @Nullable IPageUnlockListener unlockListener) {
        pages.values().stream()
            .filter(page -> page.canUnlock(owner, event))
            .forEach(page -> unlockPage(owner, page, unlockListener));
    }

    public void unlockPage(IPageOwner owner, IPage page, @Nullable IPageUnlockListener unlockListener) {
        if (owner.getPageState(page).isLocked()) {
            if (unlockListener == null || unlockListener.onPageUnlocked(page)) {
                owner.setPageState(page, PageState.UNREAD);
            }
        }
    }

    public void registerConditionFactory(String conditionType, IConditionFactory factory) {
        conditionFactories.put(conditionType, factory);
    }

    public int getTotalPages() {
        return pages.size();
    }
}
