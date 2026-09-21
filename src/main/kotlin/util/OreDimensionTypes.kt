package com.algorithmlx.dimore.util

import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
//$ if >=26.3.0 'import net.minecraft.world.level.levelgen.feature.BlockReplacement' else 'import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration'
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest

enum class OreDimensionTypes(
    override val dimensionBlock: () -> Block,
    private val rule: RuleTest
): OreDimensionType, NameTarget {
    OVERWORLD({ Blocks.STONE }, TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES)),
    OVERWORLD_DEEPSLATE({ Blocks.DEEPSLATE }, TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES)),
    NETHER({ Blocks.NETHERRACK }, BlockMatchTest(Blocks.NETHERRACK)),
    END({ Blocks.END_STONE }, BlockMatchTest(Blocks.END_STONE));

    //$ if >=26.3.0 'override fun replacementSettings(block: BlockState): BlockReplacement = BlockReplacement.replace(rule, block)' else 'override fun replacementSettings(block: BlockState): OreConfiguration.TargetBlockState = OreConfiguration.target(rule, block)'
    override fun replacementSettings(block: BlockState): OreConfiguration.TargetBlockState = OreConfiguration.target(rule, block)
}
