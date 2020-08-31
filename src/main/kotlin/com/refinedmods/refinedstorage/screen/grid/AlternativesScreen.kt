package com.refinedmods.refinedstorage.screen.grid

import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.AlternativesContainer
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.widget.CheckboxWidget
import com.refinedmods.refinedstorage.screen.widget.ScrollbarWidget
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.tag.ItemTags
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import reborncore.common.fluid.FluidUtil
import reborncore.common.fluid.container.FluidInstance
import java.util.*
import java.util.function.Consumer

class AlternativesScreen private constructor(private val parent: Screen, player: PlayerEntity, title: Text):
        BaseScreen<AlternativesContainer>(AlternativesContainer(player), 175, 143, null, title) {
    private val scrollbar: ScrollbarWidget = ScrollbarWidget(this, 155, 20, 12, 89)
    private val lines: MutableList<Line> = ArrayList()
    private var type = 0
    private var slot = 0
    private var item: ItemStack? = null
    private var fluid: FluidInstance? = null

    constructor(parent: Screen, player: PlayerEntity, title: Text, item: ItemStack?, slot: Int) : this(parent, player, title) {
        type = IType.ITEMS
        this.slot = slot
        this.item = item
        fluid = null
    }

    constructor(parent: Screen, player: PlayerEntity, title: Text, fluid: FluidInstance?, slot: Int) : this(parent, player, title) {
        type = IType.FLUIDS
        this.slot = slot
        item = null
        this.fluid = fluid
    }

    override fun onPostInit(x: Int, y: Int) {
        lines.clear()
        if (item != null) {
            lines.add(ItemLine(item!!))
            for (owningTag in ItemTags.getTagGroup().getTagsFor(item!!.item)) {
                // TODO GridTile
//                lines.add(TagLine(owningTag, GridTile.ALLOWED_ITEM_TAGS.value[slot].contains(owningTag)))
                var line = ItemListLine()
                for ((itemCount, item) in ItemTags.getTagGroup().getTag(owningTag)!!.values().withIndex()) {
                    if (itemCount > 0 && itemCount % 8 == 0) {
                        lines.add(line)
                        line = ItemListLine()
                    }
                    line.addItem(ItemStack(item))
                }
                lines.add(line)
            }
        } else if (fluid != null) {
            // TODO Render fluid
//            lines.add(FluidLine(fluid))
//            for (owningTag in FluidTags.getCollection().getOwningTags(fluid.getFluid())) {
//                lines.add(TagLine(owningTag, GridTile.ALLOWED_FLUID_TAGS.value[slot].contains(owningTag)))
//                var fluidCount = 0
//                var line = FluidListLine()
//                for (fluid in FluidTags.getCollection().get(owningTag).getAllElements()) {
//                    if (fluidCount > 0 && fluidCount % 8 == 0) {
//                        lines.add(line)
//                        line = FluidListLine()
//                    }
//                    fluidCount++
//                    line.addFluid(FluidInstance(fluid, FluidAttributes.BUCKET_VOLUME))
//                }
//                lines.add(line)
//            }
        }

        // Do an initial layout
        val xx = 8
        var yy = 20
        for (i in lines.indices) {
            val visible = i >= scrollbar.getOffset() && i < scrollbar.getOffset() + visibleRows
            if (visible) {
                lines[i].layoutDependantControls(true, guiLeft + xx + 3, guiTop + yy + 3)
                yy += 18
            }
        }
        val apply: ButtonWidget = addButton(x + 7, y + 114, 50, 20, TranslatableText("gui.refinedstorage.alternatives.apply"), lines.size > 1, true, ButtonWidget.PressAction {  apply() })
        addButton(x + apply.width + 7 + 4, y + 114, 50, 20, TranslatableText("gui.cancel"), enabled = true, visible = true, onPress = ButtonWidget.PressAction { close() })
    }

    override fun tick(x: Int, y: Int) {
        scrollbar.setEnabled(rows > visibleRows)
        scrollbar.setMaxOffset(rows - visibleRows)
    }

    private val rows: Int
        get() = lines.size
    private val visibleRows: Int
        get() = 5

    override fun renderBackground(matrixStack: MatrixStack, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/alternatives.png")
        DrawableHelper.drawTexture(matrixStack, x, y, 0f, 0f, xSize, ySize, 256, 256)
        scrollbar.render(matrixStack)
    }

    override fun renderForeground(matrixStack: MatrixStack, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.string)
        var x = 8
        var y = 20
        for (i in lines.indices) {
            val visible = i >= scrollbar.getOffset() && i < scrollbar.getOffset() + visibleRows
            if (visible) {
                lines[i].layoutDependantControls(true, guiLeft + x + 3, guiTop + y + 3)
                lines[i].render(matrixStack, x, y)
                y += 18
            } else {
                lines[i].layoutDependantControls(false, -100, -100)
            }
        }
        x = 8
        y = 20
        for (i in lines.indices) {
            val visible = i >= scrollbar.getOffset() && i < scrollbar.getOffset() + visibleRows
            if (visible) {
                lines[i].renderTooltip(matrixStack, x, y, mouseX, mouseY)
                y += 18
            }
        }
    }

    override fun mouseMoved(mx: Double, my: Double) {
        scrollbar.mouseMoved(mx, my)
        super.mouseMoved(mx, my)
    }

    override fun mouseClicked(mx: Double, my: Double, button: Int): Boolean {
        return scrollbar.mouseClicked(mx, my, button) || super.mouseClicked(mx, my, button)
    }

    override fun mouseReleased(mx: Double, my: Double, button: Int): Boolean {
        return scrollbar.mouseReleased(mx, my, button) || super.mouseReleased(mx, my, button)
    }

    override fun mouseScrolled(x: Double, y: Double, delta: Double): Boolean {
        return scrollbar.mouseScrolled(x, y, delta) || super.mouseScrolled(x, y, delta)
    }

    override fun keyPressed(key: Int, scanCode: Int, modifiers: Int): Boolean {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            close()
            return true
        }
        return super.keyPressed(key, scanCode, modifiers)
    }

    private fun close() {
        MinecraftClient.getInstance().openScreen(parent)
    }

    private fun apply() {
        val allowed: MutableSet<Identifier> = HashSet()
        for (line in lines) {
            if (line is TagLine) {
                if (line.widget.isChecked) {
                    allowed.add(line.tagName)
                }
            }
        }
        // TODO Turn on once GridTile ported
//        if (type == IType.ITEMS) {
//            val existing: MutableList<Set<Identifier>> = GridTile.ALLOWED_ITEM_TAGS.value
//            existing[slot] = allowed
//            TileDataManager.setParameter(GridTile.ALLOWED_ITEM_TAGS, existing)
//        } else if (type == IType.FLUIDS) {
//            val existing: MutableList<Set<Identifier>> = GridTile.ALLOWED_FLUID_TAGS.value
//            existing[slot] = allowed
//            TileDataManager.setParameter(GridTile.ALLOWED_FLUID_TAGS, existing)
//        }
        close()
    }

    private interface Line {
        fun render(matrixStack: MatrixStack, x: Int, y: Int) {}
        fun renderTooltip(matrixStack: MatrixStack, x: Int, y: Int, mx: Int, my: Int) {}
        fun layoutDependantControls(visible: Boolean, x: Int, y: Int) {}
    }

    private inner class ItemLine(private val item: ItemStack) : Line {
        override fun render(matrixStack: MatrixStack, x: Int, y: Int) {
            RenderSystem.color4f(1f, 1f, 1f, 1f)
            renderItem(matrixStack, x + 3, y + 2, item)
            renderString(matrixStack, x + 4 + 19, y + 7, item.name.string)
        }
    }

    private inner class FluidLine(item: FluidInstance) : Line {
        private val fluid: FluidInstance = item
        override fun render(matrixStack: MatrixStack, x: Int, y: Int) {
            // TODO Draw fluid
//            FluidConfigGui.draw(matrixStack)
//            FluidRenderer.INSTANCE.render(matrixStack, x + 3, y + 2, fluid)
            renderString(matrixStack, x + 4 + 19, y + 7, FluidUtil.getFluidName(fluid.fluid))
        }

    }

    private inner class TagLine(val tagName: Identifier, checked: Boolean) : Line {
        val widget: CheckboxWidget = addCheckBox(-100, -100, Text.of(RenderUtils.shorten(tagName.toString(), 22)), checked, Consumer { })
        override fun layoutDependantControls(visible: Boolean, x: Int, y: Int) {
            widget.visible = visible
            widget.x = x
            widget.y = y
        }

        init {
            // TODO Foreground colors missing?
//            widget.setFGColor(-0xc8c8c9)
            widget.setShadow(false)
        }
    }

    private inner class ItemListLine : Line {
        private val items: MutableList<ItemStack> = ArrayList()
        fun addItem(stack: ItemStack) {
            items.add(stack)
        }

        override fun render(matrixStack: MatrixStack, x: Int, y: Int) {
            var rX = x
            for (item in items) {
                renderItem(matrixStack, rX + 3, y, item)
                rX += 17
            }
        }

        override fun renderTooltip(matrixStack: MatrixStack, x: Int, y: Int, mx: Int, my: Int) {
            var rX = x
            for (item in items) {
                if (RenderUtils.inBounds(rX+ 3, y, 16, 16, mx.toDouble(), my.toDouble())) {
                    this@AlternativesScreen.renderTooltip(matrixStack, item, mx, my, RenderUtils.getTooltipFromItem(item))
                }
                rX += 17
            }
        }
    }

    private inner class FluidListLine : Line {
        private val fluids: MutableList<FluidInstance> = ArrayList()
        fun addFluid(stack: FluidInstance) {
            fluids.add(stack)
        }

        override fun render(matrixStack: MatrixStack, x: Int, y: Int) {
            // TODO Render fluid
//            var x = x
//            for (fluid in fluids) {
//                FluidRenderer.INSTANCE.render(matrixStack, x + 3, y, fluid)
//                x += 17
//            }
        }

        override fun renderTooltip(matrixStack: MatrixStack, x: Int, y: Int, mx: Int, my: Int) {
            var rX = x
            for (fluid in fluids) {
                if (RenderUtils.inBounds(rX + 3, y, 16, 16, mx.toDouble(), my.toDouble())) {
                    this@AlternativesScreen.renderTooltip(matrixStack, mx, my, FluidUtil.getFluidName(fluid.fluid))
                }
                rX += 17
            }
        }
    }

}