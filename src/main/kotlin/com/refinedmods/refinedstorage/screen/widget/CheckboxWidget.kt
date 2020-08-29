package com.refinedmods.refinedstorage.screen.widget

import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.widget.CheckboxWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.function.Consumer

class CheckboxWidget(x: Int, y: Int, text: Text, isChecked: Boolean, private val onPress: Consumer<com.refinedmods.refinedstorage.screen.widget.CheckboxWidget>) : CheckboxWidget(
        x,
        y,
        MinecraftClient.getInstance().textRenderer.getWidth(text.string) + BOX_WIDTH,
        10,
        text,
        isChecked
) {
    private var shadow = true
    fun setShadow(shadow: Boolean) {
        this.shadow = shadow
    }

    override fun onPress() {
        super.onPress()
        onPress.accept(this)
    }

    fun setChecked(value: Boolean) {
        this.isChecked = value
    }

    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val minecraft = MinecraftClient.getInstance()
        minecraft.textureManager.bindTexture(TEXTURE)
        RenderSystem.enableDepthTest()
        val textRenderer = minecraft.textRenderer
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, this.alpha)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA)
        val textureX = if (this.isFocused) 10.0f else 0.0f
        val textureY = if (this.isChecked) 10.0f else 0.0f
        val width = 10
        val height = 10
        DrawableHelper.drawTexture(matrixStack, this.x, this.y, textureX, textureY, width, height, 32, 32)
        this.renderBg(matrixStack, minecraft, mouseX, mouseY)
        var color = 14737632
        if (!active) {
            color = 10526880
        }
        // TODO find packedFGColor?
//        else if (packedFGColor != 0) {
//            color = packedFGColor
//        }
        if (shadow) {
            drawStringWithShadow(matrixStack, textRenderer, this.message.asString(), this.x + 13, this.y + (height - 8) / 2, color)
        } else {

            textRenderer.draw(matrixStack, this.message, (this.x + 13).toFloat(), this.y + (height - 8) / 2f, color)
        }
    }


    companion object {
        private val TEXTURE: Identifier = Identifier("textures/gui/checkbox.png")
        private const val BOX_WIDTH = 13
    }

}