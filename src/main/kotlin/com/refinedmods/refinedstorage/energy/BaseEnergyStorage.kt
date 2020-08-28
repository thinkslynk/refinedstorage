package com.refinedmods.refinedstorage.energy

import team.reborn.energy.EnergySide
import team.reborn.energy.EnergyStorage
import team.reborn.energy.EnergyTier
import kotlin.math.min

open class BaseEnergyStorage(protected var capacity: Double, protected var maxReceive: Double, protected var maxExtract: Double) : EnergyStorage {
    var energy: Double = 0.0

    //     @Volatile: Impl from EnergyStorage#extractEnergy, without the canExtract check
    fun extractEnergyBypassCanExtract(maxExtract: Double, simulate: Boolean) {
        val energyExtracted = min(this.energy, maxExtract)
        if (!simulate) {
            this.energy -= energyExtracted
        }
    }

    override fun setStored(amount: Double) {
        this.energy = amount
    }

    override fun getMaxStoredPower(): Double {
        return capacity
    }

    override fun getTier(): EnergyTier {
        TODO("Not yet implemented")
    }

    override fun getStored(face: EnergySide): Double {
        return energy
    }
}