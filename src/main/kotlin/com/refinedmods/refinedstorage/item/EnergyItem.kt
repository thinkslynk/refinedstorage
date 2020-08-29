package com.refinedmods.refinedstorage.item

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.MathHelper
import reborncore.common.util.ItemDurabilityExtensions
import team.reborn.energy.EnergyHolder
import reborncore.common.util.ItemUtils as RCItemUtils

abstract class EnergyItem(
    settings: Settings,
    private val creative: Boolean,
    private val energyCapacity: Int
) : Item(settings), EnergyHolder, ItemDurabilityExtensions {

    override fun getMaxStoredPower(): Double = energyCapacity.toDouble()

    override fun getDurability(stack: ItemStack): Double {
        val energy = RCItemUtils.getPowerForDurabilityBar(stack)
        return 1.0 - energy / maxStoredPower
    }

    override fun getDurabilityColor(stack: ItemStack?): Int {
        val energy = RCItemUtils.getPowerForDurabilityBar(stack)
        return MathHelper.hsvToRgb(
            (energy / maxStoredPower).toFloat().coerceAtLeast(0.0f) / 3.0f,
            1.0f,
            1.0f
        )
    }

    override fun showDurability(stack: ItemStack): Boolean = !creative
}