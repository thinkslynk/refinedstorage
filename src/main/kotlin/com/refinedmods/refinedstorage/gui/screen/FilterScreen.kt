@file:Suppress("DuplicatedCode")

package com.refinedmods.refinedstorage.gui.screen

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.api.util.IFilter
import com.refinedmods.refinedstorage.gui.screenhandlers.FilterScreenHandler
import com.refinedmods.refinedstorage.render.RenderSettings
import com.refinedmods.refinedstorage.gui.widget.CheckboxWidget
import com.refinedmods.refinedstorage.gui.widget.sidebutton.FilterTypeSideButton
import java.util.function.Consumer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.lwjgl.glfw.GLFW

class FilterScreen(container: FilterScreenHandler, player: PlayerEntity, title: Text):
        BaseScreen<FilterScreenHandler>(container, player, title)
{
    private val stack: ItemStack = container.stack
    // TODO remove defaults and use init{}
    private var compare: Int = 0
    private var mode: Int = 0
    private var modFilter: Boolean = false
    private val name: String = "todo fix"
    private var type: Int = 0
    private var modFilterCheckBox: CheckboxWidget? = null
    private var modeButton: ButtonWidget? = null
    private var nameField: TextFieldWidget? = null
    override fun onPostInit(x: Int, y: Int) {
        addCheckBox(x + 7, y + 77, TranslatableText("gui.refinedstorage.filter.compare_nbt"), compare and IComparer.COMPARE_NBT == IComparer.COMPARE_NBT, Consumer {
            compare = compare xor IComparer.COMPARE_NBT
            sendUpdate()
        })
        modFilterCheckBox = addCheckBox(0, y + 71 + 25, TranslatableText("gui.refinedstorage.filter.mod_filter"), modFilter, Consumer {
            modFilter = !modFilter
            sendUpdate()
        })
        modeButton = addButton(x + 7, y + 71 + 21, 0, 20, Text.of(""), enabled = true, visible = true, onPress = ButtonWidget.PressAction {
            mode = if (mode == IFilter.MODE_WHITELIST) IFilter.MODE_BLACKLIST else IFilter.MODE_WHITELIST
            updateModeButton(mode)
            sendUpdate()
        })
        updateModeButton(mode)

        val screen = this
        TextFieldWidget(textRenderer, x + 34, y + 121, 137 - 6, textRenderer.fontHeight, Text.of("")).run {
            nameField = this
            this.text = name
//            this.setEnableBackgroundDrawing(false) // TODO How do we disable this?
            this.isVisible = true
            this.setFocusUnlocked(true)
            this.changeFocus(false)
            this.setEditableColor(RenderSettings.INSTANCE.secondaryColor) // TODO Is this correct?
            this.setChangedListener { sendUpdate() }
            addButton(this)
            addSideButton(FilterTypeSideButton(screen))
        }
    }

    private fun updateModeButton(mode: Int) {
        val text: Text = if (mode == IFilter.MODE_WHITELIST) TranslatableText("sidebutton.refinedstorage.mode.whitelist") else TranslatableText("sidebutton.refinedstorage.mode.blacklist")
        modeButton?.let {
            it.width = textRenderer.getWidth(text.string) + 12
            it.message = text
            modFilterCheckBox?.x = it.x + it.width + 4
        }
    }

    override fun keyPressed(key: Int, scanCode: Int, modifiers: Int): Boolean {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            MinecraftClient.getInstance().player?.closeScreen()
            return true
        }
        return if (nameField!!.keyPressed(key, scanCode, modifiers) || nameField?.isActive == true) {
            true
        } else super.keyPressed(key, scanCode, modifiers)
    }

    override fun tick(x: Int, y: Int) {}
    override fun renderBackground(matrixStack: MatrixStack, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/filter.png")
        drawTexture(matrixStack, x, y, 0, 0, 176, 231)
    }

    override fun renderForeground(matrixStack: MatrixStack, mouseX: Int, mouseY: Int) {
        textRenderer.draw(matrixStack, title, 7f, 7f, RenderSettings.INSTANCE.secondaryColor)
        textRenderer.draw(matrixStack, I18n.translate("container.inventory"), 7f, 7f, RenderSettings.INSTANCE.secondaryColor)
    }

    fun getType(): Int {
        return type
    }

    fun setType(type: Int) {
        this.type = type
//        setType(stack, type) // TODO where does this go?
    }

    fun sendUpdate() {
        // TODO messages
        //RS.NETWORK_HANDLER.sendToServer(FilterUpdateMessage(compare, mode, modFilter, nameField!!.text, type)
    }

    init {
        // TODO where are these methods?
//        compare = getCompare(container.stack)
//        mode = getMode(container.stack)
//        modFilter = isModFilter(container.stack)
//        name = getName(container.stack)
//        type = getType(container.stack)
    }
}