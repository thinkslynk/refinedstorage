package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.api.network.security.Permission
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode
import com.refinedmods.refinedstorage.util.NetworkUtils
import com.refinedmods.refinedstorage.util.WorldUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World

abstract class NetworkNodeBlock(
        settings: Settings
):
        BaseBlock(settings)
{

    // TODO Network
    override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos, notify: Boolean) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify)
        if (!world.isClient) {
            val node = API.instance().getNetworkNodeManager(world as ServerWorld).getNode(pos)
            if (node is NetworkNode) {
                node.setRedstonePowered(world.isReceivingRedstonePower(pos))
            }
        }
    }

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

    override fun onDirectionChanged(world: World, pos: BlockPos, newDirection: Direction) {
        super.onDirectionChanged(world, pos, newDirection)

        world.getBlockEntity(pos)?.let {
            if (it is INetworkNodeProxy<*>) {
                val node = (it as INetworkNodeProxy<*>).node
                if (node is NetworkNode) {
                    node.onDirectionChanged(newDirection)
                }
            }
        }
    }

    private fun discoverNode(world: World, pos: BlockPos) {
        Direction.values().forEach { facing ->
            NetworkUtils.getNodeFromTile(world.getBlockEntity(pos.offset(facing)))?.network?.let{
                it.nodeGraph.invalidate(Action.PERFORM, it.world, it.position)
                return
            }
        }
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)

        if(!world.isClient && placer is PlayerEntity) {
            NetworkUtils.getNodeFromTile(world.getBlockEntity(pos))?.let { placed ->
                discoverNode(world, pos)
                placed.owner = placer.gameProfile.id
                for (facing in Direction.values()) {
                    NetworkUtils.getNodeFromTile(world.getBlockEntity(pos.offset(facing)))?.network?.let {
                        network ->
                        if (!network.securityManager.hasPermission(Permission.BUILD, placer)) {
                            WorldUtils.sendNoPermissionMessage(placer)
                            world.breakBlock(pos, true)
                        }
                    }
                }
            }
        }
    }

    override fun calcBlockBreakingDelta(state: BlockState, player: PlayerEntity, world: BlockView, pos: BlockPos): Float {
        NetworkUtils.getNodeFromTile(world.getBlockEntity(pos))?.network?.let { network ->
            if (!network.securityManager.hasPermission(Permission.BUILD, player)) {
                WorldUtils.sendNoPermissionMessage(player)
                return 0f
            }
        }

        return super.calcBlockBreakingDelta(state, player, world, pos)
    }
    companion object {
        @JvmField
        val CONNECTED: BooleanProperty = BooleanProperty.of("connected")
    }

}