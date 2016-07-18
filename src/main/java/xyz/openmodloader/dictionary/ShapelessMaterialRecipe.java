package xyz.openmodloader.dictionary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/**
 * Shapeless recipe that uses the material dictionary
 */
public class ShapelessMaterialRecipe implements IRecipe {

    private final ItemStack output;
    private final List<Predicate<ItemStack>> matchers;

    /**
     * Creates a new shapeless material recipe
     *
     * @param output recipe output
     * @param matchers recipe input
     */
    public ShapelessMaterialRecipe(ItemStack output, List<Predicate<ItemStack>> matchers) {
        this.output = output;
        this.matchers = matchers;
    }

    /**
     * Creates a new shapeless material recipe
     *
     * @param output recipe output
     * @param input recipe input. Valid ingredients include item stacks, items,
     *        blocks, {@link Dictionaries#MATERIALS material IDs}, and
     *        {@link ItemStackDictionary item stack matchers}.
     * @see net.minecraft.item.crafting.CraftingManager
     */
    public ShapelessMaterialRecipe(Block output, Object... input) {
        this(new ItemStack(output), input);
    }

    /**
     * Creates a new shapeless material recipe
     *
     * @param output recipe output
     * @param input recipe input. Valid ingredients include item stacks, items,
     *        blocks, {@link Dictionaries#MATERIALS material IDs}, and
     *        {@link ItemStackDictionary item stack matchers}.
     * @see net.minecraft.item.crafting.CraftingManager
     */
    public ShapelessMaterialRecipe(Item output, Object... input) {
        this(new ItemStack(output), input);
    }

    /**
     * Creates a new shapeless material recipe
     *
     * @param output recipe output
     * @param input recipe input. Valid ingredients include item stacks, items,
     *        blocks, {@link Dictionaries#MATERIALS material IDs}, and
     *        {@link ItemStackDictionary item stack matchers}.
     * @see net.minecraft.item.crafting.CraftingManager
     */
    public ShapelessMaterialRecipe(ItemStack output, Object... input) {
        this.output = output;
        this.matchers = new ArrayList<>();

        for (Object obj : input) {
            if (obj instanceof Predicate) {
                matchers.add((Predicate<ItemStack>) obj);
            } else if (obj instanceof ItemStack) {
                matchers.add(ItemStackDictionary.matcherOf((ItemStack) obj));
            } else if (obj instanceof Item) {
                matchers.add(ItemStackDictionary.matcherOf(new ItemStack((Item) obj)));
            } else if (obj instanceof Block) {
                matchers.add(ItemStackDictionary.matcherOf(new ItemStack((Block) obj)));
            } else if (obj instanceof String) {
                matchers.add(Dictionaries.MATERIALS.get((String) obj));
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
        List<Predicate<ItemStack>> remaining = new ArrayList<>(matchers);

        matching: for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);

            if (stack == null) {
                continue;
            }

            for (Iterator<Predicate<ItemStack>> iterator = remaining.iterator(); iterator.hasNext();) {
                Predicate<ItemStack> matcher = iterator.next();

                if (matcher.test(stack)) {
                    iterator.remove();
                    continue matching;
                }
            }

            return false;
        }

        return remaining.isEmpty();
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting var1) {
        return output.copy();
    }

    @Override
    public int getRecipeSize() {
        return matchers.size();
    }
}