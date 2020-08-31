package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.NetworkNodeTile
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting

class RedstoneModeSideButton<T: NetworkNodeTile<*, T>>(screen: BaseScreen<*>, private val parameter: TileDataParameter<Int, T>) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.translate("sidebutton.refinedstorage.redstone_mode").toString() + "\n" + Formatting.GRAY + I18n.translate("sidebutton.refinedstorage.redstone_mode." + parameter.value)
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.drawTexture(matrixStack, x, y, parameter.value * 16, 0, 16, 16)
    }

    override fun onPress() {
        TileDataManager.setParameter(parameter, parameter.value + 1)
    }
}