package com.refinedmods.refinedstorage.screen.widget

import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.gui.Element
import net.minecraft.client.util.math.MatrixStack
import java.util.*
import java.util.function.Consumer
import kotlin.math.floor

class ScrollbarWidget(private val screen: BaseScreen<*>, private val x: Int, private val y: Int, private val width: Int, private val height: Int) : Element {
    private var enabled = false
    private var offset = 0
    private var maxOffset = 0
    private var clicked = false
    private val listeners: MutableList<ScrollbarWidgetListener> = LinkedList()
    fun addListener(listener: ScrollbarWidgetListener) {
        listeners.add(listener)
    }

    fun getWidth(): Int {
        return width
    }

    fun getHeight(): Int {
        return height
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    fun isEnabled(): Boolean {
        return enabled
    }

    fun render(matrixStack: MatrixStack?) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        screen.bindTexture(RS.ID, "icons.png")
        screen.drawTexture(matrixStack, screen.guiLeft + x, screen.guiTop + y + (height - SCROLLER_HEIGHT.toFloat()).coerceAtMost(offset.toFloat() / maxOffset.toFloat() * (height - SCROLLER_HEIGHT).toFloat()).toInt(), if (isEnabled()) 232 else 244, 0, 12, 15)
    }

    override fun mouseClicked(mx: Double, my: Double, button: Int): Boolean {
        var rx = mx
        var ry = my
        rx -= screen.guiLeft
        ry -= screen.guiTop
        if (button == 0 && RenderUtils.inBounds(x, y, width, height, rx, ry)) {
            // Prevent accidental scrollbar click after clicking recipe transfer button
            // TODO Rei
//            if (JeiIntegration.isLoaded && System.currentTimeMillis() - GridRecipeTransferHandler.LAST_TRANSFER_TIME <= GridRecipeTransferHandler.TRANSFER_SCROLLBAR_DELAY_MS) {
//                return false
//            }
            updateOffset(my)
            clicked = true
            return true
        }
        return false
    }

    override fun mouseMoved(mx: Double, my: Double) {
        var rx = mx
        var ry = my
        rx -= screen.guiLeft
        ry -= screen.guiTop
        if (clicked && RenderUtils.inBounds(x, y, width, height, rx, ry)) {
            updateOffset(ry)
        }
    }

    private fun updateOffset(my: Double) {
        setOffset(floor((my - y).toFloat() / (height - SCROLLER_HEIGHT).toFloat() * maxOffset.toFloat()).toInt())
    }

    override fun mouseReleased(mx: Double, my: Double, button: Int): Boolean {
        if (clicked) {
            clicked = false
            return true
        }
        return false
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollDelta: Double): Boolean {
        if (isEnabled()) {
            setOffset(offset + (-scrollDelta).toInt().coerceAtMost(1).coerceAtLeast(-1))
            return true
        }
        return false
    }

    fun setMaxOffset(maxOffset: Int) {
        this.maxOffset = maxOffset
        if (offset > maxOffset) {
            offset = 0.coerceAtLeast(maxOffset)
        }
    }

    fun getOffset(): Int {
        return offset
    }

    private fun setOffset(offset: Int) {
        val oldOffset = this.offset
        if (offset in 0..maxOffset) {
            this.offset = offset
            listeners.forEach(Consumer { l: ScrollbarWidgetListener -> l.onOffsetChanged(oldOffset, offset) })
        }
    }

    companion object {
        private const val SCROLLER_HEIGHT = 15
    }
}