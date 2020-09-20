package com.refinedmods.refinedstorage.api.network.node

import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Creates a network node.
 *
 * @param tag   the tag on disk
 * @param world the world
 * @param pos   the pos
 * @return the network node
 */
typealias INetworkNodeFactory = (CompoundTag, World, BlockPos) -> INetworkNode