package com.refinedmods.refinedstorage.gui.widget.sidebutton

import com.refinedmods.refinedstorage.gui.screen.FilterScreen
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting

class FilterTypeSideButton(override val screen: FilterScreen) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.translate("sidebutton.refinedstorage.type").toString() + "\n" + Formatting.GRAY + I18n.translate("sidebutton.refinedstorage.type." + screen.getType())
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.drawTexture(matrixStack, x, y, 16 * screen.getType(), 128, 16, 16)
    }

    override fun onPress() {
        screen.setType(if (screen.getType() == IType.ITEMS) IType.FLUIDS else IType.ITEMS)
        screen.sendUpdate()
    }

}