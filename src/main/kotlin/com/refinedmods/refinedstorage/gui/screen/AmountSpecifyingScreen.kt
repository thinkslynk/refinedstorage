package com.refinedmods.refinedstorage.gui.screen

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.render.RenderSettings
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import kotlin.math.min
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.apache.commons.lang3.tuple.Pair
import org.lwjgl.glfw.GLFW

abstract class AmountSpecifyingScreen<T : SyncedGuiDescription>(
    val parent: BaseScreen<T>,
    container: T,
    private val xSize: Int,
    private val ySize: Int,
    player: PlayerEntity,
    title: Text
) : BaseScreen<T>(container, player, title) {
    protected lateinit var amountField: TextFieldWidget
    protected lateinit var okButton: ButtonWidget
    protected lateinit var cancelButton: ButtonWidget
    protected abstract val okButtonText: Text
    protected abstract val texture: String
    protected abstract val increments: IntArray
    protected abstract val defaultAmount: Int
    protected abstract fun canAmountGoNegative(): Boolean
    protected abstract val maxAmount: Int
    protected open val amountPos: Pair<Int, Int> = Pair.of(7 + 2, 50 + 1)
    protected open val okCancelPos: Pair<Int, Int> = Pair.of(114, 33)
    protected open val okCancelButtonWidth: Int = 50

    init {
        player.inventory.offHand
        player.inventory.mainHandStack
    }

    override fun onPostInit(x: Int, y: Int) {
        okButton = addButton(x + okCancelPos.left, y + okCancelPos.right, okCancelButtonWidth, 20, okButtonText,
            enabled = true,
            visible = true,
            onPress = ButtonWidget.PressAction { onOkButtonPressed(hasShiftDown()) })
        cancelButton = addButton(x + okCancelPos.left, y + okCancelPos.right + 24, okCancelButtonWidth, 20, TranslatableText("gui.cancel"),
            enabled = true,
            visible = true,
            onPress = ButtonWidget.PressAction { close() })


        TextFieldWidget(textRenderer, x + amountPos.left, y + amountPos.right, 69 - 6, textRenderer.fontHeight, Text.of("")).run {
            amountField = this
//            this.setEnableBackgroundDrawing(false) // TODO How to change background drawing?
            this.isVisible = true
            this.text = defaultAmount.toString()
            this.setEditableColor(RenderSettings.INSTANCE.secondaryColor) // TODO is this correct?
            this.setUneditableColor(RenderSettings.INSTANCE.secondaryColor) // TODO is this correct?
            this.setFocusUnlocked(false)
            this.changeFocus(true)
            addButton(this) // TODO ?
        }

        focused = this

        val increments = increments
        var xx = 7
        val width = 30
        (0..2).forEach { i ->
            val increment = increments[i]
            var text: Text = Text.of("+$increment")
            if (text.string == "+1000") {
                text = Text.of("+1B")
            }
            addButton(x + xx, y + 20, width, 20, text,
                enabled = true,
                visible = true,
                onPress = ButtonWidget.PressAction { onIncrementButtonClicked(increment) })
            xx += width + 3
        }
        xx = 7
        (0..2).forEach { i ->
            val increment = increments[i]
            var text: Text = Text.of("-$increment")
            if (text.string == "-1000") {
                text = Text.of("-1B")
            }
            addButton(x + xx, y + ySize - 20 - 7, width, 20, text,
                enabled = true,
                visible = true,
                onPress = ButtonWidget.PressAction { onIncrementButtonClicked(-increment) })
            xx += width + 3
        }
    }

    override fun keyPressed(key: Int, scanCode: Int, modifiers: Int): Boolean {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            close()
            return true
        }
        if ((key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) && amountField.isFocused) {
            onOkButtonPressed(hasShiftDown())
            return true
        }
        return if (amountField.keyPressed(key, scanCode, modifiers)) {
            true
        } else super.keyPressed(key, scanCode, modifiers)
    }

    private fun onIncrementButtonClicked(increment: Int) {
        val oldAmount = amountField.text.toIntOrNull() ?: 0
        val newAmount = if (canAmountGoNegative())
            oldAmount + increment
        else
            1.coerceAtLeast((if (oldAmount == 1 && increment != 1) 0 else oldAmount) + increment)

        amountField.text = min(newAmount, maxAmount).toString()
    }

    override fun renderBackground(matrixStack: MatrixStack, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, texture)
        drawTexture(matrixStack, x, y, 0, 0, xSize, ySize)
        amountField.renderButton(matrixStack, 0, 0, 0f)
    }

    override fun renderForeground(matrixStack: MatrixStack, mouseX: Int, mouseY: Int) {
        textRenderer.draw(matrixStack, title, 7f, 7f, RenderSettings.INSTANCE.secondaryColor)
    }

    open fun onOkButtonPressed(shiftDown: Boolean) {}

    fun close() = MinecraftClient.getInstance().openScreen(parent)
}