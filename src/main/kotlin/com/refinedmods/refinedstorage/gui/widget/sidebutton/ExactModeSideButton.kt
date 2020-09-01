package com.refinedmods.refinedstorage.gui.widget.sidebutton

import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.gui.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.NetworkNodeTile
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting

class ExactModeSideButton<T: NetworkNodeTile<*>>(screen: BaseScreen<*>, private val parameter: TileDataParameter<Int, T>) : SideButton(screen) {
    override fun getTooltip(): String {
        var tooltip: String = I18n.translate("sidebutton.refinedstorage.exact_mode").toString() + "\n" + Formatting.GRAY
        if (parameter.value and MASK == MASK) {
            tooltip += I18n.translate("sidebutton.refinedstorage.exact_mode.on")
        } else {
            tooltip += I18n.translate("sidebutton.refinedstorage.exact_mode.off")
        }
        return tooltip
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        val ty = 16 * 12
        val tx = if (parameter.value and MASK == MASK) 0 else 16
        screen.drawTexture(matrixStack, x, y, tx, ty, 16, 16)
    }

    override fun onPress() {
        TileDataManager.setParameter(parameter, parameter.value xor MASK)
    }

    companion object {
        private const val MASK = IComparer.COMPARE_NBT
    }
}