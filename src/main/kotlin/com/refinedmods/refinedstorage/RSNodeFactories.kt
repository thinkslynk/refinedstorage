package com.refinedmods.refinedstorage

import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeFactory
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.network.node.ConstructorNetworkNode
import com.refinedmods.refinedstorage.apiimpl.network.node.DestructorNetworkNode
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode
import com.refinedmods.refinedstorage.extensions.CompoundNBT
import net.minecraft.nbt.CompoundTag


object RSNodeFactories {
    fun register() {
//        API.instance().getNetworkNodeRegistry()
//            .add(DiskDriveNetworkNode.ID, { tag, world, pos -> readAndReturn(tag, DiskDriveNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry()
//            .add(CableNetworkNode.ID, { tag, world, pos -> readAndReturn(tag, CableNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry().add(
//            GridNetworkNode.ID,
//            { tag, world, pos -> readAndReturn(tag, GridNetworkNode(world, pos, GridType.NORMAL)) })
//        API.instance().getNetworkNodeRegistry().add(
//            GridNetworkNode.CRAFTING_ID,
//            { tag, world, pos -> readAndReturn(tag, GridNetworkNode(world, pos, GridType.CRAFTING)) })
//        API.instance().getNetworkNodeRegistry().add(
//            GridNetworkNode.PATTERN_ID,
//            { tag, world, pos -> readAndReturn(tag, GridNetworkNode(world, pos, GridType.PATTERN)) })
//        API.instance().getNetworkNodeRegistry().add(
//            GridNetworkNode.FLUID_ID,
//            { tag, world, pos -> readAndReturn(tag, GridNetworkNode(world, pos, GridType.FLUID)) })
//
//        API.instance().getNetworkNodeRegistry().add(
//            StorageNetworkNode.ONE_K_STORAGE_BLOCK_ID,
//            { tag, world, pos -> readAndReturn(tag, StorageNetworkNode(world, pos, ItemStorageType.ONE_K)) })
//        API.instance().getNetworkNodeRegistry().add(
//            StorageNetworkNode.FOUR_K_STORAGE_BLOCK_ID,
//            { tag, world, pos -> readAndReturn(tag, StorageNetworkNode(world, pos, ItemStorageType.FOUR_K)) })
//        API.instance().getNetworkNodeRegistry().add(
//            StorageNetworkNode.SIXTEEN_K_STORAGE_BLOCK_ID,
//            { tag, world, pos -> readAndReturn(tag, StorageNetworkNode(world, pos, ItemStorageType.SIXTEEN_K)) })
//        API.instance().getNetworkNodeRegistry().add(
//            StorageNetworkNode.SIXTY_FOUR_K_STORAGE_BLOCK_ID,
//            { tag, world, pos -> readAndReturn(tag, StorageNetworkNode(world, pos, ItemStorageType.SIXTY_FOUR_K)) })
//        API.instance().getNetworkNodeRegistry().add(
//            StorageNetworkNode.CREATIVE_STORAGE_BLOCK_ID,
//            { tag, world, pos -> readAndReturn(tag, StorageNetworkNode(world, pos, ItemStorageType.CREATIVE)) })
//
//        API.instance().getNetworkNodeRegistry().add(
//            FluidStorageNetworkNode.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK_ID,
//            { tag, world, pos ->
//                readAndReturn(
//                    tag,
//                    FluidStorageNetworkNode(world, pos, FluidStorageType.SIXTY_FOUR_K)
//                )
//            })
//        API.instance().getNetworkNodeRegistry().add(
//            FluidStorageNetworkNode.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK_ID,
//            { tag, world, pos ->
//                readAndReturn(
//                    tag,
//                    FluidStorageNetworkNode(world, pos, FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K)
//                )
//            })
//        API.instance().getNetworkNodeRegistry().add(
//            FluidStorageNetworkNode.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK_ID,
//            { tag, world, pos ->
//                readAndReturn(
//                    tag,
//                    FluidStorageNetworkNode(world, pos, FluidStorageType.THOUSAND_TWENTY_FOUR_K)
//                )
//            })
//        API.instance().getNetworkNodeRegistry().add(
//            FluidStorageNetworkNode.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK_ID,
//            { tag, world, pos ->
//                readAndReturn(
//                    tag,
//                    FluidStorageNetworkNode(world, pos, FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K)
//                )
//            })
//        API.instance().getNetworkNodeRegistry().add(
//            FluidStorageNetworkNode.CREATIVE_FLUID_STORAGE_BLOCK_ID,
//            { tag, world, pos -> readAndReturn(tag, FluidStorageNetworkNode(world, pos, FluidStorageType.CREATIVE)) })
//
//        API.instance().getNetworkNodeRegistry().add(
//            ExternalStorageNetworkNode.ID,
//            { tag, world, pos -> readAndReturn(tag, ExternalStorageNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry()
//            .add(ImporterNetworkNode.ID, { tag, world, pos -> readAndReturn(tag, ImporterNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry()
//            .add(ExporterNetworkNode.ID, { tag, world, pos -> readAndReturn(tag, ExporterNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry().add(
//            NetworkReceiverNetworkNode.ID,
//            { tag, world, pos -> readAndReturn(tag, NetworkReceiverNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry().add(
//            NetworkTransmitterNetworkNode.ID,
//            { tag, world, pos -> readAndReturn(tag, NetworkTransmitterNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry()
//            .add(RelayNetworkNode.ID, { tag, world, pos -> readAndReturn(tag, RelayNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry()
//            .add(DetectorNetworkNode.ID, { tag, world, pos -> readAndReturn(tag, DetectorNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry().add(
//            SecurityManagerNetworkNode.ID,
//            { tag, world, pos -> readAndReturn(tag, SecurityManagerNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry()
//            .add(InterfaceNetworkNode.ID, { tag, world, pos -> readAndReturn(tag, InterfaceNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry().add(
//            FluidInterfaceNetworkNode.ID,
//            { tag, world, pos -> readAndReturn(tag, FluidInterfaceNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry().add(
//            WirelessTransmitterNetworkNode.ID,
//            { tag, world, pos -> readAndReturn(tag, WirelessTransmitterNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry().add(
//            StorageMonitorNetworkNode.ID,
//            { tag, world, pos -> readAndReturn(tag, StorageMonitorNetworkNode(world, pos)) })
        API.networkNodeRegistry.add( ConstructorNetworkNode.ID ) { tag, world, pos ->
            readAndReturn(tag, ConstructorNetworkNode(world, pos))
        }
        API.networkNodeRegistry.add( DestructorNetworkNode.ID ) { tag, world, pos ->
            readAndReturn(tag, DestructorNetworkNode(world, pos))
        }

//        API.instance().getNetworkNodeRegistry().add(
//            DiskManipulatorNetworkNode.ID,
//            { tag, world, pos -> readAndReturn(tag, DiskManipulatorNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry()
//            .add(CrafterNetworkNode.ID, { tag, world, pos -> readAndReturn(tag, CrafterNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry().add(
//            CrafterManagerNetworkNode.ID,
//            { tag, world, pos -> readAndReturn(tag, CrafterManagerNetworkNode(world, pos)) })
//        API.instance().getNetworkNodeRegistry().add(
//            CraftingMonitorNetworkNode.ID,
//            { tag, world, pos -> readAndReturn(tag, CraftingMonitorNetworkNode(world, pos)) })
    }

    private fun readAndReturn(tag: CompoundTag, node: NetworkNode): INetworkNode {
        node.read(tag)
        return node
    }
}