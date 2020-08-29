package com.refinedmods.refinedstorage.tile

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.NetworkType
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.network.Network
import com.refinedmods.refinedstorage.apiimpl.network.node.RootNetworkNode
import com.refinedmods.refinedstorage.block.ControllerBlock
import com.refinedmods.refinedstorage.block.ControllerBlock.EnergyType
import com.refinedmods.refinedstorage.block.CreativeControllerBlock
import com.refinedmods.refinedstorage.extensions.DOUBLE
import com.refinedmods.refinedstorage.tile.config.IRedstoneConfigurable
import com.refinedmods.refinedstorage.tile.config.RedstoneMode
import com.refinedmods.refinedstorage.tile.config.RedstoneMode.Companion.createParameter
//import com.refinedmods.refinedstorage.tile.data.RSSerializers
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.thinkslynk.fabric.annotations.registry.RegisterBlockEntity
import com.thinkslynk.fabric.generated.BlockEntityRegistryGenerated
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.chunk.ChunkManager
import net.minecraft.world.chunk.WorldChunk
import team.reborn.energy.EnergySide
import team.reborn.energy.EnergyStorage
import team.reborn.energy.EnergyTier
import java.util.*
import java.util.function.Function

open class ControllerTile(type: NetworkType, entity: BlockEntityType<*>?):
        BaseTile(entity),
        INetworkNodeProxy<RootNetworkNode>,
        IRedstoneConfigurable,
        EnergyStorage
{

    private val type: NetworkType
    var removedNetwork: INetwork? = null
        private set
    private var dummyNetwork: Network? = null
    override fun writeUpdate(tag: CompoundTag): CompoundTag {
        super.writeUpdate(tag)
        tag.putInt(NBT_ENERGY_TYPE, (network as Network).energyType.ordinal)
        return tag
    }

    override fun readUpdate(tag: CompoundTag) {
        if (tag.contains(NBT_ENERGY_TYPE)) {
            world!!.setBlockState(pos, world!!.getBlockState(pos).with(ControllerBlock.ENERGY_TYPE, EnergyType.values()[tag.getInt(NBT_ENERGY_TYPE)]))
        }
        super.readUpdate(tag)
    }

    val network: INetwork
        get() {
            if (world!!.isClient) {
                val net = dummyNetwork ?: Network(world!!, pos, type)
                dummyNetwork = net
                return net
            }
            return instance().getNetworkManager(world as ServerWorld).getNetwork(pos)
                    ?: throw IllegalStateException("No network present at $pos")
        }



    override fun cancelRemoval() {
        super.cancelRemoval()

        if (!world!!.isClient) {
            val manager = instance().getNetworkManager(world as ServerWorld)
            if (manager.getNetwork(pos) == null) {
                manager.setNetwork(pos, Network(world!!, pos, type))
                manager.markForSaving()
            }
        }
    }

    override fun markRemoved() {
        super.markRemoved()
        if (!world!!.isClient) {
            val manager = instance().getNetworkManager(world as ServerWorld)
            val network = manager.getNetwork(pos)
            removedNetwork = network
            manager.removeNetwork(pos)
            manager.markForSaving()
            network!!.onRemoved()
        }
    }

    override val node: RootNetworkNode
        get() = (network as Network).root
    override var redstoneMode: RedstoneMode
        get() = (network as Network).redstoneMode
        set(mode) {
            (network as Network).redstoneMode = mode
        }

    // TODO Replace capability (do we need this?)
//    fun <T> getCapability(@Nonnull cap: Capability<T>, @Nullable direction: Direction?): LazyOptional<T> {
//        if (cap === CapabilityEnergy.ENERGY) {
//            return energyProxyCap.cast()
//        }
//        return if (cap === NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY) {
//            networkNodeProxyCap.cast()
//        } else super.getCapability(cap, direction)
//    }

    companion object {
        val REDSTONE_MODE = createParameter<ControllerTile>()
        val ENERGY_USAGE = TileDataParameter<Double?, ControllerTile?>(0.0, DOUBLE, Function { t: ControllerTile? -> t!!.network.energyUsage })
        val ENERGY_STORED = TileDataParameter<Double?, ControllerTile?>(0.0, DOUBLE, Function { t: ControllerTile? -> t!!.network.energyStorage.getStored(EnergySide.UNKNOWN) })
        val ENERGY_CAPACITY = TileDataParameter<Double?, ControllerTile?>(0.0, DOUBLE, Function { t: ControllerTile? -> t!!.network.energyStorage.maxStoredPower })
//        val NODES: TileDataParameter<List<ClientNode>, ControllerTile> = TileDataParameter<T, E>(RSSerializers.CLIENT_NODE_SERIALIZER, ArrayList<Any>(), Function<E, T> { tile: E -> collectClientNodes(tile) })
        private const val NBT_ENERGY_TYPE = "EnergyType"
//        private fun collectClientNodes(tile: ControllerTile): List<ClientNode> {
//            val nodes: MutableList<ClientNode> = ArrayList()
//            for (node in tile.network.nodeGraph!!.all()!!) {
//                if (node!!.isActive) {
//                    val stack = node.itemStack
//                    if (stack.isEmpty) {
//                        continue
//                    }
//                    val clientNode = ClientNode(stack, 1, node.energyUsage)
//                    if (nodes.contains(clientNode)) {
//                        val other = nodes[nodes.indexOf(clientNode)]
//                        other.amount = other.amount + 1
//                    } else {
//                        nodes.add(clientNode)
//                    }
//                }
//            }
//
//            nodes.sort(java.util.Comparator { a: ClientNode, b: ClientNode -> b.energyUsage.compareTo(a.energyUsage) })
//            return nodes
//        }
    }

    init {
        dataManager.addWatchedParameter(REDSTONE_MODE)
        dataManager.addWatchedParameter(ENERGY_USAGE)
        dataManager.addWatchedParameter(ENERGY_STORED)
        dataManager.addParameter(ENERGY_CAPACITY)
//        dataManager.addParameter(NODES)
        this.type = type
    }

    override fun setStored(amount: Double) {
        network.energyStorage.setStored(amount)
    }

    override fun getMaxStoredPower(): Double {
        return network.energyStorage.maxStoredPower
    }

    override fun getTier(): EnergyTier {
        return network.energyStorage.tier
    }

    override fun getStored(face: EnergySide?): Double {
        return network.energyStorage.getStored(face)
    }
}

@RegisterBlockEntity(RS.ID, ControllerBlock.ID, ["CONTROLLER_BLOCK"])
class NormalControllerTile: ControllerTile(NetworkType.NORMAL, BlockEntityRegistryGenerated.NORMAL_CONTROLLER_TILE)

@RegisterBlockEntity(RS.ID, ControllerBlock.CREATIVE_ID, ["CREATIVE_CONTROLLER_BLOCK"])
class CreativeControllerTile: ControllerTile(NetworkType.CREATIVE, BlockEntityRegistryGenerated.CREATIVE_CONTROLLER_TILE)