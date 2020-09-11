package com.refinedmods.refinedstorage.apiimpl.network

import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeFactory
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeManager
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.extensions.Constants
import com.refinedmods.refinedstorage.extensions.getCustomLogger
import java.util.concurrent.ConcurrentHashMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.PersistentState
import net.minecraft.world.World

class NetworkNodeManager(
        name: String?,
        private val world: World
): PersistentState(name), INetworkNodeManager {
    private val nodes: ConcurrentHashMap<BlockPos, INetworkNode> = ConcurrentHashMap()

    companion object {
        private val log = getCustomLogger(NetworkNodeManager::class)

        const val NAME = "refinedstorage_nodes"
        private const val NBT_NODES = "Nodes"
        private const val NBT_NODE_ID = "Id"
        private const val NBT_NODE_DATA = "Data"
        private const val NBT_NODE_POS = "Pos"
    }

    override fun fromTag(tag: CompoundTag) {
        if (tag.contains(NBT_NODES)) {

            val nodesTag: ListTag = tag.getList(NBT_NODES, Constants.NBT.COMPOUND_TAG)

            nodes.clear()
            nodesTag.indices.forEach { i ->
                val nodeTag: CompoundTag = nodesTag.getCompound(i)
                val id = Identifier(nodeTag.getString(NBT_NODE_ID))
                val data: CompoundTag = nodeTag.getCompound(NBT_NODE_DATA)
                val pos: BlockPos = BlockPos.fromLong(nodeTag.getLong(NBT_NODE_POS))

                when(val factory: INetworkNodeFactory? = API.networkNodeRegistry[id]){
                    null -> log.warn("Factory for $id not found in network node registry")
                    else -> {
                        try {
                            nodes[pos] = factory(data, world, pos)
                        } catch (t: Throwable) {
                            log.error("Could not read network node", t)
                        }
                    }
                }
            }
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        val list = ListTag()
        nodes.values.forEach { node ->
            try {
                val nodeTag = CompoundTag()
                nodeTag.putString(NBT_NODE_ID, node.id.toString())
                nodeTag.putLong(NBT_NODE_POS, node.pos.asLong())
                nodeTag.put(NBT_NODE_DATA, node.write(CompoundTag()))
                list.add(nodeTag)
            } catch (t: Throwable) {
                log.error("Error while saving network node", t)
            }
        }
        tag.put(NBT_NODES, list)
        return tag
    }

    override fun getCachedNode(pos: BlockPos): INetworkNode? = nodes[pos]

    override fun getNode(pos: BlockPos): INetworkNode? {
        // TODO possible self-registering cache alternative
//        // Lookup and cache
//        return BlockComponents.get(RSComponents.NETWORK_NODE_PROXY, world, pos)?.let {
//            nodes[pos] = it.node
//            it.node
//        }

        // Use cache if it exists
        if(nodes.contains(pos)) {
            return nodes[pos]
        }

        return when (val entity = world.getBlockEntity(pos)) {
            is INetworkNode -> {
                nodes[pos] = entity
                entity
            }
            else -> null
        }
    }

    override fun removeNode(pos: BlockPos) {
        nodes.remove(pos)
        markDirty()
    }

    override fun setNode(pos: BlockPos, node: INetworkNode) {
        nodes[pos] = node
        markDirty()
    }

    override fun markDirty() {
        if(!API.isLoading) super.markDirty()
    }

    override fun all(): Collection<INetworkNode> {
        return nodes.values
    }

}