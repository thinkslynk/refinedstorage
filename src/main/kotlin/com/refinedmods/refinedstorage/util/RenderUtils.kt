package com.refinedmods.refinedstorage.util

import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.render.Styles
import com.refinedmods.refinedstorage.screen.BaseScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.item.TooltipContext
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.*
import net.minecraft.util.Language
import net.minecraft.util.math.Matrix4f
import reborncore.client.gui.GuiUtil
import reborncore.common.fluid.FluidUtil
import reborncore.common.fluid.container.FluidInstance
import java.util.*

object RenderUtils {
    fun shorten(_text: String, length: Int): String {
        var text = _text
        if (text.length > length) {
            text = text.substring(0, length) + "..."
        }
        return text
    }

    fun getOffsetOnScale(pos: Int, scale: Float): Int {
        val multiplier = pos / scale
        return multiplier.toInt()
    }

    fun addCombinedItemsToTooltip(tooltip: MutableList<Text>, displayAmount: Boolean, stacks: List<ItemStack>) {
        val combinedIndices: MutableSet<Int> = HashSet()
        for (i in stacks.indices) {
            if (!stacks[i].isEmpty && !combinedIndices.contains(i)) {
                val stack = stacks[i]
                var data: MutableText = stack.name.copy()
                var amount = stack.count
                for (j in i + 1 until stacks.size) {
                    if (instance().comparer.isEqual(stack, stacks[j])) {
                        amount += stacks[j].count
                        combinedIndices.add(j)
                    }
                }
                if (displayAmount) {
                    data = Text.of(amount.toString() + "x " + data).copy()
                }

                tooltip.add(Texts.setStyleIfAbsent(data, Styles.GRAY))
            }
        }
    }

    fun addCombinedFluidsToTooltip(tooltip: MutableList<Text?>, displayMb: Boolean, stacks: List<FluidInstance>) {
        val combinedIndices: MutableSet<Int> = HashSet()
        for (i in stacks.indices) {
            if (!stacks[i].isEmpty && !combinedIndices.contains(i)) {
                val stack: FluidInstance = stacks[i]
                val name = FluidUtil.getFluidName(stack.fluid)
                var data: MutableText = Text.of(name).copy()
                var amount: Int = stack.amount.rawValue
                for (j in i + 1 until stacks.size) {
                    if (instance().comparer.isEqual(stack, stacks[j], IComparer.COMPARE_NBT)) {
                        amount += stacks[j].amount.rawValue
                        combinedIndices.add(j)
                    }
                }

                if (displayMb) {
                    data = Text.of(instance().quantityFormatter.formatInBucketForm(amount) + " " + data).copy()
                }

                tooltip.add(Texts.setStyleIfAbsent(data, Styles.GRAY))
            }
        }
    }

    // @Volatile: Copied with some tweaks from GuiUtils#drawHoveringText(@Nonnull final ItemStack stack, List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, TextRenderer font)
    fun drawTooltipWithSmallText(matrixStack: MatrixStack, _textLines: List<StringVisitable>, smallTextLines: List<String>, showSmallText: Boolean, stack: ItemStack, _mouseX: Int, _mouseY: Int, _screenWidth: Int, _screenHeight: Int, fontRenderer: TextRenderer) {
        // RS begin - definitions
        var textLines: List<StringVisitable> = _textLines
        var mouseX = _mouseX
        var mouseY = _mouseY
        var screenWidth = _screenWidth
        var screenHeight = _screenHeight
        var maxTextWidth = -1
        var font: TextRenderer = MinecraftClient.getInstance().textRenderer
        val textScale = if (MinecraftClient.getInstance().forcesUnicodeFont()) 1f else 0.7f
        // RS end
        if (textLines.isNotEmpty()) {
            // TODO Relpace events
//            val event: RenderTooltipEvent.Pre = Pre(stack, textLines, matrixStack, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font)
//            if (MinecraftForge.EVENT_BUS.post(event)) return
//            mouseX = event.getX()
//            mouseY = event.getY()
//            screenWidth = event.getScreenWidth()
//            screenHeight = event.getScreenHeight()
//            maxTextWidth = event.getMaxWidth()
//            font = event.getTextRenderer()
            RenderSystem.disableRescaleNormal()
            RenderSystem.disableDepthTest()
            var tooltipTextWidth = 0
            for (textLine in textLines) {
                val textLineWidth: Int = font.getWidth(textLine.string)
                if (textLineWidth > tooltipTextWidth) tooltipTextWidth = textLineWidth
            }

            // RS BEGIN
            if (showSmallText) {
                for (smallText in smallTextLines) {
                    val size = (font.getWidth(smallText) * textScale).toInt()
                    if (size > tooltipTextWidth) {
                        tooltipTextWidth = size
                    }
                }
            }
            // RS END
            var needsWrap = false
            var titleLinesCount = 1
            var tooltipX = mouseX + 12
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = mouseX - 16 - tooltipTextWidth
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    tooltipTextWidth = if (mouseX > screenWidth / 2) mouseX - 12 - 8 else screenWidth - 16 - mouseX
                    needsWrap = true
                }
            }
            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
                tooltipTextWidth = maxTextWidth
                needsWrap = true
            }
            if (needsWrap) {
                var wrappedTooltipWidth = 0
                val wrappedTextLines: MutableList<StringVisitable> = ArrayList()
                for (i in textLines.indices) {
                    val textLine: StringVisitable? = textLines[i]
                    val wrappedLine: List<StringVisitable> = font.textHandler.wrapLines(textLine, tooltipTextWidth, Style.EMPTY)
                    if (i == 0) titleLinesCount = wrappedLine.size
                    for (line in wrappedLine) {
                        val lineWidth: Int = font.getWidth(line.string)
                        if (lineWidth > wrappedTooltipWidth) wrappedTooltipWidth = lineWidth
                        wrappedTextLines.add(line)
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth
                textLines = wrappedTextLines
                tooltipX = if (mouseX > screenWidth / 2) mouseX - 16 - tooltipTextWidth else mouseX + 12
            }
            var tooltipY = mouseY - 12
            var tooltipHeight = 8
            if (textLines.size > 1) {
                tooltipHeight += (textLines.size - 1) * 10
                if (textLines.size > titleLinesCount) tooltipHeight += 2 // gap between title lines and next lines
            }

            // RS BEGIN
            if (showSmallText) {
                tooltipHeight += smallTextLines.size * 10
            }
            // RS END
            if (tooltipY < 4) tooltipY = 4 else if (tooltipY + tooltipHeight + 4 > screenHeight) tooltipY = screenHeight - tooltipHeight - 4
            val zLevel = BaseScreen.Z_LEVEL_TOOLTIPS
            var backgroundColor = -0xfeffff0
            var borderColorStart = 0x505000FF
            var borderColorEnd = borderColorStart and 0xFEFEFE shr 1 or borderColorStart and -0x1000000
            // TODO Events
//            val colorEvent: RenderTooltipEvent.Color = TextColor(stack, textLines, matrixStack, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd)
//            MinecraftForge.EVENT_BUS.post(colorEvent)
//            backgroundColor = colorEvent.getBackground()
//            borderColorStart = colorEvent.getBorderStart()
//            borderColorEnd = colorEvent.getBorderEnd()
            GuiUtil.drawGradientRect(tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor)
            GuiUtil.drawGradientRect(tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor)
            GuiUtil.drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor)
            GuiUtil.drawGradientRect(tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor)
            GuiUtil.drawGradientRect(tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor)
            GuiUtil.drawGradientRect(tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd)
            GuiUtil.drawGradientRect(tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd)
            GuiUtil.drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart)
            GuiUtil.drawGradientRect(tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd)
//            MinecraftForge.EVENT_BUS.post(PostBackground(stack, textLines, matrixStack, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight)) // TODO Events
            val renderType: VertexConsumerProvider.Immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)
            val textStack = MatrixStack()
            textStack.translate(0.0, 0.0, zLevel.toDouble())
            val textLocation: Matrix4f = textStack.peek().model
            val tooltipTop = tooltipY
            for (lineNumber in textLines.indices) {
                val line: StringVisitable? = textLines[lineNumber]
                if (line != null) font.draw(
                        Language.getInstance().get(line.string),
                        tooltipX.toFloat(),
                        tooltipY.toFloat(),
                        -1, true, textLocation, renderType, false, 0, 15728880
                )
                if (lineNumber + 1 == titleLinesCount) tooltipY += 2
                tooltipY += 10
            }
            renderType.draw()
//            MinecraftForge.EVENT_BUS.post(PostText(stack, textLines, matrixStack, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight)) // TODO Events

            // RS BEGIN
            if (showSmallText) {
                var y = tooltipTop + tooltipHeight - 6
                for (i in smallTextLines.indices.reversed()) {
                    // This is TextRenderer#drawStringWithShadow but with a custom MatrixStack
                    RenderSystem.enableAlphaTest()

                    // TextRenderer#drawStringWithShadow - call to func_228078_a_ (private)
                    val smallTextStack = MatrixStack()
                    smallTextStack.translate(0.0, 0.0, zLevel.toDouble())
                    smallTextStack.scale(textScale, textScale, 1f)
                    val r: VertexConsumerProvider.Immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)

                    font.draw(Texts.setStyleIfAbsent(Text.of(smallTextLines[i]).copy(), Styles.GRAY),
                            getOffsetOnScale(tooltipX, textScale).toFloat(),
                            getOffsetOnScale(y - if (MinecraftClient.getInstance().forcesUnicodeFont()) 2 else 0, textScale).toFloat(),
                            -1,
                            true,
                            smallTextStack.peek().model,
                            r,
                            false,
                            0,
                            15728880
                    )
                    r.draw()
                    y -= 9
                }
            }
            // RS END
            RenderSystem.enableDepthTest()
            RenderSystem.enableRescaleNormal()
        }
    }

    // @Volatile: From Screen#getTooltipFromItem
    fun getTooltipFromItem(stack: ItemStack): List<Text> {
        val minecraft = MinecraftClient.getInstance()
        return stack.getTooltip(minecraft.player, if (minecraft.options.advancedItemTooltips) TooltipContext.Default.ADVANCED else TooltipContext.Default.NORMAL)
    }

    fun inBounds(x: Int, y: Int, w: Int, h: Int, ox: Double, oy: Double): Boolean {
        return ox >= x && ox <= x + w && oy >= y && oy <= y + h
    }
}