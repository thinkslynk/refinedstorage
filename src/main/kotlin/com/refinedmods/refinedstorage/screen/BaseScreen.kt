package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.config.ClientConfig
import com.refinedmods.refinedstorage.container.AmountContainer
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.render.RenderSettings
import com.refinedmods.refinedstorage.screen.grid.AlternativesScreen
import com.refinedmods.refinedstorage.screen.widget.CheckboxWidget
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton
import com.refinedmods.refinedstorage.util.RenderUtils
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.options.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import reborncore.common.fluid.FluidUtil
import reborncore.common.fluid.container.FluidInstance
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

abstract class BaseScreen<T : ScreenHandler>(
        container: T,
        val xSize: Int, val ySize: Int,
        inventory: PlayerInventory?,
        title: Text
) : HandledScreen<T>(container, inventory, title) {
    private val logger = LogManager.getLogger(javaClass)
    private var sideButtonY = 0

    // TODO These are almost certainly WRONG
    val guiLeft: Int
        get() {return this.x}
    val guiTop: Int
        get() {return this.y}

    private fun runActions() {
        runActions(javaClass)
        runActions(HandledScreen::class.java)
        TrackedDataHandlerRegistry.FLOAT
    }

    private fun runActions(clazz: Class<*>) {
        val queue = ACTIONS[clazz]
        if (queue != null && !queue.isEmpty()) {
            queue.forEach {
                it.accept(this)
            }
        }
    }

    override fun init() {
        MinecraftClient.getInstance().keyboard.setRepeatEvents(true)
        onPreInit()
        super.init()
        // TODO Crafting Tweaks
//        if (CraftingTweaksIntegration.isLoaded) {
//            buttons.removeIf({ b -> !isCraftingTweaksClass(b.getClass()) })
//            children.removeIf({ c -> !isCraftingTweaksClass(c.getClass()) })
//        } else {
//            buttons.clear()
//            children.clear()
//        }

        buttons.clear()
        children.clear()

        sideButtonY = 6
        onPostInit(guiLeft, guiTop)
        runActions()
    }

    override fun onClose() {
        super.onClose()
        MinecraftClient.getInstance().keyboard.setRepeatEvents(false)
    }

    override fun tick() {
        super.tick()
        runActions()
        tick(guiLeft, guiTop)
    }

    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderBackground(matrixStack)
        super.render(matrixStack, mouseX, mouseY, partialTicks)
        renderForeground(matrixStack, mouseX, mouseY)
    }

    override fun drawBackground(matrixStack: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        renderBackground(matrixStack, guiLeft, guiTop, mouseX, mouseY)
        for (i in 0 until screenHandler.slots.size) {
            val slot: Slot = screenHandler.slots[i]
            // TODO fluid
//            if (slot is FluidFilterSlot && slot.isEnabled) {
//                val stack: FluidInstance = slot.fluidInventory.getFluid(slot.id)
//                if (!stack.isEmpty) {
//                    FluidRenderer.INSTANCE.render(matrixStack, guiLeft + slot.x, guiTop + slot.y, stack)
//                    if (slot.isSizeAllowed) {
//                        renderQuantity(matrixStack, guiLeft + slot.x, guiTop + slot.y, instance().quantityFormatter.formatInBucketForm(stack.amount.rawValue), RenderSettings.INSTANCE.secondaryColor)
//                        GL11.glDisable(GL11.GL_LIGHTING)
//                    }
//                }
//            }
        }
    }

    override fun drawForeground(matrixStack: MatrixStack, _mouseX: Int, _mouseY: Int) {
        var mouseX = _mouseX
        var mouseY = _mouseY
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        mouseX -= guiLeft
        mouseY -= guiTop
        renderForeground(matrixStack, mouseX, mouseY)
        for (button in this.buttons) {
            if (button is SideButton && button.isHovered) {
                renderTooltip(matrixStack, mouseX, mouseY, (button as SideButton).getTooltip())
            }
        }
        for (i in 0 until this.screenHandler.slots.size) {
            val slot: Slot = screenHandler.slots[i]
            if (slot is FluidFilterSlot && slot.isEnabled) {
                val stack: FluidInstance = slot.fluidInventory.getFluid(slot.id)
                if (!stack.isEmpty && RenderUtils.inBounds(slot.x, slot.y, 17, 17, mouseX.toDouble(), mouseY.toDouble())) {
                    renderTooltip(matrixStack, mouseX, mouseY, FluidUtil.getFluidName(stack.fluid))
                }
            }
        }
    }


    override fun onMouseClick(slot: Slot, slotId: Int, mouseButton: Int, type: SlotActionType) {
        val valid = type !== SlotActionType.QUICK_MOVE && MinecraftClient.getInstance().player?.inventory?.getStack(slotId)?.isEmpty ?: true
        if (valid && slot is FilterSlot && slot.isEnabled && slot.isSizeAllowed) {
            if (!slot.getStack().isEmpty) {
                if (slot.isAlternativesAllowed && hasControlDown()) {
                    MinecraftClient.getInstance().openScreen(AlternativesScreen(
                            this,
                            MinecraftClient.getInstance().player!!,
                            TranslatableText("gui.$RS.ID.alternatives"),
                            slot.getStack(),
                            slot.id
                    ))
                } else {
                    MinecraftClient.getInstance()
                            .openScreen(ItemAmountScreen(
                            this as BaseScreen<AmountContainer>,
                            MinecraftClient.getInstance().player!!,
                            slot.id,
                            slot.getStack(),
                            slot.maxItemCount,
                            if (slot.isAlternativesAllowed) Function { parent: Screen ->
                                AlternativesScreen(
                                        parent,
                                        MinecraftClient.getInstance().player!!,
                                        TranslatableText("gui.$RS.ID.alternatives"),
                                        slot.getStack(),
                                        slot.id
                                ) as Screen
                            } else null
                    ))
                }
            }
        } else if (valid && slot is FluidFilterSlot && slot.isEnabled && slot.isSizeAllowed) {
            val stack: FluidInstance = slot.fluidInventory.getFluid(slot.id)
            if (!stack.isEmpty) {
                if (slot.isAlternativesAllowed && hasControlDown()) {
                    MinecraftClient.getInstance().openScreen(AlternativesScreen(
                            this,
                            MinecraftClient.getInstance().player!!,
                            TranslatableText("gui.$RS.ID.alternatives"),
                            stack,
                            slot.id
                    ))
                } else {
                    // TODO Fluid screens
//                    MinecraftClient.getInstance().openScreen(FluidAmountScreen(
//                            this,
//                            MinecraftClient.getInstance().player,
//                            slot.slotNumber,
//                            stack,
//                            (slot as FluidFilterSlot).fluidInventory.maxAmount,
//                            if ((slot as FluidFilterSlot).isAlternativesAllowed) Function { parent: Screen? ->
//                                AlternativesScreen(
//                                        this,
//                                        MinecraftClient.getInstance().player,
//                                        TranslatableText("gui.$RS.ID.alternatives"),
//                                        stack,
//                                        slot.id
//                                )
//                            } else null
//                    ))
                }
            } else {

                super.onMouseClick(slot, slotId, mouseButton, type)
            }
        } else {
            super.onMouseClick(slot, slotId, mouseButton, type)
        }
    }

    fun addCheckBox(x: Int, y: Int, text: Text, checked: Boolean, onPress: Consumer<CheckboxWidget>): CheckboxWidget {
        val checkBox = CheckboxWidget(x, y, text, checked, onPress)
        this.addButton(checkBox)
        return checkBox
    }

    fun addButton(x: Int, y: Int, w: Int, h: Int, text: Text?, enabled: Boolean, visible: Boolean, onPress: ButtonWidget.PressAction?): ButtonWidget  {
        val button = ButtonWidget(x, y, w, h, text, onPress)
        button.active
        button.active = enabled
        button.visible = visible
        addButton(button)
        return button
    }

    fun addSideButton(button: SideButton) {
        button.x = guiLeft + -SideButton.WIDTH - 2
        button.y = guiTop + sideButtonY
        sideButtonY += SideButton.HEIGHT + 2
        addButton(button)
    }

    fun bindTexture(namespace: String, filenameInTexturesFolder: String) {
        TrackedDataHandlerRegistry.FLOAT
        MinecraftClient.getInstance().textureManager
                .bindTexture(TEXTURE_CACHE.computeIfAbsent("$namespace:$filenameInTexturesFolder")
                { Identifier(namespace, "textures/$filenameInTexturesFolder") })
    }

    fun renderItem(matrixStack: MatrixStack, x: Int, y: Int, stack: ItemStack) {
        renderItem(matrixStack, x, y, stack, false, null, 0)
    }


    fun renderItem(matrixStack: MatrixStack, x: Int, y: Int, stack: ItemStack, overlay: Boolean, text: String?, textColor: Int) {
        try {
            zOffset = Z_LEVEL_ITEMS
            itemRenderer.zOffset = Z_LEVEL_ITEMS.toFloat()
            itemRenderer.renderInGui(stack, x, y)
            if (overlay) {
                itemRenderer.renderGuiItemOverlay(textRenderer, stack, x, y, "")
            }

            zOffset = 0
            itemRenderer.zOffset = 0f

            text?.let { renderQuantity(matrixStack, x, y, it, textColor) }
        } catch (t: Throwable) {
            logger.warn("Couldn't render stack: " + stack.item.toString(), t)
        }
    }

    fun renderQuantity(matrixStack: MatrixStack, x: Int, y: Int, qty: String, color: Int) {
        val large = MinecraftClient.getInstance().forcesUnicodeFont() || ClientConfig.gridLargeFont
        RenderSystem.pushMatrix()
        RenderSystem.translatef(x.toFloat(), y.toFloat(), Z_LEVEL_QTY.toFloat())
        if (!large) {
            RenderSystem.scalef(0.5f, 0.5f, 1f)
        }

        textRenderer.drawWithShadow(
                matrixStack,
                qty,
                (if (large) 16f else 30f) - textRenderer.getWidth(qty).toFloat(),
                if (large) 8f else 22f,
                color
        )
        RenderSystem.popMatrix()
    }

    fun renderString(matrixStack: MatrixStack, x: Int, y: Int, message: String) {
        renderString(matrixStack, x, y, message, RenderSettings.INSTANCE.primaryColor)
    }

    fun renderString(matrixStack: MatrixStack, x: Int, y: Int, message: String, color: Int) {
        textRenderer.draw(matrixStack, message, x.toFloat(), y.toFloat(), color)
    }

    fun renderTooltip(matrixStack: MatrixStack, x: Int, y: Int, lines: String) {
        renderTooltip(matrixStack, ItemStack.EMPTY, x, y, lines)
    }

    fun renderTooltip(matrixStack: MatrixStack, stack: ItemStack, x: Int, y: Int, lines: String) {
        renderTooltip(matrixStack, stack, x, y,
                lines.split("\n")
                        .map {Text.of(it) } // TODO Original didn't actually fill out the text...?
        )
    }

    fun renderTooltip(matrixStack: MatrixStack, stack: ItemStack, x: Int, y: Int, lines: List<Text>) {
        // TODO GuiUtils.drawHoveringText(stack, matrixStack, lines, x, y, width, height, -1, font);
    }

    protected open fun onPreInit() {
        // NO OP
    }

    abstract fun onPostInit(x: Int, y: Int)
    abstract fun tick(x: Int, y: Int)

    abstract fun renderBackground(matrixStack: MatrixStack, x: Int, y: Int, mouseX: Int, mouseY: Int)
    abstract fun renderForeground(matrixStack: MatrixStack, mouseX: Int, mouseY: Int)

    companion object {
        const val Z_LEVEL_ITEMS = 100
        const val Z_LEVEL_TOOLTIPS = 500
        const val Z_LEVEL_QTY = 300
        private val TEXTURE_CACHE: MutableMap<String, Identifier> = HashMap()
        private val ACTIONS: MutableMap<Class<*>, Queue<Consumer<Any>>> = HashMap()
        fun isKeyDown(keybinding: KeyBinding): Boolean {

            val key = KeyBindingHelper.getBoundKeyOf(keybinding)
            return InputUtil.isKeyPressed(
                    MinecraftClient.getInstance().window.handle, key.code
            )

            // TODO figure out keyboard conflict lookup
//                 && keybinding. getKeyConflictContext().isActive() &&
//                    keybinding.getKeyModifier().isActive(keybinding.getKeyConflictContext())


        }

        fun executeLater(clazz: Class<*>, callback: Consumer<Any>) {
            var queue = ACTIONS[clazz]
            if (queue == null) {
                ACTIONS[clazz] = ArrayDeque<Consumer<Any>>().also { queue = it }
            }
            queue!!.add(callback)
        }

        fun executeLater(callback: Consumer<Any>) {
            executeLater(HandledScreen::class.java, callback)
        }
    }
}