package com.refinedmods.refinedstorage.integration.jei

import com.refinedmods.refinedstorage.container.BaseContainer
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.gui.screen.ingame.HandledScreen
import reborncore.common.fluid.container.FluidInstance

class GuiContainerHandler : IGuiContainerHandler<HandledScreen<*>> {


    fun getIngredientUnderMouse(screen: BaseScreen<*>, mouseX: Double, mouseY: Double): Any? {
        var mouseX = mouseX
        var mouseY = mouseY
        mouseX -= screen.guiLeft
        mouseY -= screen.guiTop
        if (screen is GridScreen) {
            val grid = screen as GridScreen
            if (!grid.searchField.isFocused && grid.isOverSlotArea(mouseX, mouseY)) {
                return if (grid.slotNumber >= 0 && grid.slotNumber < grid.view.stacks.size) grid.view.stacks[grid.slotNumber].ingredient else null
            }
        }
        if (screen.getContainer() is BaseContainer) {
            for (slot in (screen.getContainer() as BaseContainer).getFluidSlots()) {
                val fluidInSlot: FluidInstance = slot.fluidInventory.getFluid(slot.getSlotIndex())
                if (!fluidInSlot.isEmpty() && RenderUtils.inBounds(slot.xPos, slot.yPos, 18, 18, mouseX, mouseY)) {
                    return fluidInSlot
                }
                TileDataParameter
            }
        }
        return null
    }
}