package xyz.openmodloader.dictionary;

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
}
