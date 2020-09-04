package com.refinedmods.refinedstorage.data.sync

import com.refinedmods.refinedstorage.data.BaseBlockEntityData
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.netty.buffer.Unpooled
import java.lang.ref.WeakReference
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class C2SSyncedDataTest {
    private val updateTracker = Helpers.UpdateTracker()
    private lateinit var clientData: C2SSyncedData<BaseBlockEntityData>
    private lateinit var serverData: C2SSyncedData<BaseBlockEntityData>
    private lateinit var clientRegistry: ClientSidePacketRegistry
    private lateinit var serverRegistry: ServerSidePacketRegistry

    @BeforeEach
    fun setup() {
        clientData = spyk(Helpers.getC2SData(CLIENT), recordPrivateCalls = true)
        serverData = spyk(Helpers.getC2SData(SERVER), recordPrivateCalls = true)

        clientData.observers.add(WeakReference(updateTracker))
        serverData.observers.add(WeakReference(updateTracker))

        clientRegistry = Helpers.getClientRegistry()
        serverRegistry = Helpers.getServerRegistry()

        every { clientData["getClientRegistry"]() } returns clientRegistry
        every { clientData["getServerRegistry"]() } returns serverRegistry

        every { serverData["getClientRegistry"]() } returns clientRegistry
        every { serverData["getServerRegistry"]() } returns serverRegistry

        clientData.data = Helpers.getData()
        serverData.data = Helpers.getData()
    }

    private fun createTestBuf(d: BaseBlockEntityData? = null): PacketByteBuf {
        val data = d ?: BaseBlockEntityData(BlockPos(10,20,30), Direction.SOUTH)
        val buf = PacketByteBuf(Unpooled.buffer())
        data.getSerializer().write(buf, data)

        return buf
    }

    @Test
    fun `Listener is notified of changes`() {
        // Check that data replacements cause notifications
        serverData.data = BaseBlockEntityData(BlockPos(2,3,4), Direction.WEST)
        assert(updateTracker.notified)

        // Check that inner data changes cause notifications
        serverData.data.direction = Direction.NORTH
        assert(updateTracker.notified)

        // Check that packets cause notifications
        serverData.accept(mockk{
            every { taskQueue } returns mockk {
                every {  execute(any()) } answers {
                    firstArg<Runnable>().run()
                }
            }
        }, createTestBuf())
        assert(updateTracker.notified)
    }

    @Test
    fun `Only client registers for receiving`() {
        verify(exactly=0) {clientRegistry.register(any(), any())}
        verify(exactly=0) {serverRegistry.register(any(), any())}
        clientData.register()
        verify(exactly=0) {clientRegistry.register(any(), any())}
        verify(exactly=0) {serverRegistry.register(any(), any())}
        serverData.register()
        verify(exactly=0) {clientRegistry.register(any(), any())}
        verify(exactly=1) {serverRegistry.register(any(), any())}
    }

    @Test
    fun `Only server unregisters for receiving`() {
        verify(exactly=0) {clientRegistry.unregister(any())}
        verify(exactly=0) {serverRegistry.unregister(any())}
        clientData.unregister()
        verify(exactly=0) {clientRegistry.unregister(any())}
        verify(exactly=0) {serverRegistry.unregister(any())}
        serverData.unregister()
        verify(exactly=0) {clientRegistry.unregister(any())}
        verify(exactly=1) {serverRegistry.unregister(any())}
    }

    @Test
    fun `On replace, server is notified`() {

        // Check that data replacements sync to server
        verify(exactly=1) {clientRegistry.sendToServer(any(), any<PacketByteBuf>())}
        clientData.data = BaseBlockEntityData(BlockPos(2,3,4), Direction.WEST)
        verify(exactly=2) {clientRegistry.sendToServer(any(), any<PacketByteBuf>())}
    }

    @Test
    fun `On changed, server is notified`() {
        // Check that inner data changes sync to server
        verify(exactly=1) {clientRegistry.sendToServer(any(), any<PacketByteBuf>())}
        clientData.data.blockPos = BlockPos(20,30,40)
        verify(exactly=2) {clientRegistry.sendToServer(any(), any<PacketByteBuf>())}
    }
}