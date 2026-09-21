package com.algorithmlx.dimore.init.post

import com.algorithmlx.dimore.init.config.JsonComment
import com.algorithmlx.dimore.init.config.JsonDefaults
import com.algorithmlx.dimore.init.config.MiningLevel
import com.algorithmlx.dimore.init.config.MiningSettings
import com.algorithmlx.dimore.util.OreDimensionType
import com.algorithmlx.dimore.util.OrePlacementConfig
import com.algorithmlx.dimore.util.ResLoc
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.util.valueproviders.IntProvider
import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
//$ if >=26.3.0 'import net.minecraft.world.level.levelgen.feature.BlockReplacement' else 'import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration'
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest

val ExampleBlock = PostBlock(
    displayName = "My First Ore",
    isRedstone = false,
    mining = MiningSettings(toolLevel = MiningLevel.IRON),
    generationSettings = PostBlock.GenerationSettings(
        //$ if fabric '"minecraft:overworld",' else '"#minecraft:is_overworld",'
        "minecraft:overworld",
        PostBlock.GenerationSettings.ConfiguredFeature(
            size = 1,
            count = 12,
            minHeight = -60,
            maxHeight = 120,
            oreTarget = PostBlock.TargetType(from = "minecraft:stone")
        )
    )
)

@JsonDefaults(recursive = true)
@Serializable
data class PostBlock(
    @JsonComment([
        "Display name of block. If empty, use default language key: blocks.dimore.custom.block_id"
    ])
    @SerialName("display_name")
    val displayName: String = "",
    @JsonComment([
        "Makes block with redstone logic (light, interact)"
    ])
    @SerialName("is_redstone")
    val isRedstone: Boolean = false,
    @JsonComment([
        "Block properties"
    ])
    val properties: SimplyProperties = SimplyProperties(),
    @JsonComment([
        "Mining tool and its minimum level. Null keeps vanilla block-property behavior."
    ])
    val mining: MiningSettings? = null,
    @JsonComment([
        "Experience drop settings",
        "May be \"single\" and \"range\"",
        "May be null",
        "",
        "Single example: { \"type\": \"single\", \"value\": 1 }",
        "",
        "Range example: { \"type\": \"range\", \"min\": 0, \"max\": 1 }",
    ], multiline = true)
    @SerialName("experience_drop")
    val experienceDrop: ExperienceDrop = SingleExperience(1),
    @JsonComment([
        "Block generation settings"
    ])
    @SerialName("generation")
    val generationSettings: GenerationSettings
) {
    @Serializable
    data class GenerationSettings(
        //? if fabric {
        @JsonComment(["Dimension id where this block generates."])
        @SerialName("dimension")
        //?} else {
        /*@JsonComment(["Biome id or #biome_tag where this block generates."])
        @SerialName("biome")
        *///?}
        val target: String,
        val config: ConfiguredFeature,
    ) {
        fun asDimensionType() = OreReplacementRule(config.oreTarget)

        open class OreReplacementRule(
            val target: TargetType
        ): OreDimensionType {
            //$ if >=26.3.0 'override fun replacementSettings(block: BlockState): BlockReplacement = BlockReplacement.replace(target.asRuleTest(), block)' else 'override fun replacementSettings(block: BlockState): OreConfiguration.TargetBlockState = OreConfiguration.target(target.asRuleTest(), block)'
            override fun replacementSettings(block: BlockState): OreConfiguration.TargetBlockState = OreConfiguration.target(target.asRuleTest(), block)
        }

        @Serializable
        class ConfiguredFeature(
            @JsonComment(["The size of a single ore vein."])
            override val size: Int,
            @JsonComment(["Number of ore veins per chunk."])
            override val count: Int,
            @JsonComment(["Minimum generation height"])
            @SerialName("min_height")
            override val minHeight: Int,
            @JsonComment(["Maximum generation height"])
            @SerialName("max_height")
            override val maxHeight: Int,
            @JsonComment(["Settings of ore generation"])
            @SerialName("ore_target")
            val oreTarget: TargetType,
        ) : OrePlacementConfig
    }

    @Serializable
    data class TargetType(
        @JsonComment(["Block replace type.", "Can be \"block\" and \"tag\""], multiline = true)
        val type: String = "block",
        val from: String
    ) {
        @Transient val isBlock = this.type == "block"
        @Transient val isTag = !isBlock

        fun asRuleTest(): RuleTest {
            return if (isTag)
                TagMatchTest(TagKey.create(Registries.BLOCK, ResLoc.parse(from)))
            else {
                val blockId = ResLoc.parse(from)
                val block = BuiltInRegistries.BLOCK
                    //$ if >1.21.1 '.getValue(blockId)' else '.get(blockId)'
                    .getValue(blockId)
                BlockMatchTest(block)
            }
        }
    }

    @Serializable
    data class SimplyProperties(
        @SerialName("no_collision")
        val noCollision: Boolean = false,
        @SerialName("no_occlusion")
        val noOcclusion: Boolean = false,
        val friction: Float = 0F,
        @SerialName("speed_factor")
        val speedFactor: Float = 0F,
        @SerialName("jump_factor")
        val jumpFactor: Float = 0F,
        @SerialName("light_level")
        val lightLevel: Int = 0, // it's too simple to JSON encode
        @SerialName("destroy_time")
        val destroyTime: Float = 0F,
        @SerialName("explosion_resistance")
        val explosionResistance: Float = 0F,
        val instabreak: Boolean = false,
        @SerialName("random_ticks")
        val randomTicks: Boolean = false,
        @SerialName("dynamic_shape")
        val dynamicShape: Boolean = false,
        @SerialName("no_loot_table")
        val noLootTable: Boolean = false,
        @SerialName("ignited_by_lava")
        val ignitedByLava: Boolean = false,
        val liquid: Boolean = false,
        @SerialName("force_solid_on")
        val forceSolidOn: Boolean = false,
        @SerialName("require_tool")
        val requiresCorrectToolForDrops: Boolean = false,
        @SerialName("no_terrain_particles")
        val noTerrainParticles: Boolean = false,
        val replaceable: Boolean = false
    ) {
        fun asBlockBehaviourProperties(forceCorrectTool: Boolean = false): BlockBehaviour.Properties =
            BlockBehaviour.Properties.of().apply {
                if (noCollision)
                    //$ if >1.21.1 'this.noCollision()' else 'this.noCollission()'
                    this.noCollision()
                if (noOcclusion) this.noOcclusion()
                if (friction != 0F) this.friction(friction)
                if (speedFactor != 0F) this.speedFactor(speedFactor)
                if (jumpFactor != 0F) this.jumpFactor(jumpFactor)
                if (lightLevel != 0) this.lightLevel { lightLevel }
                if (destroyTime != 0F) this.destroyTime(destroyTime)
                if (explosionResistance != 0F) this.explosionResistance(explosionResistance)
                if (instabreak) this.instabreak()
                if (randomTicks) this.randomTicks()
                if (dynamicShape) this.dynamicShape()
                if (noLootTable) this.noLootTable()
                if (ignitedByLava) this.ignitedByLava()
                if (liquid) this.liquid()
                if (forceSolidOn) this.forceSolidOn()
                if (requiresCorrectToolForDrops || forceCorrectTool) this.requiresCorrectToolForDrops()
                if (noTerrainParticles) this.noTerrainParticles()
                if (replaceable) this.replaceable()
            }
    }
}

@Serializable
sealed class ExperienceDrop {
    abstract fun asMC(): IntProvider
}

@Serializable
@SerialName("single")
data class SingleExperience(
    val value: Int
): ExperienceDrop() {
    override fun asMC(): IntProvider = ConstantInt.of(value)
}

@Serializable
@SerialName("range")
data class RangedExperience(
    val min: Int,
    val max: Int
): ExperienceDrop() {
    override fun asMC(): IntProvider = UniformInt.of(min, max)
}
