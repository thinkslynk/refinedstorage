package com.refinedmods.refinedstorage.screen

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.render.RenderSettings
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.apache.commons.lang3.tuple.Pair
import org.lwjgl.glfw.GLFW

abstract class AmountSpecifyingScreen<T : ScreenHandler>(
        val parent: BaseScreen<T>, container: T,
        width: Int,
        height: Int,
        playerInventory: PlayerInventory,
        title: Text
) : BaseScreen<T>(container, width, height, playerInventory, title) {
    protected var amountField: TextFieldWidget? = null
    protected var okButton: ButtonWidget? = null
    protected var cancelButton: ButtonWidget? = null
    protected abstract val okButtonText: Text
    protected abstract val texture: String
    protected abstract val increments: IntArray
    protected abstract val defaultAmount: Int
    protected abstract fun canAmountGoNegative(): Boolean
    protected abstract val maxAmount: Int
    protected open val amountPos: Pair<Int, Int>
        protected get() = Pair.of(7 + 2, 50 + 1)
    protected open val okCancelPos: Pair<Int?, Int?>?
        protected get() = Pair.of(114, 33)
    protected open val okCancelButtonWidth: Int
        protected get() = 50

    override fun onPostInit(x: Int, y: Int) {
        val pos = okCancelPos
        okButton = addButton(x + pos!!.left!!, y + pos.right!!, okCancelButtonWidth, 20, okButtonText, true, true, ButtonWidget.PressAction { btn -> onOkButtonPressed(hasShiftDown()) })
        cancelButton = addButton(x + pos.left!!, y + pos.right!! + 24, okCancelButtonWidth, 20, TranslatableText("gui.cancel"), true, true, ButtonWidget.PressAction { btn -> close() })


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
        for (i in 0..2) {
            val increment = increments[i]
            var text: Text = Text.of("+$increment")
            if (text.string == "+1000") {
                text = Text.of("+1B")
            }
            addButton(x + xx, y + 20, width, 20, text, true, true, ButtonWidget.PressAction { btn -> onIncrementButtonClicked(increment) })
            xx += width + 3
        }
        xx = 7
        for (i in 0..2) {
            val increment = increments[i]
            var text: Text = Text.of("-$increment")
            if (text.string == "-1000") {
                text = Text.of("-1B")
            }
            addButton(x + xx, y + ySize - 20 - 7, width, 20, text, true, true, ButtonWidget.PressAction { btn -> onIncrementButtonClicked(-increment) })
            xx += width + 3
        }
    }

    override fun keyPressed(key: Int, scanCode: Int, modifiers: Int): Boolean {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            close()
            return true
        }
        if ((key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) && amountField!!.isFocused) {
            onOkButtonPressed(hasShiftDown())
            return true
        }
        return if (amountField!!.keyPressed(key, scanCode, modifiers)) {
            true
        } else super.keyPressed(key, scanCode, modifiers)
    }

    private fun onIncrementButtonClicked(increment: Int) {
        var oldAmount = 0
        try {
            oldAmount = amountField!!.text.toInt()
        } catch (e: NumberFormatException) {
            // NO OP
        }
        var newAmount = increment
        newAmount = if (!canAmountGoNegative()) {
            Math.max(1, (if (oldAmount == 1 && newAmount != 1) 0 else oldAmount) + newAmount)
        } else {
            oldAmount + newAmount
        }
        if (newAmount > maxAmount) {
            newAmount = maxAmount
        }
        amountField!!.text = newAmount.toString()
    }

    override fun tick(x: Int, y: Int) {
        // NO OP
    }

    override fun renderBackground(matrixStack: MatrixStack, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, texture)
        drawTexture(matrixStack, x, y, 0, 0, xSize, ySize)
        amountField!!.renderButton(matrixStack, 0, 0, 0f)
    }

    override fun renderForeground(matrixStack: MatrixStack, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.string)
    }

    protected open fun onOkButtonPressed(shiftDown: Boolean) {
        // NO OP
    }

    fun close() {
        MinecraftClient.getInstance().openScreen(parent)
    }
}