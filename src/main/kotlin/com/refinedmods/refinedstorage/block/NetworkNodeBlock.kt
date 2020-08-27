package com.refinedmods.refinedstorage.block

//import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
//import com.refinedmods.refinedstorage.apiimpl.API
//import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode
import com.refinedmods.refinedstorage.RS
//import com.refinedmods.refinedstorage.tile.NetworkNodeTile
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty

abstract class NetworkNodeBlock(
        settings: Settings,
        val hasConnected: Boolean
) : BaseBlock(settings) {

    // TODO Network
//    override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos, notify: Boolean) {
//        super.neighborUpdate(state, world, pos, block, fromPos, notify)
//        if (!world.isClient) {
//            val node = API.instance().getNetworkNodeManager(world as ServerWorld).getNode(pos)
//            if (node is NetworkNode) {
//                node.setRedstonePowered(world.isReceivingRedstonePower(pos))
//            }
//        }
//    }
//
//    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
//        if (state.block !== newState.block) {
//            // Different block, drop inventory
//            world.getBlockEntity(pos)?.let { entity ->
//                if (entity is NetworkNodeTile<*>) {
//                    entity.node.drops?.let { inventory ->
//                        inventory.drop(world, pos)
//                    }
//                }
//            }
//
//        }
//
//        // Call onReplaced after the drops check so the tile still exists
//        super.onStateReplaced(state, world, pos, newState, moved)
//    }

//    override fun onDirectionChanged(world: World, pos: BlockPos, newDirection: Direction) {
//        super.onDirectionChanged(world, pos, newDirection)
//
//        world.getBlockEntity(pos)?.let {
//            if (it is INetworkNodeProxy<*>) {
//                val node = (it as INetworkNodeProxy<*>).node
//                if (node is NetworkNode) {
//                    node.onDirectionChanged(newDirection)
//                }
//            }
//        }
//    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        RS.log.info("appendProperties: hasConnected: $hasConnected")
        if (hasConnected) {
            builder.add(CONNECTED)
        }
    }

//    fun hasBlockEntity(state: BlockState): Boolean {
//        return connected
//    }
//

    companion object {
        @JvmField
        val CONNECTED: BooleanProperty = BooleanProperty.of("connected")
    }

    init {
        RS.log.info("init: hasConnected: $hasConnected")
        if (hasConnected) {
            this.defaultState = this.defaultState.with(CONNECTED, false)
        }
    }
}