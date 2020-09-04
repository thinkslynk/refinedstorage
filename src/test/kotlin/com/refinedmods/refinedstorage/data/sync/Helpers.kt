package com.refinedmods.refinedstorage.data.sync

import com.refinedmods.refinedstorage.data.BaseBlockEntityData
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

const val CLIENT = true
const val SERVER = false

object Helpers {
    class UpdateTracker: SimpleObserver{
        var notified: Boolean = false
        get() {
            val ret = field
            field = false
            return ret
        }
        override fun onUpdate(){
            notified = true
        }




    }
    fun getData(): BaseBlockEntityData = BaseBlockEntityData(BlockPos(1,2,3), Direction.EAST)

    fun getBiData(isClient: Boolean): BiSyncedData<BaseBlockEntityData> =
        BiSyncedData(
            Identifier("test", "this"),
            isClient,
            getData(),
            mockk()
        )

    fun getC2SData(isClient: Boolean): C2SSyncedData<BaseBlockEntityData> =
        C2SSyncedData(
            Identifier("test", "this"),
            isClient,
            getData(),
            mockk()
        )

    fun getS2CData(isClient: Boolean): S2CSyncedData<BaseBlockEntityData> =
        S2CSyncedData(
            Identifier("test", "this"),
            isClient,
            getData(),
            listOf(mockk<ServerPlayerEntity>())
        )

    fun getClientRegistry(): ClientSidePacketRegistry{
        return mockk {
            every { register(any(), any()) } returns Unit
            every { unregister(any()) } returns Unit
            every { sendToServer(any<Identifier>(), any()) } returns Unit
        }
    }

    fun getServerRegistry(): ServerSidePacketRegistry{
        return mockk {
            every { register(any(), any()) } returns Unit
            every { unregister(any()) } returns Unit
            every { sendToPlayer(any(), any(), any<PacketByteBuf>()) } returns Unit
        }
    }
}