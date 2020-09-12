package com.refinedmods.refinedstorage.gui.screen

import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.API.quantityFormatter
import com.refinedmods.refinedstorage.container.slot.BaseSlot
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.gui.screen.grid.AlternativesScreen
import com.refinedmods.refinedstorage.gui.screenhandlers.AmountScreenHandler
import com.refinedmods.refinedstorage.gui.widget.CheckboxWidget
import com.refinedmods.refinedstorage.gui.widget.sidebutton.SideButton
import com.refinedmods.refinedstorage.render.RenderSettings
import com.refinedmods.refinedstorage.util.RenderUtils
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import org.lwjgl.opengl.GL11
import reborncore.client.RenderUtil
import reborncore.client.gui.builder.widget.tooltip.ToolTip
import reborncore.common.fluid.FluidUtil
import reborncore.common.fluid.container.FluidInstance

abstract class BaseScreen<T : ScreenHandler>(
    container: T,
    player: PlayerEntity,
    title: Text
): HandledScreen<T>(container, player.inventory, title) {
    private var sideButtonY = 0

    fun getX(): Int = x
    fun getY(): Int = y

    override fun init() {
        MinecraftClient.getInstance().keyboard.setRepeatEvents(true)
        onPreInit()
        super.init()

        playerInventory.player.mainHandStack
        // TODO Crafting Tweaks
//        if (CraftingTweaksIntegration.isLoaded) {
//            buttons.removeIf({ b -> !isCraftingTweaksClass(b.getClass()) })
//            children.removeIf({ c -> !isCraftingTweaksClass(c.getClass()) })
//        } else {
            buttons.clear()
            children.clear()
//        }

        sideButtonY = 6
        onPostInit(this.x, this.y)
    }

    override fun onClose() {
        super.onClose()
        MinecraftClient.getInstance().keyboard.setRepeatEvents(false)
    }

    override fun tick() {
        super.tick()
        tick(this.x, this.y)
    }

    override fun render(matrixStack: MatrixStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderBackground(matrixStack)
        super.render(matrixStack, mouseX, mouseY, partialTicks)
        renderForeground(matrixStack, mouseX, mouseY)
    }

    override fun drawBackground(matrixStack: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        renderBackground(matrixStack, this.x, this.y, mouseX, mouseY)
        for (i in 0 until screenHandler.slots.size) {
            val slot: Slot = screenHandler.slots[i]
            if (slot is FluidFilterSlot && slot.isEnabled) {
                val stack = slot.fluidInventory.getFluid(slot.id)
                if (!stack.isEmpty) {
                    val sprite = RenderUtil.getStillTexture(stack)
                    drawSprite(
                        matrixStack,
                        this.x + slot.x,
                        this.y + slot.y,
                        this.zOffset,
                        sprite.width,
                        sprite.height,
                        sprite
                    )

                    if (slot.isSizeAllowed) {
                        renderQuantity(
                            matrixStack, this.x + slot.x, this.y + slot.y,
                            quantityFormatter.formatInBucketForm(stack.amount.rawValue),
                            RenderSettings.INSTANCE.secondaryColor
                        )
                        GL11.glDisable(GL11.GL_LIGHTING)
                    }
                }
            }
        }
    }

    override fun drawForeground(matrixStack: MatrixStack, mouseX: Int, mouseY: Int) {
        val rX = mouseX - this.x
        val rY = mouseY - this.y

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)

        renderForeground(matrixStack, rX, rY)
        buttons.filter { it is SideButton && it.isHovered }
            .forEach { it.renderToolTip(matrixStack, rX, rY) }

        screenHandler.slots.filter { it is FluidFilterSlot && it.isEnabled }
let{it as List<FluidFilterSlot>}
            .forEach { slot ->
                val stack = slot.fluidInventory.getFluid(slot.id)
                if (!stack.isEmpty && RenderUtils.inBounds(slot.x, slot.y, 17, 17, rX.toDouble(), rY.toDouble())) {
                    ToolTip(Text.of(FluidUtil.getFluidName(stack.fluid)))
                        .draw(matrixStack, textRenderer, rX, rY)
                }
            }

    }

    fun openAlternateScreen(player: PlayerEntity, slot: FilterSlot) {
        MinecraftClient.getInstance().openScreen(
            AlternativesScreen(
                this,
                player,
                TranslatableText("gui.$RS.ID.alternatives"),
                slot.stack,
                slot.id
            )
        )
    }

    fun openAlternateScreen(player: PlayerEntity, slot: FluidFilterSlot, fluidStack: FluidInstance) {
        MinecraftClient.getInstance().openScreen(
            AlternativesScreen(
                this,
                player,
                TranslatableText("gui.$RS.ID.alternatives"),
                fluidStack,
                slot.id
            )
        )
    }

    fun openItemAmountScreen(player: PlayerEntity, slot: FilterSlot) {
        MinecraftClient.getInstance()
            .openScreen(ItemAmountScreen(
                this as BaseScreen<AmountScreenHandler>,
                player,
                slot.id,
                slot.stack,
                slot.maxItemCount,
                if (slot.isAlternativesAllowed) Function { parent: Screen ->
                    AlternativesScreen(
                        parent,
                        player,
                        TranslatableText("gui.$RS.ID.alternatives"),
                        slot.stack,
                        slot.id
                    ) as Screen
                } else null
            ))
    }

    fun openFluidAmountScreen(player: PlayerEntity, slot: FluidFilterSlot, fluidStack: FluidInstance) {
        // TODO FluidAmountScreen
//            MinecraftClient.getInstance().openScreen(FluidAmountScreen(
//                this,
//                player,
//                slot.id,
//                fluidStack,
//                slot.fluidInventory.maxAmount,
//                if (slot.isAlternativesAllowed) Function { parent: Screen ->
//                    AlternativesScreen(
//                            parent,
//                            player,
//                            TranslatableText("gui.$RS.ID.alternatives"),
//                            fluidStack,
//                            slot.id
//                    )
//                } else null
//        ))
    }


    override fun onMouseClick(slot: Slot?, slotId: Int, mouseButton: Int, type: SlotActionType) {
        val player = MinecraftClient.getInstance().player!!
        val valid = type != SlotActionType.QUICK_MOVE &&
                slotId >= 0 &&
                slotId < playerInventory.size() &&
                playerInventory.getStack(slotId)?.isEmpty ?: true &&
                slot != null &&
                slot is BaseSlot &&
                slot.isEnabled &&
                slot.isSizeAllowed

        if(!valid)
            return super.onMouseClick(slot, slotId, mouseButton, type)

        when(slot) {

            is FilterSlot -> {
                if (!slot.getStack().isEmpty) {
                    return when {
                        slot.isAlternativesAllowed && hasControlDown() ->
                            openAlternateScreen(player, slot)
                        else ->
                            openItemAmountScreen(player, slot)
                    }
                }
            }

            is FluidFilterSlot -> {
                val fluidStack = slot.fluidInventory.getFluid(slot.id)
                if (!fluidStack.isEmpty) {
                    return when {
                        slot.isAlternativesAllowed && hasControlDown() ->
                            openAlternateScreen(player, slot, fluidStack)
                        else ->
                            openFluidAmountScreen(player, slot, fluidStack)
                    }
                }
            }
        }

        super.onMouseClick(slot, slotId, mouseButton, type)
    }

    fun renderItem(
        matrixStack: MatrixStack,
        x: Int,
        y: Int,
        stack: ItemStack,
        overlay: Boolean = false,
        text: String? = null,
        textColor: Int = 0
    ) {
        zOffset = Z_LEVEL_ITEMS
        itemRenderer.zOffset = Z_LEVEL_ITEMS.toFloat()
        itemRenderer.renderInGui(stack, x, y)
        if (overlay) {
            itemRenderer.renderGuiItemOverlay(textRenderer, stack, x, y, "")
        }

        zOffset = 0
        itemRenderer.zOffset = 0f

        text?.let { renderQuantity(matrixStack, x, y, it, textColor) }
    }

    fun addCheckBox(x: Int, y: Int, text: Text, checked: Boolean, onPress: Consumer<CheckboxWidget>): CheckboxWidget {
        val checkBox = CheckboxWidget(x, y, text, checked, onPress)
        this.addButton(checkBox)
        return checkBox
    }

    fun addButton(
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        text: Text?,
        enabled: Boolean,
        visible: Boolean,
        onPress: ButtonWidget.PressAction?
    ): ButtonWidget  {
        val button = ButtonWidget(x, y, w, h, text, onPress)
        button.active
        button.active = enabled
        button.visible = visible
        addButton(button)
        return button
    }

    fun addSideButton(button: SideButton) {
        button.x = this.x + -SideButton.WIDTH - 2
        button.y = this.y + sideButtonY
        sideButtonY += SideButton.HEIGHT + 2
        addButton(button)
    }

    fun bindTexture(namespace: String, filenameInTexturesFolder: String) {
        TrackedDataHandlerRegistry.FLOAT
        MinecraftClient.getInstance().textureManager
            .bindTexture(
                TEXTURE_CACHE.computeIfAbsent("$namespace:$filenameInTexturesFolder")
                { Identifier(namespace, "textures/$filenameInTexturesFolder") })
    }

    fun renderQuantity(matrixStack: MatrixStack, x: Int, y: Int, qty: String, color: Int) {
        val large = MinecraftClient.getInstance().forcesUnicodeFont() || RS.CONFIG.clientConfig.grid.largeFont

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

    protected open fun onPreInit() {}
    open fun onPostInit(x: Int, y: Int) {}

    open fun tick(x: Int, y: Int) {}

    open fun renderBackground(matrixStack: MatrixStack, x: Int, y: Int, mouseX: Int, mouseY: Int) {}
    open fun renderForeground(matrixStack: MatrixStack, mouseX: Int, mouseY: Int) {}

    companion object {
        const val Z_LEVEL_ITEMS = 100
        const val Z_LEVEL_TOOLTIPS = 500
        const val Z_LEVEL_QTY = 300
        private val TEXTURE_CACHE: MutableMap<String, Identifier> = HashMap()
    }
}
