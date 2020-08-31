package com.refinedmods.refinedstorage.screen

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.ConstructorScreenHandler
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ConstructorDropSideButton
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton
import com.refinedmods.refinedstorage.tile.ConstructorTile
import com.refinedmods.refinedstorage.tile.NetworkNodeTile
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class ConstructorScreen(container: ConstructorScreenHandler, inventory: PlayerInventory, title: Text):
        BaseScreen<ConstructorScreenHandler>(container, 211, 137, inventory, title) {
    override fun onPostInit(x: Int, y: Int) {
        addSideButton(RedstoneModeSideButton(this, NetworkNodeTile.REDSTONE_MODE))
        addSideButton(TypeSideButton(this, ConstructorTile.TYPE))
        addSideButton(ExactModeSideButton(this, ConstructorTile.COMPARE))
        addSideButton(ConstructorDropSideButton(this))
    }

    override fun tick(x: Int, y: Int) {}
    override fun renderBackground(matrixStack: MatrixStack, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/constructor.png")
        DrawableHelper.drawTexture(matrixStack, x, y, 0f, 0f, xSize, ySize, 256, 256)
    }


    override fun renderForeground(matrixStack: MatrixStack, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.string)
        renderString(matrixStack, 7, 43, I18n.translate("container.inventory"))
    }
}