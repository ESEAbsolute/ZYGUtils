package icu.eseabs0.zygutils.registry;

import icu.eseabs0.zygutils.types.PositionedItemType;
import icu.eseabs0.zygutils.utils.LogUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class PanlingRegistryBase<T extends PositionedItemType> {
    protected final Map<T, Identifier> TYPE_TO_ID = new HashMap<>();
    protected final Map<Identifier, T> ID_TO_TYPE = new HashMap<>();

    protected void map(T type, Identifier id) {
        TYPE_TO_ID.put(type, id);
        ID_TO_TYPE.put(id, type);
    }

    protected static Identifier id(String ns, String path) {
        return new Identifier(ns, path);
    }

    public Identifier getId(T type) {
        return TYPE_TO_ID.get(type);
    }

    public Optional<T> typeOf(Identifier id) {
        return Optional.ofNullable(ID_TO_TYPE.get(id));
    }

    /*
        !!! IMPORTANT !!!
        This method is based on the assumption of Panling
        items that the NBT structure of them matches:
        =================================================
        {
            display: {
                Name: '{
                    "translate": "pl.item.name.relifestone"
                }'
            },
            id: "panling: relive_stone"
        }
        =================================================
        where "id" can be accessed by .getNBT().getString("id")

        Should be modified when updates to 1.20.5+
    */
    public Optional<T> parse(ItemStack stack) {
        try {
            if (stack == null || stack.getNbt() == null) return Optional.empty();
            String s = stack.getNbt().getString("id");
//            LogUtils.sendMessage("Registry ID: %s".formatted(s));
            if (s == null || s.isEmpty()) return Optional.empty();

//            LogUtils.sendMessage("Map size: %d".formatted(ID_TO_TYPE.size()));
            Optional<T> result = typeOf(new Identifier(s.toLowerCase()));
//            LogUtils.sendMessage("Parsed result present: %s".formatted(result.isPresent()));
//            result.ifPresent(v -> LogUtils.sendMessage("Parsed type: %s".formatted(v)));
            return result;
        } catch (Exception e) {
//            LogUtils.sendMessage("Exception: %s".formatted(e.getMessage()));
            return Optional.empty();
        }
    }

    public Item getItem(T type) {
        Identifier id = getId(type);
        return id == null ? null : Registries.ITEM.get(id);
    }

    public ItemStack getItemStack(T type, int count) {
        Item item = getItem(type);
        return item == null ? ItemStack.EMPTY : new ItemStack(item, count);
    }
}
