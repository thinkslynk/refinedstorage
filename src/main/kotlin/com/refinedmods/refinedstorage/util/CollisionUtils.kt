package com.refinedmods.refinedstorage.util

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape

object CollisionUtils {
    fun isInBounds(shape: VoxelShape, pos: BlockPos?, hit: Vec3d): Boolean {
        val box: Box = shape.boundingBox.offset(pos)
        return hit.x >= box.minX && hit.x <= box.maxX && hit.y >= box.minY && hit.y <= box.maxY && hit.z >= box.minZ && hit.z <= box.maxZ
    }
}