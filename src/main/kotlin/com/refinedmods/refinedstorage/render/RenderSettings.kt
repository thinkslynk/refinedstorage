package com.refinedmods.refinedstorage.render

import net.minecraft.util.Formatting


class RenderSettings {
    var primaryColor = 0
        private set
    var secondaryColor = 0
        private set

    fun setColors(primaryColor: Int, secondaryColor: Int) {
        if (primaryColor == -1) {
            this.primaryColor = 4210752
        } else {
            this.primaryColor = primaryColor
        }
        if (secondaryColor == -1) {
            this.secondaryColor = Formatting.WHITE.colorValue!!
        } else {
            this.secondaryColor = secondaryColor
        }
    }

    companion object {
        @JvmField
        val INSTANCE = RenderSettings()
    }

    init {
        setColors(-1, -1)
    }
}