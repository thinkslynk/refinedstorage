package com.refinedmods.refinedstorage.network

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.network.disk.StorageDiskSizeRequestMessage
import com.refinedmods.refinedstorage.network.disk.StorageDiskSizeResponseMessage
import com.refinedmods.refinedstorage.network.tiledata.TileDataParameterMessage
import com.refinedmods.refinedstorage.network.tiledata.TileDataParameterUpdateMessage
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class NetworkHandler {

    companion object {
        //ServerSide Packet IDs (sendToServer)
        val TILE_DATA_PARAMETER_UPDATE_MESSAGE_ID: Identifier = Identifier(RS.ID, "tile_data_parameter_update")
        val STORAGE_DISK_SIZE_REQUEST_MESSAGE_ID: Identifier = Identifier(RS.ID, "storage_disk_size_request")

        //ClientSide Packet IDs (sendTo)
        val TILE_DATA_PARAMETER_MESSAGE_ID: Identifier = Identifier(RS.ID, "tile_data_parameter")
        val STORAGE_DISK_SIZE_RESPONSE_MESSAGE_ID: Identifier = Identifier(RS.ID, "storage_disk_size_response")

        fun registerClient() {
            ClientSidePacketRegistry.INSTANCE.register(TILE_DATA_PARAMETER_MESSAGE_ID, TileDataParameterMessage())
            ClientSidePacketRegistry.INSTANCE.register(STORAGE_DISK_SIZE_RESPONSE_MESSAGE_ID, StorageDiskSizeResponseMessage())
        }

        fun register() {
            ServerSidePacketRegistry.INSTANCE.register(TILE_DATA_PARAMETER_UPDATE_MESSAGE_ID, TileDataParameterUpdateMessage())
            ServerSidePacketRegistry.INSTANCE.register(STORAGE_DISK_SIZE_REQUEST_MESSAGE_ID, StorageDiskSizeRequestMessage())
        }


    }

    private val protocolVersion = 1.toString()
//    private val handler:
//    private val handler: SimpleChannel = NetworkRegistry.ChannelBuilder
//            .named(Identifier(RS.ID, "main_channel"))
//            .clientAcceptedVersions(protocolVersion::equals)
//            .serverAcceptedVersions(protocolVersion::equals)
//            .networkProtocolVersion({ protocolVersion })
//            .simpleChannel()




//        var id = 0
//        handler.registerMessage(id++, StorageDiskSizeRequestMessage::class.java, { message: StorageDiskSizeRequestMessage, buf: PacketByteBuf -> StorageDiskSizeRequestMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> StorageDiskSizeRequestMessage.Companion.decode(buf) }, { message: StorageDiskSizeRequestMessage, ctx: Supplier<NetworkEvent.Context> -> StorageDiskSizeRequestMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, StorageDiskSizeResponseMessage::class.java, { message: StorageDiskSizeResponseMessage, buf: PacketByteBuf -> StorageDiskSizeResponseMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> StorageDiskSizeResponseMessage.Companion.decode(buf) }, { message: StorageDiskSizeResponseMessage, ctx: Supplier<NetworkEvent.Context> -> StorageDiskSizeResponseMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, FilterUpdateMessage::class.java, { message: FilterUpdateMessage, buf: PacketByteBuf -> FilterUpdateMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> FilterUpdateMessage.Companion.decode(buf) }, { message: FilterUpdateMessage, ctx: Supplier<NetworkEvent.Context> -> FilterUpdateMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, FluidFilterSlotUpdateMessage::class.java, { message: FluidFilterSlotUpdateMessage, buf: PacketByteBuf -> FluidFilterSlotUpdateMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> FluidFilterSlotUpdateMessage.Companion.decode(buf) }, { message: FluidFilterSlotUpdateMessage, ctx: Supplier<NetworkEvent.Context> -> FluidFilterSlotUpdateMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, TileDataParameterMessage::class.java, { message: TileDataParameterMessage, buf: PacketByteBuf -> TileDataParameterMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> TileDataParameterMessage.Companion.decode(buf) }, { message: TileDataParameterMessage?, ctx: Supplier<NetworkEvent.Context> -> TileDataParameterMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, TileDataParameterUpdateMessage::class.java, { message: TileDataParameterUpdateMessage, buf: PacketByteBuf -> TileDataParameterUpdateMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> TileDataParameterUpdateMessage.Companion.decode(buf) }, { message: TileDataParameterUpdateMessage, ctx: Supplier<NetworkEvent.Context> -> TileDataParameterUpdateMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, GridItemUpdateMessage::class.java, { message: GridItemUpdateMessage, buf: PacketByteBuf -> GridItemUpdateMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> GridItemUpdateMessage.Companion.decode(buf) }, { message: GridItemUpdateMessage, ctx: Supplier<NetworkEvent.Context> -> GridItemUpdateMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, GridItemDeltaMessage::class.java, { message: GridItemDeltaMessage, buf: PacketByteBuf -> GridItemDeltaMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> GridItemDeltaMessage.Companion.decode(buf) }, { message: GridItemDeltaMessage, ctx: Supplier<NetworkEvent.Context> -> GridItemDeltaMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, GridItemPullMessage::class.java, { message: GridItemPullMessage, buf: PacketByteBuf -> GridItemPullMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> GridItemPullMessage.Companion.decode(buf) }, { message: GridItemPullMessage, ctx: Supplier<NetworkEvent.Context> -> GridItemPullMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, GridItemInsertHeldMessage::class.java, { message: GridItemInsertHeldMessage, buf: PacketByteBuf -> GridItemInsertHeldMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> GridItemInsertHeldMessage.Companion.decode(buf) }, { message: GridItemInsertHeldMessage, ctx: Supplier<NetworkEvent.Context> -> GridItemInsertHeldMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, GridClearMessage::class.java, { obj: GridClearMessage?, message: GridClearMessage?, buf: PacketByteBuf? -> GridClearMessage.encode(message, buf) }, { obj: GridClearMessage?, buf: PacketByteBuf? -> GridClearMessage.decode(buf) }, { obj: GridClearMessage?, message: GridClearMessage?, ctx: Supplier<NetworkEvent.Context> -> GridClearMessage.handle(message, ctx) })
//        handler.registerMessage(id++, GridPatternCreateMessage::class.java, { message: GridPatternCreateMessage, buf: PacketByteBuf -> GridPatternCreateMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> GridPatternCreateMessage.Companion.decode(buf) }, { message: GridPatternCreateMessage, ctx: Supplier<NetworkEvent.Context> -> GridPatternCreateMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, SetFilterSlotMessage::class.java, { message: SetFilterSlotMessage, buf: PacketByteBuf -> SetFilterSlotMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> SetFilterSlotMessage.Companion.decode(buf) }, { message: SetFilterSlotMessage, ctx: Supplier<NetworkEvent.Context> -> SetFilterSlotMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, SetFluidFilterSlotMessage::class.java, { message: SetFluidFilterSlotMessage, buf: PacketByteBuf -> SetFluidFilterSlotMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> SetFluidFilterSlotMessage.Companion.decode(buf) }, { message: SetFluidFilterSlotMessage, ctx: Supplier<NetworkEvent.Context> -> SetFluidFilterSlotMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, GridFluidUpdateMessage::class.java, { message: GridFluidUpdateMessage, buf: PacketByteBuf -> GridFluidUpdateMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> GridFluidUpdateMessage.Companion.decode(buf) }, { message: GridFluidUpdateMessage, ctx: Supplier<NetworkEvent.Context> -> GridFluidUpdateMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, GridFluidDeltaMessage::class.java, { message: GridFluidDeltaMessage, buf: PacketByteBuf -> GridFluidDeltaMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> GridFluidDeltaMessage.Companion.decode(buf) }, { message: GridFluidDeltaMessage, ctx: Supplier<NetworkEvent.Context> -> GridFluidDeltaMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, GridFluidInsertHeldMessage::class.java, { obj: GridFluidInsertHeldMessage?, message: GridFluidInsertHeldMessage?, buf: PacketByteBuf? -> GridFluidInsertHeldMessage.encode(message, buf) }, { obj: GridFluidInsertHeldMessage?, buf: PacketByteBuf? -> GridFluidInsertHeldMessage.decode(buf) }, { obj: GridFluidInsertHeldMessage?, message: GridFluidInsertHeldMessage?, ctx: Supplier<NetworkEvent.Context> -> GridFluidInsertHeldMessage.handle(message, ctx) })
//        handler.registerMessage(id++, GridFluidPullMessage::class.java, { message: GridFluidPullMessage, buf: PacketByteBuf -> GridFluidPullMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> GridFluidPullMessage.Companion.decode(buf) }, { message: GridFluidPullMessage, ctx: Supplier<NetworkEvent.Context> -> GridFluidPullMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, GridTransferMessage::class.java, { message: GridTransferMessage, buf: PacketByteBuf -> GridTransferMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> GridTransferMessage.Companion.decode(buf) }, { message: GridTransferMessage, ctx: Supplier<NetworkEvent.Context> -> GridTransferMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, GridProcessingTransferMessage::class.java, { message: GridProcessingTransferMessage, buf: PacketByteBuf -> GridProcessingTransferMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> GridProcessingTransferMessage.Companion.decode(buf) }, { message: GridProcessingTransferMessage, ctx: Supplier<NetworkEvent.Context> -> GridProcessingTransferMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, SecurityManagerUpdateMessage::class.java, { message: SecurityManagerUpdateMessage, buf: PacketByteBuf -> SecurityManagerUpdateMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> SecurityManagerUpdateMessage.Companion.decode(buf) }, { message: SecurityManagerUpdateMessage, ctx: Supplier<NetworkEvent.Context> -> SecurityManagerUpdateMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, WirelessGridSettingsUpdateMessage::class.java, { message: WirelessGridSettingsUpdateMessage, buf: PacketByteBuf -> WirelessGridSettingsUpdateMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> WirelessGridSettingsUpdateMessage.Companion.decode(buf) }, { message: WirelessGridSettingsUpdateMessage, ctx: Supplier<NetworkEvent.Context> -> WirelessGridSettingsUpdateMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, OpenNetworkItemMessage::class.java, { message: OpenNetworkItemMessage, buf: PacketByteBuf -> OpenNetworkItemMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> OpenNetworkItemMessage.Companion.decode(buf) }, { message: OpenNetworkItemMessage, ctx: Supplier<NetworkEvent.Context> -> OpenNetworkItemMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, WirelessFluidGridSettingsUpdateMessage::class.java, { message: WirelessFluidGridSettingsUpdateMessage, buf: PacketByteBuf -> WirelessFluidGridSettingsUpdateMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> WirelessFluidGridSettingsUpdateMessage.Companion.decode(buf) }, { message: WirelessFluidGridSettingsUpdateMessage, ctx: Supplier<NetworkEvent.Context> -> WirelessFluidGridSettingsUpdateMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, PortableGridSettingsUpdateMessage::class.java, { message: PortableGridSettingsUpdateMessage, buf: PacketByteBuf -> PortableGridSettingsUpdateMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> PortableGridSettingsUpdateMessage.Companion.decode(buf) }, { message: PortableGridSettingsUpdateMessage, ctx: Supplier<NetworkEvent.Context> -> PortableGridSettingsUpdateMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, PortableGridItemUpdateMessage::class.java, { message: PortableGridItemUpdateMessage, buf: PacketByteBuf -> PortableGridItemUpdateMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> PortableGridItemUpdateMessage.Companion.decode(buf) }, { message: PortableGridItemUpdateMessage, ctx: Supplier<NetworkEvent.Context> -> PortableGridItemUpdateMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, PortableGridItemDeltaMessage::class.java, { message: PortableGridItemDeltaMessage, buf: PacketByteBuf -> PortableGridItemDeltaMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> PortableGridItemDeltaMessage.Companion.decode(buf) }, { message: PortableGridItemDeltaMessage, ctx: Supplier<NetworkEvent.Context> -> PortableGridItemDeltaMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, PortableGridFluidUpdateMessage::class.java, { message: PortableGridFluidUpdateMessage, buf: PacketByteBuf -> PortableGridFluidUpdateMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> PortableGridFluidUpdateMessage.Companion.decode(buf) }, { message: PortableGridFluidUpdateMessage, ctx: Supplier<NetworkEvent.Context> -> PortableGridFluidUpdateMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, PortableGridFluidDeltaMessage::class.java, { message: PortableGridFluidDeltaMessage, buf: PacketByteBuf -> PortableGridFluidDeltaMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> PortableGridFluidDeltaMessage.Companion.decode(buf) }, { message: PortableGridFluidDeltaMessage, ctx: Supplier<NetworkEvent.Context> -> PortableGridFluidDeltaMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, GridCraftingPreviewRequestMessage::class.java, { message: GridCraftingPreviewRequestMessage, buf: PacketByteBuf -> GridCraftingPreviewRequestMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> GridCraftingPreviewRequestMessage.Companion.decode(buf) }, { message: GridCraftingPreviewRequestMessage, ctx: Supplier<NetworkEvent.Context> -> GridCraftingPreviewRequestMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, GridCraftingPreviewResponseMessage::class.java, { message: GridCraftingPreviewResponseMessage, buf: PacketByteBuf -> GridCraftingPreviewResponseMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> GridCraftingPreviewResponseMessage.Companion.decode(buf) }, { message: GridCraftingPreviewResponseMessage, ctx: Supplier<NetworkEvent.Context> -> GridCraftingPreviewResponseMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, GridCraftingStartRequestMessage::class.java, { message: GridCraftingStartRequestMessage, buf: PacketByteBuf -> GridCraftingStartRequestMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> GridCraftingStartRequestMessage.Companion.decode(buf) }, { message: GridCraftingStartRequestMessage, ctx: Supplier<NetworkEvent.Context> -> GridCraftingStartRequestMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, GridCraftingStartResponseMessage::class.java, { obj: GridCraftingStartResponseMessage?, message: GridCraftingStartResponseMessage?, buf: PacketByteBuf? -> GridCraftingStartResponseMessage.encode(message, buf) }, { obj: GridCraftingStartResponseMessage?, buf: PacketByteBuf? -> GridCraftingStartResponseMessage.decode(buf) }, { obj: GridCraftingStartResponseMessage?, message: GridCraftingStartResponseMessage?, ctx: Supplier<NetworkEvent.Context> -> GridCraftingStartResponseMessage.handle(message, ctx) })
//        handler.registerMessage(id++, CraftingMonitorUpdateMessage::class.java, { message: CraftingMonitorUpdateMessage, buf: PacketByteBuf -> CraftingMonitorUpdateMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> CraftingMonitorUpdateMessage.Companion.decode(buf) }, { message: CraftingMonitorUpdateMessage, ctx: Supplier<NetworkEvent.Context> -> CraftingMonitorUpdateMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, CraftingMonitorCancelMessage::class.java, { message: CraftingMonitorCancelMessage, buf: PacketByteBuf -> CraftingMonitorCancelMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> CraftingMonitorCancelMessage.Companion.decode(buf) }, { message: CraftingMonitorCancelMessage, ctx: Supplier<NetworkEvent.Context> -> CraftingMonitorCancelMessage.Companion.handle(message, ctx) })
//        handler.registerMessage(id++, WirelessCraftingMonitorSettingsUpdateMessage::class.java, { message: WirelessCraftingMonitorSettingsUpdateMessage, buf: PacketByteBuf -> WirelessCraftingMonitorSettingsUpdateMessage.Companion.encode(message, buf) }, { buf: PacketByteBuf -> WirelessCraftingMonitorSettingsUpdateMessage.Companion.decode(buf) }, { message: WirelessCraftingMonitorSettingsUpdateMessage, ctx: Supplier<NetworkEvent.Context> -> WirelessCraftingMonitorSettingsUpdateMessage.Companion.handle(message, ctx) })

    fun sendToServer(message: Any?) {
//        handler.sendToServer(message)
    }

    fun sendTo(player: ServerPlayerEntity, message: Any?) {
//        if (player !is FakePlayer) {
//            handler.sendTo(message, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT)
//        }
    }
}