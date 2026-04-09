package icu.eseabs0.zygutils.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {
    public static Screen create(Screen parent) {
        ZYGConfig config = ZYGConfig.INSTANCE;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config.zygutils.title"));

        builder.setSavingRunnable(ZYGConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.zygutils.general"));
        
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.zygutils.master_switch"), config.masterSwitch)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    config.masterSwitch = newValue;
                    if (newValue) {
                        icu.eseabs0.zygutils.ZYGUtilsClient.INSTANCE.getEventManager().add(icu.eseabs0.zygutils.events.UpdateListener.class, icu.eseabs0.zygutils.modules.ElementManager.getInstance());
                    } else {
                        icu.eseabs0.zygutils.ZYGUtilsClient.INSTANCE.getEventManager().remove(icu.eseabs0.zygutils.events.UpdateListener.class, icu.eseabs0.zygutils.modules.ElementManager.getInstance());
                    }
                })
                .build());

        // 元素银行配置
        ConfigCategory elementBank = builder.getOrCreateCategory(Text.translatable("config.zygutils.element_bank"));

        elementBank.addEntry(entryBuilder.startEnumSelector(Text.translatable("config.zygutils.mode"), ZYGConfig.ElementStoreMode.class, config.elementStoreMode)
                .setDefaultValue(ZYGConfig.ElementStoreMode.ALCHEMIST)
                .setEnumNameProvider(e -> {
                    if (e == ZYGConfig.ElementStoreMode.STORE) return Text.translatable("config.zygutils.mode.store_all");
                    if (e == ZYGConfig.ElementStoreMode.IGNORE) return Text.translatable("config.zygutils.mode.ignore");
                    return Text.translatable("config.zygutils.mode.alchemist");
                })
                .setSaveConsumer(newValue -> config.elementStoreMode = newValue)
                .build());

        // 金木水火土
        elementBank.addEntry(createNormalElementEntry(entryBuilder, "config.zygutils.metal", config.metalStrategy, newValue -> config.metalStrategy = newValue));
        elementBank.addEntry(createNormalElementEntry(entryBuilder, "config.zygutils.wood", config.woodStrategy, newValue -> config.woodStrategy = newValue));
        elementBank.addEntry(createNormalElementEntry(entryBuilder, "config.zygutils.water", config.waterStrategy, newValue -> config.waterStrategy = newValue));
        elementBank.addEntry(createNormalElementEntry(entryBuilder, "config.zygutils.fire", config.fireStrategy, newValue -> config.fireStrategy = newValue));
        elementBank.addEntry(createNormalElementEntry(entryBuilder, "config.zygutils.earth", config.earthStrategy, newValue -> config.earthStrategy = newValue));

        // 精炼金木水火土
        elementBank.addEntry(createRefinedElementEntry(entryBuilder, "config.zygutils.refined_metal", config.refinedMetalStrategy, newValue -> config.refinedMetalStrategy = newValue));
        elementBank.addEntry(createRefinedElementEntry(entryBuilder, "config.zygutils.refined_wood", config.refinedWoodStrategy, newValue -> config.refinedWoodStrategy = newValue));
        elementBank.addEntry(createRefinedElementEntry(entryBuilder, "config.zygutils.refined_water", config.refinedWaterStrategy, newValue -> config.refinedWaterStrategy = newValue));
        elementBank.addEntry(createRefinedElementEntry(entryBuilder, "config.zygutils.refined_fire", config.refinedFireStrategy, newValue -> config.refinedFireStrategy = newValue));
        elementBank.addEntry(createRefinedElementEntry(entryBuilder, "config.zygutils.refined_earth", config.refinedEarthStrategy, newValue -> config.refinedEarthStrategy = newValue));

        // 钱庄配置
        ConfigCategory bank = builder.getOrCreateCategory(Text.translatable("config.zygutils.bank"));
        bank.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.zygutils.store_copper"), config.storeCopperCash)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.storeCopperCash = newValue)
                .build());
        bank.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.zygutils.store_gold"), config.storeGoldIngot)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.storeGoldIngot = newValue)
                .build());
        bank.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.zygutils.store_silver"), config.storeSilverTicket)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.storeSilverTicket = newValue)
                .build());
        bank.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.zygutils.auto_disable_silver"), config.autoDisableSilverTicket)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.autoDisableSilverTicket = newValue)
                .build());

        // 调试功能
        ConfigCategory debug = builder.getOrCreateCategory(Text.translatable("config.zygutils.debug"));
        debug.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.zygutils.debug_gui"), config.debugGuiOpen)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.debugGuiOpen = newValue)
                .build());
        debug.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.zygutils.debug_trigger"), config.debugStorageTrigger)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.debugStorageTrigger = newValue)
                .build());
        debug.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.zygutils.debug_element_decision"), config.debugElementDecision)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.debugElementDecision = newValue)
                .build());

        return builder.build();
    }

    private static me.shedaniel.clothconfig2.gui.entries.EnumListEntry<ZYGConfig.NormalElementStrategy> createNormalElementEntry(
            ConfigEntryBuilder builder, String translationKey, ZYGConfig.NormalElementStrategy currentValue, java.util.function.Consumer<ZYGConfig.NormalElementStrategy> saveConsumer) {
        return builder.startEnumSelector(Text.translatable(translationKey), ZYGConfig.NormalElementStrategy.class, currentValue)
                .setDefaultValue(ZYGConfig.NormalElementStrategy.KEEP_10_15)
                .setEnumNameProvider(e -> {
                    if (e == ZYGConfig.NormalElementStrategy.STORE_ALL) return Text.translatable("config.zygutils.strategy.store_all");
                    if (e == ZYGConfig.NormalElementStrategy.IGNORE) return Text.translatable("config.zygutils.strategy.ignore");
                    return Text.translatable("config.zygutils.strategy.keep_10_15");
                })
                .setSaveConsumer(saveConsumer)
                .build();
    }

    private static me.shedaniel.clothconfig2.gui.entries.EnumListEntry<ZYGConfig.RefinedElementStrategy> createRefinedElementEntry(
            ConfigEntryBuilder builder, String translationKey, ZYGConfig.RefinedElementStrategy currentValue, java.util.function.Consumer<ZYGConfig.RefinedElementStrategy> saveConsumer) {
        return builder.startEnumSelector(Text.translatable(translationKey), ZYGConfig.RefinedElementStrategy.class, currentValue)
                .setDefaultValue(ZYGConfig.RefinedElementStrategy.KEEP_ABOVE_10)
                .setEnumNameProvider(e -> {
                    if (e == ZYGConfig.RefinedElementStrategy.STORE_ALL) return Text.translatable("config.zygutils.strategy.store_all");
                    if (e == ZYGConfig.RefinedElementStrategy.IGNORE) return Text.translatable("config.zygutils.strategy.ignore_short");
                    return Text.translatable("config.zygutils.strategy.keep_above_10");
                })
                .setSaveConsumer(saveConsumer)
                .build();
    }
}
