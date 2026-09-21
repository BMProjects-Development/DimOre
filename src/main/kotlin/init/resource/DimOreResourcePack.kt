package com.algorithmlx.dimore.init.resource

import com.algorithmlx.dimore.LOGGER
import com.algorithmlx.dimore.ModId
import com.algorithmlx.dimore.init.Registry
import com.algorithmlx.dimore.init.config.ConfigManager
import com.algorithmlx.dimore.init.config.DimOreConfigManager
import com.algorithmlx.dimore.init.config.MiningConfiguration
import com.algorithmlx.dimore.init.config.MiningLevel
import com.algorithmlx.dimore.init.config.MiningTool
import com.algorithmlx.dimore.util.OreCatalog
import com.algorithmlx.dimore.util.ResLoc
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
//$ if >=26.3.0 'import net.minecraft.server.packs.AbstractPackMetadataResources' else 'import net.minecraft.server.packs.AbstractPackResources'
import net.minecraft.server.packs.AbstractPackResources
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.IoSupplier
import java.io.File
import java.io.InputStream

//$ if >=26.3.0 'class DimOreResourcePack(location: PackLocationInfo) : AbstractPackMetadataResources(location), PackResources {' else 'class DimOreResourcePack(location: PackLocationInfo) : AbstractPackResources(location) {'
class DimOreResourcePack(location: PackLocationInfo) : AbstractPackResources(location) {
    private val targetPath = File("config/$ModId/client/")
    private val generatedData by lazy(::createMiningTagResources)

    init {
        if (!targetPath.exists()) {
            targetPath.mkdirs()
            LOGGER.info("Created client resource directory")
        }
    }

    override fun getRootResource(vararg path: String): IoSupplier<InputStream>? = null

    override fun getResource(type: PackType, location: ResLoc): IoSupplier<InputStream>? {
        if (location.namespace != ModId) return null

        if (type == PackType.SERVER_DATA) {
            return generatedData[location]?.let { bytes -> IoSupplier { bytes.inputStream() } }
        }

        val fileName = mapResourcePathToFile(location.path) ?: return null
        val target = targetPath.resolve(fileName)

        return if (target.isFile) IoSupplier { target.inputStream() } else null
    }

    override fun listResources(
        type: PackType,
        namespace: String,
        directory: String,
        output: PackResources.ResourceOutput
    ) {
        if (namespace != ModId) return

        if (type == PackType.SERVER_DATA) {
            generatedData
                .filterKeys { it.path.startsWith(directory) }
                .forEach { (location, bytes) -> output.accept(location) { bytes.inputStream() } }
            return
        }

        targetPath.listFiles()
            ?.asSequence()
            ?.filter { it.isFile && !it.name.startsWith("_") }
            ?.forEach { file ->
                val resourcePath = mapFileToResourcePath(file.name) ?: return@forEach

                if (resourcePath.startsWith(directory)) {
                    output.accept(ResLoc.parse("$ModId:$resourcePath")) { file.inputStream() }
                }
            }
    }

    override fun getNamespaces(type: PackType): Set<String> = setOf(ModId)

    override fun close() {}

    private fun createMiningTagResources(): Map<ResLoc, ByteArray> {
        val configuredBlocks = buildList<Pair<String, MiningConfiguration>> {
            OreCatalog.all(DimOreConfigManager.config).forEach { ore ->
                add("$ModId:${ore.id}" to ore.settings)
            }

            Registry.getPostBlocks().forEach { (id, block) ->
                block.mining?.let { add("$ModId:$id" to it) }
            }
        }

        val tags = mutableMapOf<String, MutableSet<String>>()

        MiningTool.entries.forEach { tags[it.tagPath] = linkedSetOf() }
        MiningLevel.entries.mapNotNull { it.tagPath }.forEach { tags[it] = linkedSetOf() }

        configuredBlocks.forEach { (blockId, mining) ->
            tags.getOrPut(mining.tool.tagPath, ::linkedSetOf).add(blockId)
            mining.toolLevel.tagPath?.let { tag ->
                tags.getOrPut(tag, ::linkedSetOf).add(blockId)
            }
        }

        return buildMap {
            tags.forEach { (tagPath, blockIds) ->
                val bytes = encodeTag(blockIds)

                listOf("tags/block", "tags/blocks").forEach { tagDirectory ->
                    val location = ResLoc.fromNamespaceAndPath(ModId, "$tagDirectory/$tagPath.json")
                    put(location, bytes)
                }
            }
        }
    }

    private fun encodeTag(blockIds: Set<String>): ByteArray {
        val json = JsonObject(
            mapOf(
                "replace" to JsonPrimitive(false),
                "values" to JsonArray(blockIds.sorted().map(::JsonPrimitive))
            )
        )
        return ConfigManager.json
            .encodeToString(JsonObject.serializer(), json)
            .toByteArray(Charsets.UTF_8)
    }

    private fun mapResourcePathToFile(path: String): String? = when {
        path.startsWith("blockstates/custom.") && path.endsWith(".json") ->
            "blockstate." + path.removePrefix("blockstates/custom.")

        path.startsWith("models/block/custom.") && path.endsWith(".json") ->
            "block.model." + path.removePrefix("models/block/custom.")

        path.startsWith("items/custom.") && path.endsWith(".json") ->
            "items." + path.removePrefix("items/custom.")

        path.startsWith("models/item/custom.") && path.endsWith(".json") ->
            "item.model." + path.removePrefix("models/item/custom.")

        path.startsWith("textures/") && path.endsWith(".png") ->
            "texture." + path.removePrefix("textures/").replace('/', '.')

        path.endsWith(".png") && !path.contains("/") -> "texture.$path"
        else -> null
    }

    private fun mapFileToResourcePath(fileName: String): String? = when {
        fileName.startsWith("blockstate.") && fileName.endsWith(".json") ->
            "blockstates/custom." + fileName.removePrefix("blockstate.")

        fileName.startsWith("block.model.") && fileName.endsWith(".json") ->
            "models/block/custom." + fileName.removePrefix("block.model.")

        fileName.startsWith("items.") && fileName.endsWith(".json") ->
            "items/custom." + fileName.removePrefix("items.")

        fileName.startsWith("item.model.") && fileName.endsWith(".json") ->
            "models/item/custom." + fileName.removePrefix("item.model.")

        fileName.startsWith("texture.") && fileName.endsWith(".png") -> {
            val path = fileName.removePrefix("texture.").removeSuffix(".png")
            "textures/${path.replace('.', '/')}.png"
        }

        else -> null
    }
}
