package com.algorithmlx.dimore.init.post.loot

import com.algorithmlx.dimore.util.ResLoc
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
//$ if >1.21.1 'import net.minecraft.core.HolderLookup' else 'import net.minecraft.core.HolderGetter'
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
//$ if >=26.3.0 'import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders' else 'import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator'
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
//? if >=26.3.0 {
/*import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders
*///?}

@Serializable
@SerialName("ore_bonus")
data class SimpleApplyOreBonus(
    @SerialName("with_enchant")
    val withEnchantment: String
): SimpleFunction() {
    //$ if >1.21.1 'override fun asMC(lookupProvider: HolderLookup.Provider): LootItemConditionalFunction.Builder<*> =' else 'override fun asMC(lookupProvider: HolderGetter.Provider): LootItemConditionalFunction.Builder<*> ='
    override fun asMC(lookupProvider: HolderLookup.Provider): LootItemConditionalFunction.Builder<*> =
        ApplyBonusCount.addOreBonusCount(
            //? if >1.21.1 {
            lookupProvider.getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResLoc.parse(withEnchantment)))
            //?} else {
            /*lookupProvider.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResLoc.parse(withEnchantment)))
            *///?}
        )
}

@Serializable
@SerialName("explosion_decay")
class ExplosionDecay: SimpleFunction() {
    //$ if >1.21.1 'override fun asMC(lookupProvider: HolderLookup.Provider): LootItemConditionalFunction.Builder<*> =' else 'override fun asMC(lookupProvider: HolderGetter.Provider): LootItemConditionalFunction.Builder<*> ='
    override fun asMC(lookupProvider: HolderLookup.Provider): LootItemConditionalFunction.Builder<*> =
        ApplyExplosionDecay.explosionDecay()
}

@Serializable
@SerialName("count_uniform")
data class SetCountUniform(
    val min: Float,
    val max: Float,
    val add: Boolean = false
): SimpleFunction() {
    //$ if >1.21.1 'override fun asMC(lookupProvider: HolderLookup.Provider): LootItemConditionalFunction.Builder<*> =' else 'override fun asMC(lookupProvider: HolderGetter.Provider): LootItemConditionalFunction.Builder<*> ='
    override fun asMC(lookupProvider: HolderLookup.Provider): LootItemConditionalFunction.Builder<*> =
        //$ if >=26.3.0 'SetItemCountFunction.setCount(ContextIntProviders.fromFloat(ContextFloatProviders.between(min, max)), add)' else 'SetItemCountFunction.setCount(UniformGenerator.between(min, max), add)'
        SetItemCountFunction.setCount(UniformGenerator.between(min, max), add)
}
