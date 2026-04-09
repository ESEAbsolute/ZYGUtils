package icu.eseabs0.zygutils.registry;

import icu.eseabs0.zygutils.types.ElementType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Optional;

public final class ElementRegistry extends PanlingRegistryBase<ElementType> {
    public static final ElementRegistry INSTANCE = new ElementRegistry();

    public static final Identifier METAL = id("panling", "metal");
    public static final Identifier WOOD = id("panling", "wood");
    public static final Identifier WATER = id("panling", "water");
    public static final Identifier FIRE = id("panling", "fire");
    public static final Identifier EARTH = id("panling", "earth");
    public static final Identifier REFINED_METAL = id("panling", "refined_metal");
    public static final Identifier REFINED_WOOD = id("panling", "refined_wood");
    public static final Identifier REFINED_WATER = id("panling", "refined_water");
    public static final Identifier REFINED_FIRE = id("panling", "refined_fire");
    public static final Identifier REFINED_EARTH = id("panling", "refined_earth");

    private ElementRegistry() {
        // Initialization is done in init() to avoid class loading deadlocks
    }

    public void init() {
        if (!TYPE_TO_ID.isEmpty()) return; // Already initialized

        map(ElementType.METAL, METAL);
        map(ElementType.WOOD, WOOD);
        map(ElementType.WATER, WATER);
        map(ElementType.FIRE, FIRE);
        map(ElementType.EARTH, EARTH);

        map(ElementType.REFINED_METAL, REFINED_METAL);
        map(ElementType.REFINED_WOOD, REFINED_WOOD);
        map(ElementType.REFINED_WATER, REFINED_WATER);
        map(ElementType.REFINED_FIRE, REFINED_FIRE);
        map(ElementType.REFINED_EARTH, REFINED_EARTH);
    }

    public static Optional<ElementType> parseElement(ItemStack stack) {
        return INSTANCE.parse(stack);
    }
}
