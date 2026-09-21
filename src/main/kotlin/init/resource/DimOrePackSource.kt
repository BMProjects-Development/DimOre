package com.algorithmlx.dimore.init.resource

import com.algorithmlx.dimore.ModId
import net.minecraft.network.chat.Component
import net.minecraft.server.packs.PackLocationInfo
//? if >=26.3.0 {
/*import net.minecraft.server.packs.PackMetadataResources
*///?}
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.PackSelectionConfig
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackCompatibility
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.server.packs.repository.RepositorySource
import net.minecraft.world.flag.FeatureFlagSet
import java.util.Optional
import java.util.function.Consumer
//? if >=26.3.0 {
/*import java.util.stream.Stream
*///?}

object DimOrePackSource : RepositorySource {
    override fun loadPacks(consumer: Consumer<Pack>) {
        consumer.accept(createPack())
    }

    private fun createPack(): Pack {
        val displayName = Component.literal("$ModId Generated Resources")
        val location = PackLocationInfo(
            "${ModId}_generated_resources",
            displayName,
            PackSource.BUILT_IN,
            Optional.empty()
        )
        val resources = object : Pack.ResourcesSupplier {
            //? if >=26.3.0 {
            /*override fun openMetadata(location: PackLocationInfo): PackMetadataResources = DimOreResourcePack(location)

            override fun openResources(location: PackLocationInfo, metadata: Pack.Metadata): Stream<PackResources> =
                Stream.of(DimOreResourcePack(location))
            *///?} else {
            override fun openPrimary(location: PackLocationInfo): PackResources = DimOreResourcePack(location)

            override fun openFull(location: PackLocationInfo, metadata: Pack.Metadata): PackResources =
                openPrimary(location)
            //?}
        }
        val metadata = Pack.Metadata(
            displayName,
            PackCompatibility.COMPATIBLE,
            FeatureFlagSet.of(),
            emptyList()
        )
        val selection = PackSelectionConfig(true, Pack.Position.TOP, true)

        return Pack(location, resources, metadata, selection)
    }
}
