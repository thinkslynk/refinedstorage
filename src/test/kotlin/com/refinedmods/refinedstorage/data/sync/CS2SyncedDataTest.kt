package com.refinedmods.refinedstorage.data.sync

import com.refinedmods.refinedstorage.data.BaseBlockEntityData
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CS2SyncedDataTest {
    private val baseData = BaseBlockEntityData(BlockPos(1,2,3), Direction.EAST)

    private fun createTestBuf(d: BaseBlockEntityData? = null): PacketByteBuf {
        val data = d ?: BaseBlockEntityData(BlockPos(10,20,30), Direction.SOUTH)
        val buf = PacketByteBuf(Unpooled.buffer())
        data.getSerializer().write(buf, data)

        return buf
    }

    @Test
    fun `Listener is notified of changes`() {
        var notified = false
        val listener = { _: BaseBlockEntityData ->
            notified = true
        }

        val syncableClient = C2SSyncedData<BaseBlockEntityData>(
            Identifier("test", "this"),
            false,
            baseData,
            mockk(),
            listener
        )

        // Check that data replacements cause notifications
        syncableClient.data = BaseBlockEntityData(BlockPos(2,3,4), Direction.WEST)
        assert(notified)

        // Check that inner data changes cause notifications
        notified = false
        syncableClient.data.direction = Direction.NORTH
        assert(notified)

        // Check that client packets cause notifications
        notified = false
        syncableClient.accept(mockk{
            every { taskQueue } returns mockk {
                every {  execute(any()) } answers {
                    firstArg<Runnable>().run()
                }
            }
        }, createTestBuf())
        assert(notified)
    }

    @Test
    fun `Only server registers for receiving`() {
        // Check client
        val clientRegistry = mockkObject(ClientSidePacketRegistry.INSTANCE)
        every { ClientSidePacketRegistry.INSTANCE.register(any(), any()) } returns Unit
        verify(exactly=0) {ClientSidePacketRegistry.INSTANCE.register(any(), any())}
        C2SSyncedData(
            Identifier("test", "this"),
            true,
            baseData,
            mockk()
        ).registerClient()
        verify(exactly=0) {ClientSidePacketRegistry.INSTANCE.register(any(), any())}

        // Check server
        val serverRegistry = mockkObject(ServerSidePacketRegistry.INSTANCE)
        every { ServerSidePacketRegistry.INSTANCE.register(any(), any()) } returns Unit
        verify(exactly=0) {ServerSidePacketRegistry.INSTANCE.register(any(), any())}
        C2SSyncedData(
            Identifier("test", "this"),
            false,
            baseData,
            mockk()
        ).registerServer()
        verify(exactly=1) {ServerSidePacketRegistry.INSTANCE.register(any(), any())}
    }

    @Test
    fun `Only server unregisters for receiving`() {
        val mClientRegistry = mockk<ClientSidePacketRegistry>{
            every { unregister(any()) } returns Unit
        }
        val mServerRegistry = mockk<ServerSidePacketRegistry>{
            every { unregister(any()) } returns Unit
        }

        // Check client
        verify(exactly=0) {mClientRegistry.unregister(any())}
        var synced = C2SSyncedData(
            Identifier("test", "this"),
            true,
            baseData,
            mockk()
        )
        var mockedSynced = spyk(synced, recordPrivateCalls = true)
        every { mockedSynced["getClientRegistry"]() } returns mClientRegistry
        mockedSynced.unregisterClient()
        verify(exactly=0) { mClientRegistry.unregister(any())}

        // Check server
        verify(exactly=0) {mServerRegistry.unregister(any())}
        synced = C2SSyncedData(
            Identifier("test", "this"),
            false,
            baseData,
            mockk()
        )
        mockedSynced = spyk(synced, recordPrivateCalls = true)
        every { mockedSynced["getServerRegistry"]() } returns mServerRegistry
        mockedSynced.unregisterServer()
        verify(exactly=1) { mServerRegistry.unregister(any())}
    }

    @Test
    fun `On replace, server is notified`() {
        val sync = C2SSyncedData(
            Identifier("test", "this"),
            true,
            baseData,
            mockk()
        )
        val syncableClient = spyk(sync)
        val mClientRegistry = mockk<ClientSidePacketRegistry>{
            every { sendToServer(any<Identifier>(), any())} returns Unit
        }
        every { syncableClient["getClientRegistry"]() } returns mClientRegistry

        // Check that data replacements sync to server
        syncableClient.data = BaseBlockEntityData(BlockPos(2,3,4), Direction.WEST)
        verify(exactly=1) {mClientRegistry.sendToServer(any(), any<PacketByteBuf>())}
    }

    @Test
    fun `On changed, server is notified`() {
//        val capturedPacket = slot<PacketByteBuf>()
        val sync = C2SSyncedData(
            Identifier("test", "this"),
            true,
            baseData,
            mockk()
        )
        sync.data = sync.data // Have to do this so the spy observer is pointed to the correct object

        val syncableClient = spyk(sync)
        val mClientRegistry = mockk<ClientSidePacketRegistry>{
            every { sendToServer(any<Identifier>(), any<PacketByteBuf>())} returns Unit
        }
        every { syncableClient["getClientRegistry"]() } returns mClientRegistry

        // Check that inner data changes sync to server
        val expectedBuf = PacketByteBuf(Unpooled.buffer())
        syncableClient.data.getSerializer().write(expectedBuf, syncableClient.data)
        syncableClient.data.blockPos = BlockPos(20,30,40)
        verify(exactly=1) {mClientRegistry.sendToServer(any(), any<PacketByteBuf>())}
//        assertEquals(expectedBuf, capturedPacket.captured)

    }
}