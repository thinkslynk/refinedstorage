package com.refinedmods.refinedstorage.data

import com.refinedmods.refinedstorage.data.sync.SimpleObserver
import java.lang.ref.WeakReference
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("BaseBlockEntity")
class BaseBlockEntityDataTest {
    @Test
    fun `Observers are notified of changes`(){
        var observed = false
        val observer = object : SimpleObserver {
            override fun onUpdate() {
                observed = true
            }
        }

        val data = BaseBlockEntityData(
            blockPos = BlockPos(1, 2, 3),
            direction = Direction.EAST
        )

        // Test Block position updates
        data.observers.add(WeakReference(observer))
        data.blockPos = BlockPos(2,3,4)
        assert(observed)
        assertEquals(data.blockPos, BlockPos(2, 3, 4))


        // Test direction updates
        observed = false
        data.direction = Direction.WEST
        assert(observed)
        assertEquals(data.direction, Direction.WEST)

    }
}