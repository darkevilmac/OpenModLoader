package xyz.openmodloader.dictionary;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

/**
 * An extended version of Dictionary for ItemStacks,
 * with a few utility methods.
 */
public class ItemStackDictionary extends Dictionary<String, ItemStack> {

    /**
     * The metadata value of wildcard
     */
    public static final int WILDCARD_METADATA = Short.MAX_VALUE;

    /**
     * An item stack predicate that checks for null
     */
    public static final Predicate<ItemStack> NULL_MATCHER = (stack) -> stack == null;

    /**
     * Creates an item stack matcher.
     *
     * @param value    the value
     * @param checkNBT set to true to check NBT tags
     * @return the matcher
     */
    public static Predicate<ItemStack> matcherOf(ItemStack value, boolean checkNBT) {
        return (stack) -> {
            if (value == stack) return true;
            if (value == null || stack == null) return false;
            if (value.getItem() != stack.getItem()) return false;
            if (value.getMetadata() != WILDCARD_METADATA && value.getMetadata() != stack.getMetadata()) return false;
            return !checkNBT || ItemStack.areItemStackTagsEqual(value, stack);
        };
    }

    /**
     * Creates an NBT-insensitive item stack matcher.
     *
     * @param value the value
     * @return the matcher
     */
    public static Predicate<ItemStack> matcherOf(ItemStack value) {
        return matcherOf(value, false);
    }

    /**
     * Creates an NBT-insensitive item matcher.
     *
     * @param value the value
     * @return the matcher
     */
    public static Predicate<ItemStack> matcherOf(Item value) {
        return matcherOf(new ItemStack(value, 1, WILDCARD_METADATA), false);
    }

    /**
     * Creates an NBT-insensitive item block matcher.
     *
     * @param value the value
     * @return the matcher
     */
    public static Predicate<ItemStack> matcherOf(Block value) {
        return matcherOf(new ItemStack(value, 1, WILDCARD_METADATA), false);
    }

    /**
     * Registers an item stack.
     *
     * @param key   the key
     * @param value the value
     */
    public void register(String key, ItemStack value) {
        register(key, matcherOf(value));
    }

    /**
     * Registers item stacks.
     *
     * @param key    the key
     * @param values the values
     */
    public void register(String key, ItemStack... values) {
        for (ItemStack value : values) {
            register(key, value);
        }
    }

    /**
     * Registers an item.
     *
     * @param key the key
     * @param value the value
     */
    public void register(String key, Item value) {
        register(key, matcherOf(value));
    }

    /**
     * Registers items.
     *
     * @param key    the key
     * @param values the values
     */
    public void register(String key, Item... values) {
        for (Item value : values) {
            register(key, value);
        }
    }

    /**
     * Registers a block.
     *
     * @param key the key
     * @param value the value
     */
    public void register(String key, Block value) {
        register(key, matcherOf(value));
    }

    /**
     * Registers blocks.
     *
     * @param key    the key
     * @param values the values
     */
    public void register(String key, Block... values) {
        for (Block value : values) {
            register(key, value);
        }
    }
}