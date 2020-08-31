package com.refinedmods.refinedstorage.tile.config

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.tile.data.TileDataParameterClientListener
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.inventory.Inventory

interface IType {
    var type: Int
    val itemFilters: Inventory
    val fluidFilters: FluidInventory

    companion object {
        fun <T> createParameter(clientListener: TileDataParameterClientListener<Int>?): TileDataParameter<Int, T> where T : BlockEntity, T : INetworkNodeProxy<*> {
            return TileDataParameter<Int, T>(
                    ITEMS,
                    TrackedDataHandlerRegistry.INTEGER,
                    { t: T? -> (t!!.node as IType).type },
                    { t: T?, v: Int? ->
                        if (v == ITEMS || v == FLUIDS) {
                            (t!!.node as IType).type = v
                        }
                    },
                    clientListener
            )
        }

        fun <T> createParameter(): TileDataParameter<Int, T> where T : BlockEntity, T : INetworkNodeProxy<*> {
            return createParameter(null)
        }

        const val ITEMS = 0
        const val FLUIDS = 1
    }
}