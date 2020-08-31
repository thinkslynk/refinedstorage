package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.screen.BaseScreen
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting
import java.util.function.Consumer
import java.util.function.Supplier

class GridSizeSideButton(screen: BaseScreen<*>, private val sizeSupplier: Supplier<Int>, private val listener: Consumer<Int?>) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.translate("sidebutton.refinedstorage.grid.size").toString() + "\n" + Formatting.GRAY + I18n.translate("sidebutton.refinedstorage.grid.size." + sizeSupplier.get())
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        val size = sizeSupplier.get()
        var tx = 0
        when (size) {
            IGrid.SIZE_STRETCH -> tx = 48
            IGrid.SIZE_SMALL -> tx = 0
            IGrid.SIZE_MEDIUM -> tx = 16
            IGrid.SIZE_LARGE -> tx = 32
        }
        screen.drawTexture(matrixStack, x, y, 64 + tx, 64, 16, 16)
    }

    override fun onPress() {
        var size = sizeSupplier.get()
        when (size) {
            IGrid.SIZE_STRETCH -> size = IGrid.SIZE_SMALL
            IGrid.SIZE_SMALL -> size = IGrid.SIZE_MEDIUM
            IGrid.SIZE_MEDIUM -> size = IGrid.SIZE_LARGE
            IGrid.SIZE_LARGE -> size = IGrid.SIZE_STRETCH
        }
        listener.accept(size)
    }
}