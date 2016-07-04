package xyz.openmodloader.dictionary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/**
 * Shaped recipe that uses the material dictionary
 */
public class ShapedMaterialRecipe implements IRecipe {

    private final List<Predicate<ItemStack>> matchers;
    private final ItemStack output;
    private final int size;
    private int width;
    private int height;

    /**
     * Creates a new shaped material recipe
     *
     * @param width recipe width
     * @param height recipe height
     * @param matchers recipe input
     * @param output recipe output
     */
    public ShapedMaterialRecipe(int width, int height, List<Predicate<ItemStack>> matchers, ItemStack output) {
        this.width = width;
        this.height = height;
        this.size = width * height;
        this.matchers = matchers;
        this.output = output;
    }

    /**
     * Creates a new shaped material recipe. Example usage:
     * {@code new ShapedMaterialRecipe(Blocks.IRON_BLOCK, "III", "III", "III", 'I', "ingotIron")}
     *
     * @param output recipe output
     * @param input recipe input. Valid ingredients include item stacks, items,
     *        blocks, {@link Dictionaries#MATERIALS material IDs}, and
     *        {@link ItemStackDictionary item stack matchers}.
     * @see net.minecraft.item.crafting.CraftingManager
     */
    public ShapedMaterialRecipe(Block output, Object... input) {
        this(new ItemStack(output), input);
    }

    /**
     * Creates a new shaped material recipe. Example usage:
     * {@code new ShapedMaterialRecipe(Items.WOODEN_SHOVEL, "P", "S", "S", 'P', "planksWood", 'S', "stickWood")}
     *
     * @param output recipe output
     * @param input recipe input. Valid ingredients include item stacks, items,
     *        blocks, {@link Dictionaries#MATERIALS material IDs}, and
     *        {@link ItemStackDictionary item stack matchers}.
     * @see net.minecraft.item.crafting.CraftingManager
     */
    public ShapedMaterialRecipe(Item output, Object... input) {
        this(new ItemStack(output), input);
    }

    /**
     * Creates a new shaped material recipe. Example usage:
     * {@code new ShapedMaterialRecipe(new ItemStack(Blocks.QUARTZ_BLOCK, 1, 2), "Q", "Q", 'Q', "blockQuartz")}
     *
     * @param output recipe output
     * @param input recipe input. Valid ingredients include item stacks, items,
     *        blocks, {@link Dictionaries#MATERIALS material IDs}, and
     *        {@link ItemStackDictionary item stack matchers}.
     * @see net.minecraft.item.crafting.CraftingManager
     */
    public ShapedMaterialRecipe(ItemStack output, Object... input) {
        this.output = output;

        int index = 0;
        width = 0;
        height = 0;
        StringBuilder sb = new StringBuilder();

        if (input[index] instanceof String[]) {
            for (String s : (String[]) input[index++]) {
                ++height;
                width = s.length();
                sb.append(s);
            }
        } else {
            while (input[index] instanceof String) {
                String s = (String) input[index++];
                ++height;
                width = s.length();
                sb.append(s);
            }
        }

        String shape = sb.toString();

        Map<Character, Predicate<ItemStack>> map = new HashMap<>();

        while (index < input.length) {
            Character character = (Character) input[index];
            Object obj = input[index + 1];

            if (obj instanceof Item) {
                map.put(character, ItemStackDictionary.matcherOf(new ItemStack((Item) obj)));
            } else if (obj instanceof Block) {
                map.put(character, ItemStackDictionary.matcherOf((Block) obj));
            } else if (obj instanceof ItemStack) {
                map.put(character, ItemStackDictionary.matcherOf((ItemStack) obj));
            } else if (obj instanceof Predicate) {
                map.put(character, (Predicate<ItemStack>) obj);
            } else if (obj instanceof String) {
                map.put(character, Dictionaries.MATERIALS.get((String) obj));
            }

            index += 2;
        }

        size = width * height;
        matchers = Arrays.asList(new Predicate[size]);

        for (int slot = 0; slot < size; ++slot) {
            char character = shape.charAt(slot);
            Predicate<ItemStack> matcher = map.get(character);

            if (matcher != null) {
                matchers.set(slot, matcher);
            } else {
                matchers.set(slot, ItemStackDictionary.NULL_MATCHER);
            }
        }
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        ItemStack[] stacks = new ItemStack[inv.getSizeInventory()];

        for (int slot = 0; slot < stacks.length; ++slot) {
            ItemStack stack = inv.getStackInSlot(slot);

            if (stack != null && stack.getItem().hasContainerItem()) {
                stacks[slot] = new ItemStack(stack.getItem().getContainerItem());
            }
        }

        return stacks;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        for (int x = 0; x <= 3 - width; ++x) {
            for (int y = 0; y <= 3 - height; ++y) {
                if (checkMatch(inv, x, y, true)) {
                    return true;
                }

                if (checkMatch(inv, x, y, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean isMirror) {
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                int subX = x - startX;
                int subY = y - startY;
                Predicate<ItemStack> matcher = ItemStackDictionary.NULL_MATCHER;

                if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
                    if (isMirror) {
                        matcher = matchers.get(width - subX - 1 + subY * width);
                    } else {
                        matcher = matchers.get(subX + subY * width);
                    }
                }

                if (!matcher.test(inv.getStackInRowAndColumn(x, y))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return output.copy();
    }

    @Override
    public int getRecipeSize() {
        return size;
    }
}