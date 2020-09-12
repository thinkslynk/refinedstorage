package com.refinedmods.refinedstorage.gui.screen

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.gui.screenhandlers.ConstructorScreenHandler
import com.refinedmods.refinedstorage.gui.widget.sidebutton.ConstructorDropSideButton
import com.refinedmods.refinedstorage.gui.widget.sidebutton.ExactModeSideButton
import com.refinedmods.refinedstorage.gui.widget.sidebutton.RedstoneModeSideButton
import com.refinedmods.refinedstorage.gui.widget.sidebutton.TypeSideButton
import com.refinedmods.refinedstorage.tile.ConstructorTile
import com.refinedmods.refinedstorage.tile.NetworkNodeTile
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

class ConstructorScreen(
        gui: ConstructorScreenHandler,
        player: PlayerEntity,
        title: Text
) : BaseScreen<ConstructorScreenHandler>(gui, player, title) {

        override fun onPostInit(x: Int, y: Int) {
                addSideButton(RedstoneModeSideButton(this, NetworkNodeTile.REDSTONE_MODE))
                addSideButton(TypeSideButton(this, ConstructorTile.TYPE))
                addSideButton(ExactModeSideButton(this, ConstructorTile.COMPARE))
                addSideButton(ConstructorDropSideButton(this))
        }

        override fun tick(x: Int, y: Int) {}
        override fun renderBackground(matrixStack: MatrixStack, x: Int, y: Int, mouseX: Int, mouseY: Int) {
                bindTexture(RS.ID, "gui/constructor.png")
                drawTexture(matrixStack, x, y, 0, 0, 211, 137)
        }
}