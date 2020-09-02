package com.refinedmods.refinedstorage.config

import com.refinedmods.refinedstorage.RS
import me.sargunvohra.mcmods.autoconfig1u.ConfigData
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry

@Config(name = "client")
class ClientConfig : ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    val grid: Grid = Grid()

    @ConfigEntry.Gui.CollapsibleObject
    val crafterManager: CrafterManager = CrafterManager()

    class CrafterManager {
        @ConfigEntry.Gui.Tooltip
        var maxRowsStretch: Int = Int.MAX_VALUE
    }

    class Grid {
        @ConfigEntry.Gui.Tooltip
        var maxRowsStretch: Int = Integer.MAX_VALUE

        @ConfigEntry.Gui.Tooltip
        var detailedTooltip: Boolean = true

        @ConfigEntry.Gui.Tooltip
        var largeFont: Boolean = false

        @ConfigEntry.Gui.Tooltip
        var preventSortingWhileShiftIsDown: Boolean = true
    }
}