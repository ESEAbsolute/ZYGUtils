package icu.eseabs0.zygutils.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ZYGConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "zygutils.json");

    public static ZYGConfig INSTANCE = new ZYGConfig();

    // Master Switch
    public boolean masterSwitch = true;

    // Element Bank Config
    public enum ElementStoreMode {
        STORE, IGNORE, ALCHEMIST
    }
    public ElementStoreMode elementStoreMode = ElementStoreMode.ALCHEMIST;

    public enum NormalElementStrategy {
        KEEP_10_15, STORE_ALL, IGNORE
    }
    public enum RefinedElementStrategy {
        KEEP_ABOVE_10, STORE_ALL, IGNORE
    }

    public NormalElementStrategy metalStrategy = NormalElementStrategy.KEEP_10_15;
    public NormalElementStrategy woodStrategy = NormalElementStrategy.KEEP_10_15;
    public NormalElementStrategy waterStrategy = NormalElementStrategy.KEEP_10_15;
    public NormalElementStrategy fireStrategy = NormalElementStrategy.KEEP_10_15;
    public NormalElementStrategy earthStrategy = NormalElementStrategy.KEEP_10_15;

    public RefinedElementStrategy refinedMetalStrategy = RefinedElementStrategy.KEEP_ABOVE_10;
    public RefinedElementStrategy refinedWoodStrategy = RefinedElementStrategy.KEEP_ABOVE_10;
    public RefinedElementStrategy refinedWaterStrategy = RefinedElementStrategy.KEEP_ABOVE_10;
    public RefinedElementStrategy refinedFireStrategy = RefinedElementStrategy.KEEP_ABOVE_10;
    public RefinedElementStrategy refinedEarthStrategy = RefinedElementStrategy.KEEP_ABOVE_10;

    // Bank Config
    public boolean storeCopperCash = true;
    public boolean storeGoldIngot = true;
    public boolean storeSilverTicket = true;
    public boolean autoDisableSilverTicket = true;

    // Debug Config
    public boolean debugGuiOpen = false;
    public boolean debugStorageTrigger = false;
    public boolean debugElementDecision = false;

    public static void load() {
        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                INSTANCE = GSON.fromJson(reader, ZYGConfig.class);
                if (INSTANCE == null) INSTANCE = new ZYGConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
