package com.refinedmods.refinedstorage.apiimpl.network

import com.google.common.collect.Sets
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraph
import com.refinedmods.refinedstorage.api.network.INetworkNodeGraphListener
import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor
import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.util.NetworkUtils
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import java.util.*
import java.util.function.Consumer
import kotlin.collections.HashSet

class NetworkNodeGraph(
        private val network: INetwork
) : INetworkNodeGraph {
    private var nodes: MutableSet<INetworkNode> = Sets.newConcurrentHashSet()
    private val listeners: MutableList<INetworkNodeGraphListener> = LinkedList()
    private val actions: MutableSet<Consumer<INetwork>> = HashSet()
    private var invalidating = false
    override fun invalidate(action: Action, world: World, origin: BlockPos) {
        invalidating = true
        val operator = Operator(action)
        val originNode: INetworkNode? = NetworkUtils.getNodeFromBlockEntity(world.getBlockEntity(origin))
        if (originNode is INetworkNodeVisitor) {
            (originNode as INetworkNodeVisitor).visit(operator)
        }
        var currentVisitor: Visitor
        while (operator.toCheck.poll().also { currentVisitor = it } != null) {
            currentVisitor.visit(operator)
        }
        nodes = operator.foundNodes
        if (action == Action.PERFORM) {
            for (node in operator.newNodes) {
                node.onConnected(network)
            }
            for (node in operator.previousNodes) {
                node.onDisconnected(network)
            }
            actions.forEach { h: Consumer<INetwork> -> h.accept(network) }
            actions.clear()
            if (operator.newNodes.isNotEmpty() || operator.previousNodes.isNotEmpty()) {
                listeners.forEach(Consumer<INetworkNodeGraphListener> { obj: INetworkNodeGraphListener -> obj.onChanged() })
            }
        }
        invalidating = false
    }

    override fun runActionWhenPossible(handler: Consumer<INetwork>) {
        when {
            invalidating -> actions.add(handler)
            else -> handler.accept(network)
        }
    }

    override fun all(): Collection<INetworkNode> {
        return nodes
    }

    override fun addListener(listener: INetworkNodeGraphListener) {
        listeners.add(listener)
    }

    override fun disconnectAll() {
        nodes.forEach(Consumer<INetworkNode> { n: INetworkNode -> n.onDisconnected(network) })
        nodes.clear()
        listeners.forEach(Consumer<INetworkNodeGraphListener> { obj: INetworkNodeGraphListener -> obj.onChanged() })
    }

    val world: World
        get() = network.world

    private fun dropConflictingBlock(world: World, pos: BlockPos) {
        if (network.position != pos) {
            Block.dropStacks(world.getBlockState(pos), world, pos, world.getBlockEntity(pos))
            world.removeBlock(pos, false)
        }
    }

    private inner class Operator(override val action: Action) : INetworkNodeVisitor.Operator {
        val foundNodes: MutableSet<INetworkNode> = Sets.newConcurrentHashSet() // All scanned nodes
        val newNodes: MutableSet<INetworkNode> = Sets.newConcurrentHashSet<INetworkNode>() // All scanned new nodes, that didn't appear in the list before
        val previousNodes: MutableSet<INetworkNode> = Sets.newConcurrentHashSet(nodes) // All unscanned nodes (nodes that were in the previous list, but not in the new list)
        val toCheck: Queue<Visitor> = ArrayDeque()
        override fun apply(world: World, pos: BlockPos, side: Direction) {
            world.getBlockEntity(pos)?.let{tile ->
                val otherNode: INetworkNode? = NetworkUtils.getNodeFromBlockEntity(tile)
                if (otherNode != null) {
                    if (otherNode.network != null && otherNode.network != network) {
                        if (action == Action.PERFORM) {
                            dropConflictingBlock(world, pos)
                        }
                        return
                    }
                    if (foundNodes.add(otherNode)) {
                        if (!nodes.contains(otherNode)) {
                            // We can't let the node connect immediately
                            // We can only let the node connect AFTER the nodes list has changed in the graph
                            // This is so that storage nodes can refresh the item/fluid cache, and the item/fluid cache will notice it then (otherwise not)
                            newNodes.add(otherNode)
                        }
                        previousNodes.remove(otherNode)
                        toCheck.add(Visitor(otherNode, world, pos, side, tile))
                    }
                }
            }
        }
    }

    private class Visitor(
            private val node: INetworkNode, 
            private val world: World, 
            private val pos: BlockPos, 
            private val side: Direction, 
            private val tile: BlockEntity
    ) : INetworkNodeVisitor {
        override fun visit(operator: INetworkNodeVisitor.Operator?) {
            if (node is INetworkNodeVisitor) {
                (node as INetworkNodeVisitor).visit(operator)
            } else {
                for (checkSide in Direction.values()) {
                    if (checkSide != side) {
                        val nodeOnSide: INetworkNode? = NetworkUtils.getNodeFromTile(tile)
                        if (nodeOnSide == node) {
                            operator!!.apply(world, pos.offset(checkSide), checkSide.opposite)
                        }
                    }
                }
            }
        }

    }

}