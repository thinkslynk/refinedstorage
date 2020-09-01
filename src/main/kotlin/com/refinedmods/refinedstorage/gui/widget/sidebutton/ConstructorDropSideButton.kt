package com.refinedmods.refinedstorage.gui.widget.sidebutton

import com.refinedmods.refinedstorage.gui.screenhandlers.ConstructorScreenHandler
import com.refinedmods.refinedstorage.gui.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.ConstructorTile
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting

class ConstructorDropSideButton(screen: BaseScreen<ConstructorScreenHandler>) : SideButton(screen) {
    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.drawTexture(matrixStack, x, y, 64 + if (ConstructorTile.DROP.value) 16 else 0, 16, 16, 16)
    }

    override fun getTooltip(): String {
        return I18n.translate("sidebutton.refinedstorage.constructor.drop").toString() + "\n" + Formatting.GRAY + I18n.translate(if (ConstructorTile.DROP.value) "gui.yes" else "gui.no")
    }

    override fun onPress() {
        TileDataManager.setParameter(ConstructorTile.DROP, !ConstructorTile.DROP.value)
    }
}