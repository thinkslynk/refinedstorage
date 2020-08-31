package com.refinedmods.refinedstorage.tile.config

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import reborncore.common.fluid.container.FluidInstance
import reborncore.common.util.Tank
import java.util.function.BiConsumer
import java.util.function.Function

interface IWhitelistBlacklist {
    var whitelistBlacklistMode: Int

    companion object {
        fun <T> createParameter(): TileDataParameter<Int, T> where T : BlockEntity, T : INetworkNodeProxy<*>? {
            return TileDataParameter<Int, T>(0, TrackedDataHandlerRegistry.INTEGER,
                    Function { t: T -> (t.node as IWhitelistBlacklist).whitelistBlacklistMode },
                    BiConsumer { t: T, v: Int ->
                        if (v == WHITELIST || v == BLACKLIST) {
                            (t.node as IWhitelistBlacklist).whitelistBlacklistMode = v
                        }
                    }
            )
        }

        fun acceptsItem(filters: Inventory, mode: Int, compare: Int, stack: ItemStack?): Boolean {
            if (mode == WHITELIST) {
                for (i in 0 until filters.size()) {
                    val slot: ItemStack = filters.getStack(i)
                    if (API.comparer.isEqual(slot, stack!!, compare)) {
                        return true
                    }
                }
                return false
            } else if (mode == BLACKLIST) {
                for (i in 0 until filters.size()) {
                    val slot: ItemStack = filters.getStack(i)
                    if (API.comparer.isEqual(slot, stack!!, compare)) {
                        return false
                    }
                }
                return true
            }
            return false
        }

        fun acceptsFluid(filters: Tank, mode: Int, compare: Int, stack: FluidInstance?): Boolean {
            // TODO Fluid
//            if (mode == WHITELIST) {
//                for (i in 0 until filters.getSlots()) {
//                    val slot: FluidInstance? = filters.getFluid(i)
//                    if (!slot.isEmpty() && instance().comparer.isEqual(slot, stack, compare)) {
//                        return true
//                    }
//                }
//                return false
//            } else if (mode == BLACKLIST) {
//                for (i in 0 until filters.getSlots()) {
//                    val slot: FluidInstance? = filters.getFluid(i)
//                    if (!slot.isEmpty() && instance().comparer.isEqual(slot, stack, compare)) {
//                        return false
//                    }
//                }
//                return true
//            }
            return false
        }

        const val WHITELIST = 0
        const val BLACKLIST = 1
    }
}