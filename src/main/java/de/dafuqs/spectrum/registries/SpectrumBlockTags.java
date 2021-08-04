package de.dafuqs.spectrum.registries;

import de.dafuqs.spectrum.SpectrumCommon;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class SpectrumBlockTags {

    // PLANTS
    public static Tag<Block> MERMAIDS_BRUSH_PLANTABLE;
    public static Tag<Block> QUITOXIC_REEDS_PLANTABLE;

    // DECAY
    public static Tag<Block> DECAY;
    public static Tag<Block> MAGICAL_LEAVES;
    public static Tag<Block> FAILING_SAFE;
    public static Tag<Block> DECAY_OBSIDIAN_CONVERSIONS;
    public static Tag<Block> DECAY_CRYING_OBSIDIAN_CONVERSIONS;
    public static Tag<Block> RUIN_SAFE;
    public static Tag<Block> DECAY_BEDROCK_CONVERSIONS;


    private static Tag<Block> register(String id) {
        return TagRegistry.block(new Identifier(SpectrumCommon.MOD_ID, id));
    }

    public static void register() {
        // PLANTS
        MERMAIDS_BRUSH_PLANTABLE = register("mermaids_brush_plantable");
        QUITOXIC_REEDS_PLANTABLE = register("quitoxic_reeds_plantable");

        // DECAY
        DECAY = register("decay");
        FAILING_SAFE = register("failing_safe");
        MAGICAL_LEAVES = register("magical_leaves");
        DECAY_OBSIDIAN_CONVERSIONS = register("decay_obsidian_conversions");
        DECAY_CRYING_OBSIDIAN_CONVERSIONS = register("decay_crying_obsidian_conversions");
        RUIN_SAFE = register("ruin_safe");
        DECAY_BEDROCK_CONVERSIONS = register("decay_bedrock_conversions");
    }
}