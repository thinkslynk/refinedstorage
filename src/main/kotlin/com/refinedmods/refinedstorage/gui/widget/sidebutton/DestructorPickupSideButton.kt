package com.refinedmods.refinedstorage.gui.widget.sidebutton

import com.refinedmods.refinedstorage.gui.screen.BaseScreen
import com.refinedmods.refinedstorage.gui.screenhandlers.DestructorScreenHandler
import com.refinedmods.refinedstorage.tile.DestructorTile
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting

class DestructorPickupSideButton(screen: BaseScreen<DestructorScreenHandler>) : SideButton(screen) {
    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.drawTexture(matrixStack, x, y, 64 + if (!DestructorTile.PICKUP.value) 16 else 0, 0, 16, 16)
    }

    override fun getTooltip(): String {
        return I18n.translate("sidebutton.refinedstorage.destructor.pickup").toString() +
                "\n" +
                Formatting.GRAY +
                I18n.translate(if (DestructorTile.PICKUP.value) "gui.yes" else "gui.no")
    }

    override fun onPress() {
        TileDataManager.setParameter(DestructorTile.PICKUP, !DestructorTile.PICKUP.value)
    }
}