package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.NetworkNodeTile
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting

class TypeSideButton<T: NetworkNodeTile<*>>(screen: BaseScreen<*>, private val type: TileDataParameter<Int, T>) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.translate("sidebutton.refinedstorage.type").toString() + "\n" + Formatting.GRAY + I18n.translate("sidebutton.refinedstorage.type." + type.value)
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.drawTexture(matrixStack, x, y, 16 * type.value!!, 128, 16, 16)
    }

    override fun onPress() {
        TileDataManager.setParameter(type, if (type.value == IType.ITEMS) IType.FLUIDS else IType.ITEMS)
    }
}