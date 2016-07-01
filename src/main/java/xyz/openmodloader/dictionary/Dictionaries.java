package xyz.openmodloader.dictionary;

import java.util.List;

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

    private Dictionaries() {}

    /**
     * The material dictionary.
     * Each material has a string ID and can have multiple item stack matchers.
     *
     * @see ShapedMaterialRecipe
     * @see ShapelessMaterialRecipe
     */
    public static final ItemStackDictionary MATERIALS = new ItemStackDictionary();


    /**
     * Registers Vanilla items to the material registry,
     * and replaces some Vanilla recipes with material
     * recipes.
     */
    public static void init() {
        registerMaterial("logWood", Blocks.LOG, Blocks.LOG2);
        registerMaterial("stickWood", Items.STICK);
        registerMaterial("planksWood", Blocks.PLANKS);
        registerMaterial("stairsWood", Blocks.ACACIA_STAIRS, Blocks.BIRCH_STAIRS,
                Blocks.DARK_OAK_STAIRS, Blocks.JUNGLE_STAIRS,
                Blocks.OAK_STAIRS, Blocks.SPRUCE_STAIRS);
        registerMaterial("slabWood", Blocks.WOODEN_SLAB);
        registerMaterial("ladderWood", Blocks.LADDER);
        registerMaterial("chestWood", Blocks.CHEST);
        registerMaterial("saplingTree", Blocks.SAPLING);
        registerMaterial("leavesTree", Blocks.LEAVES, Blocks.LEAVES2);

        registerMaterial("oreCoal", Blocks.COAL_ORE);
        registerMaterial("oreDiamond", Blocks.DIAMOND_ORE);
        registerMaterial("oreEmerald", Blocks.EMERALD_ORE);
        registerMaterial("oreGold", Blocks.GOLD_ORE);
        registerMaterial("oreIron", Blocks.IRON_ORE);
        registerMaterial("oreLapiz", Blocks.LAPIS_ORE);
        registerMaterial("oreQuartz", Blocks.QUARTZ_ORE);
        registerMaterial("oreRedstone", Blocks.LIT_REDSTONE_ORE, Blocks.REDSTONE_ORE);

        registerMaterial("blockCoal", Blocks.COAL_BLOCK);
        registerMaterial("blockDiamond", Blocks.DIAMOND_BLOCK);
        registerMaterial("blockEmerald", Blocks.EMERALD_BLOCK);
        registerMaterial("blockGold", Blocks.GOLD_BLOCK);
        registerMaterial("blockIron", Blocks.IRON_BLOCK);
        registerMaterial("blockLapiz", Blocks.LAPIS_BLOCK);
        registerMaterial("blockQuartz", Blocks.QUARTZ_BLOCK);
        registerMaterial("blockRedstone", Blocks.REDSTONE_BLOCK);

        registerMaterial("ingotGold", Items.GOLD_INGOT);
        registerMaterial("ingotIron", Items.IRON_INGOT);
        registerMaterial("gemDiamond", Items.DIAMOND);
        registerMaterial("gemEmerald", Items.EMERALD);
        registerMaterial("dustRedstone", Items.REDSTONE);

        registerMaterial("record", Items.RECORD_11, Items.RECORD_13, Items.RECORD_BLOCKS,
                Items.RECORD_CAT, Items.RECORD_CHIRP, Items.RECORD_FAR, Items.RECORD_MALL,
                Items.RECORD_MELLOHI, Items.RECORD_STAL, Items.RECORD_STRAD, Items.RECORD_WAIT,
                Items.RECORD_WARD);

        registerMaterial("dirt", Blocks.DIRT);
        registerMaterial("grass", Blocks.GRASS);
        registerMaterial("stone", Blocks.STONE);
        registerMaterial("cobblestone", Blocks.COBBLESTONE);

        registerMaterial("dye", Items.DYE);
        MATERIALS.register("dyeRed", new ItemStack(Blocks.RED_FLOWER, 1, 0));
        MATERIALS.register("dyeYellow", new ItemStack(Blocks.RED_FLOWER, 1, 0));

        for (int i = 0; i < 16; i++) {
            String name = EnumDyeColor.values()[i].toString();
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            MATERIALS.register("dye" + name, new ItemStack(Items.DYE, 1, 15 - i));
        }

        registerMaterial("blockGlass", Blocks.GLASS, Blocks.STAINED_GLASS);
        registerMaterial("blockGlassClear", Blocks.GLASS);
        registerMaterial("blockGlassColored", Blocks.STAINED_GLASS);
        registerMaterial("paneGlass", Blocks.GLASS_PANE, Blocks.STAINED_GLASS_PANE);
        registerMaterial("paneGlassClear", Blocks.GLASS_PANE);
        registerMaterial("paneGlassColored", Blocks.STAINED_GLASS_PANE);

        List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
        Item itemChest = Item.getItemFromBlock(Blocks.CHEST);
        Item itemFurnace = Item.getItemFromBlock(Blocks.FURNACE);
        Item itemCraftingTable = Item.getItemFromBlock(Blocks.CRAFTING_TABLE);
        Item itemWool = Item.getItemFromBlock(Blocks.WOOL);
        Item itemHardenedClay = Item.getItemFromBlock(Blocks.STAINED_HARDENED_CLAY);
        Item itemStainedGlass = Item.getItemFromBlock(Blocks.STAINED_GLASS);
        for (int i = 0; i < recipes.size(); i++) {
            IRecipe recipe = recipes.get(i);
            ItemStack output = recipe.getRecipeOutput();
            if (output == null) {
                continue;
            } else if (output.getItem() == itemChest) {
                recipes.set(i, new ShapedMaterialRecipe(itemChest, "###", "# #", "###", '#', "planksWood"));
            } else if (output.getItem() == itemFurnace) {
                recipes.set(i, new ShapedMaterialRecipe(itemFurnace, "###", "# #", "###", '#', "cobblestone"));
            } else if (output.getItem() == itemCraftingTable) {
                recipes.set(i, new ShapedMaterialRecipe(itemCraftingTable, "##", "##", '#', "planksWood"));
            } else if (output.getItem() == itemWool && recipe instanceof ShapelessRecipes) {
                recipes.remove(i);
            } else if (output.getItem() == itemHardenedClay || output.getItem() == itemStainedGlass) {
                recipes.remove(i);
            }
        }
        for (int i = 0; i < EnumDyeColor.values().length; ++i) {
            String dye = EnumDyeColor.values()[i].toString();
            dye = "dye" + Character.toUpperCase(dye.charAt(0)) + dye.substring(1);
            recipes.add(new ShapelessMaterialRecipe(new ItemStack(itemWool, 1, i), dye, itemWool));
            recipes.add(new ShapedMaterialRecipe(new ItemStack(itemHardenedClay, 8, i), "###", "#X#", "###", '#', new ItemStack(Blocks.HARDENED_CLAY), 'X', dye));
            recipes.add(new ShapedMaterialRecipe(new ItemStack(itemStainedGlass, 8, i), "###", "#X#", "###", '#', new ItemStack(Blocks.GLASS), 'X', dye));
            recipes.add(new ShapelessMaterialRecipe(new ItemStack(Blocks.CARPET, 3, i), "##", '#', new ItemStack(Blocks.WOOL, 1, i)));
        }
    }

    private static void registerMaterial(String key, Block... values) {
        MATERIALS.register(key, values);
    }

    private static void registerMaterial(String key, Item... values) {
        MATERIALS.register(key, values);
    }
}
