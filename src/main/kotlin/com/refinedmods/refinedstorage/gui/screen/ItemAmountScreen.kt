package com.refinedmods.refinedstorage.gui.screen

import com.refinedmods.refinedstorage.gui.screenhandlers.AmountScreenHandler
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import java.util.function.Function

class ItemAmountScreen(
        parent: BaseScreen<AmountScreenHandler>,
        player: PlayerEntity,
        private val containerSlot: Int,
        private val stack: ItemStack,
        override val maxAmount: Int,
        private val alternativesScreenFactory: Function<Screen, Screen>?
) : AmountSpecifyingScreen<AmountScreenHandler>(
    parent,
    AmountScreenHandler(0, player, stack),
    if (alternativesScreenFactory != null) 194 else 172,
    99, player, TranslatableText("gui.refinedstorage.item_amount")
) {
    override val okCancelButtonWidth: Int
        get() = if (alternativesScreenFactory != null) 75 else super.okCancelButtonWidth


    override fun onPostInit(x: Int, y: Int) {
        super.onPostInit(x, y)
        if (alternativesScreenFactory != null) {
            addButton(x + 114, cancelButton.y + 24, okCancelButtonWidth, 20, TranslatableText("gui.refinedstorage.alternatives"), true, true, ButtonWidget.PressAction {
                MinecraftClient.getInstance().openScreen(alternativesScreenFactory.apply(this))
            })
        }
    }

    override val okCancelPos: Pair<Int, Int>
        get() {
            return if (alternativesScreenFactory == null) {
                super.okCancelPos
            } else 114 to 22
        }

    override val defaultAmount: Int
        get() = stack.count

    override fun canAmountGoNegative(): Boolean {
        return false
    }

    override val okButtonText: Text
        get() = TranslatableText("misc.refinedstorage.set")

    override val texture: String
        get() = if (alternativesScreenFactory != null) "gui/amount_specifying_wide.png" else "gui/amount_specifying.png"

    override val increments: IntArray
        get() = intArrayOf(
                1, 10, 64,
                -1, -10, -64
        )

    override fun onOkButtonPressed(shiftDown: Boolean) {
        try {
            val amount = amountField.text.toInt()
            // TODO Messages
//            RS.NETWORK_HANDLER.sendToServer(SetFilterSlotMessage(containerSlot, ItemHandlerHelper.copyStackWithSize(stack, amount)))
            close()
        } catch (e: NumberFormatException) {
            // NO OP
        }
    }
}