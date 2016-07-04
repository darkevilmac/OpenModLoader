package xyz.openmodloader.dictionary;

import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;

public final class Dictionaries {

    private Dictionaries() {
    }

    /**
     * The material dictionary. Each material has a string ID and can have
     * multiple item stack matchers.
     *
     * @see ShapedMaterialRecipe
     * @see ShapelessMaterialRecipe
     */
    public static final ItemStackDictionary MATERIALS = new ItemStackDictionary();

    /**
     * Registers Vanilla items to the material registry, and replaces some
     * Vanilla recipes with material recipes.
     */
    public static void init() {
        MATERIALS.register("logWood", Blocks.LOG, Blocks.LOG2);
        MATERIALS.register("stickWood", Items.STICK);
        MATERIALS.register("planksWood", Blocks.PLANKS);
        MATERIALS.register("stairsWood", Blocks.ACACIA_STAIRS, Blocks.BIRCH_STAIRS, Blocks.DARK_OAK_STAIRS, Blocks.JUNGLE_STAIRS, Blocks.OAK_STAIRS, Blocks.SPRUCE_STAIRS);
        MATERIALS.register("slabWood", Blocks.WOODEN_SLAB);
        MATERIALS.register("ladderWood", Blocks.LADDER);
        MATERIALS.register("chestWood", Blocks.CHEST);
        MATERIALS.register("saplingTree", Blocks.SAPLING);
        MATERIALS.register("leavesTree", Blocks.LEAVES, Blocks.LEAVES2);

        MATERIALS.register("oreCoal", Blocks.COAL_ORE);
        MATERIALS.register("oreDiamond", Blocks.DIAMOND_ORE);
        MATERIALS.register("oreEmerald", Blocks.EMERALD_ORE);
        MATERIALS.register("oreGold", Blocks.GOLD_ORE);
        MATERIALS.register("oreIron", Blocks.IRON_ORE);
        MATERIALS.register("oreLapiz", Blocks.LAPIS_ORE);
        MATERIALS.register("oreQuartz", Blocks.QUARTZ_ORE);
        MATERIALS.register("oreRedstone", Blocks.LIT_REDSTONE_ORE, Blocks.REDSTONE_ORE);

        MATERIALS.register("blockCoal", Blocks.COAL_BLOCK);
        MATERIALS.register("blockDiamond", Blocks.DIAMOND_BLOCK);
        MATERIALS.register("blockEmerald", Blocks.EMERALD_BLOCK);
        MATERIALS.register("blockGold", Blocks.GOLD_BLOCK);
        MATERIALS.register("blockIron", Blocks.IRON_BLOCK);
        MATERIALS.register("blockLapiz", Blocks.LAPIS_BLOCK);
        MATERIALS.register("blockQuartz", Blocks.QUARTZ_BLOCK);
        MATERIALS.register("blockRedstone", Blocks.REDSTONE_BLOCK);

        MATERIALS.register("ingotGold", Items.GOLD_INGOT);
        MATERIALS.register("ingotIron", Items.IRON_INGOT);
        MATERIALS.register("gemDiamond", Items.DIAMOND);
        MATERIALS.register("gemEmerald", Items.EMERALD);
        MATERIALS.register("dustRedstone", Items.REDSTONE);

        MATERIALS.register("record", Items.RECORD_11, Items.RECORD_13, Items.RECORD_BLOCKS, Items.RECORD_CAT, Items.RECORD_CHIRP, Items.RECORD_FAR, Items.RECORD_MALL, Items.RECORD_MELLOHI, Items.RECORD_STAL, Items.RECORD_STRAD, Items.RECORD_WAIT, Items.RECORD_WARD);

        MATERIALS.register("dirt", Blocks.DIRT);
        MATERIALS.register("grass", Blocks.GRASS);
        MATERIALS.register("stone", Blocks.STONE);
        MATERIALS.register("cobblestone", Blocks.COBBLESTONE);

        MATERIALS.register("wool", Blocks.WOOL);
        MATERIALS.register("dye", Items.DYE);

        MATERIALS.register("glass", Blocks.GLASS, Blocks.STAINED_GLASS);
        MATERIALS.register("glassClear", Blocks.GLASS);
        MATERIALS.register("glassColored", Blocks.STAINED_GLASS);
        MATERIALS.register("paneGlass", Blocks.GLASS_PANE, Blocks.STAINED_GLASS_PANE);
        MATERIALS.register("paneGlassClear", Blocks.GLASS_PANE);
        MATERIALS.register("paneGlassColored", Blocks.STAINED_GLASS_PANE);

        List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();

        for (ListIterator<IRecipe> recipeIterator = recipes.listIterator(); recipeIterator.hasNext();) {
            IRecipe recipe = recipeIterator.next();
            ItemStack output = recipe.getRecipeOutput();
            if (output == null) {
                continue;
            }
            Item item = output.getItem();
            Block block = Block.getBlockFromItem(item);
            if (block == Blocks.CHEST) {
                recipeIterator.set(new ShapedMaterialRecipe(output, "PPP", "P P", "PPP", 'P', "planksWood"));
            } else if (block == Blocks.FURNACE) {
                recipeIterator.set(new ShapedMaterialRecipe(output, "CCC", "C C", "CCC", 'C', "cobblestone"));
            } else if (block == Blocks.CRAFTING_TABLE) {
                recipeIterator.set(new ShapedMaterialRecipe(output, "PP", "PP", 'P', "planksWood"));
            } else if (block == Blocks.WOOL && recipe instanceof ShapelessRecipes) {
                recipeIterator.remove();
            } else if (block == Blocks.STAINED_HARDENED_CLAY || block == Blocks.STAINED_GLASS) {
                recipeIterator.remove();
            }
        }

        for (EnumDyeColor dyeColor : EnumDyeColor.values()) {
            String name = WordUtils.capitalize(dyeColor.toString());
            String wool = "wool" + name;
            String glass = "glass" + name;
            String glassPane = "paneGlass" + name;
            String dye = "dye" + name;
            int blockMeta = dyeColor.getMetadata();
            int dyeMeta = dyeColor.getDyeDamage();
            MATERIALS.register(wool, new ItemStack(Blocks.WOOL, 1, blockMeta));
            MATERIALS.register(glass, new ItemStack(Blocks.STAINED_GLASS, 1, blockMeta));
            MATERIALS.register(glassPane, new ItemStack(Blocks.STAINED_GLASS_PANE, 1, blockMeta));
            MATERIALS.register(dye, new ItemStack(Items.DYE, 1, dyeMeta));
            recipes.add(new ShapelessMaterialRecipe(new ItemStack(Blocks.WOOL, 1, blockMeta), dye, new ItemStack(Blocks.WOOL)));
            recipes.add(new ShapedMaterialRecipe(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 8, blockMeta), "CCC", "CDC", "CCC", 'C', new ItemStack(Blocks.HARDENED_CLAY), 'D', dye));
            recipes.add(new ShapedMaterialRecipe(new ItemStack(Blocks.STAINED_GLASS, 8, blockMeta), "GGG", "GDG", "GGG", 'G', new ItemStack(Blocks.GLASS), 'D', dye));
            recipes.add(new ShapelessMaterialRecipe(new ItemStack(Blocks.CARPET, 3, blockMeta), "WW", 'W', wool));
        }
    }
}
