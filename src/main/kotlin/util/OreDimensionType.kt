package com.algorithmlx.dimore.util

import net.minecraft.world.level.block.state.BlockState
//$ if >=26.3.0 'import net.minecraft.world.level.levelgen.feature.BlockReplacement' else 'import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration'
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration

interface OreDimensionType {
    //$ if >=26.3.0 'fun replacementSettings(block: BlockState): BlockReplacement' else 'fun replacementSettings(block: BlockState): OreConfiguration.TargetBlockState'
    fun replacementSettings(block: BlockState): OreConfiguration.TargetBlockState
}
