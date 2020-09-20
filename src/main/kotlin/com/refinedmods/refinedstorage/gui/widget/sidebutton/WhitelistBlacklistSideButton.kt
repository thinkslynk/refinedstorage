package com.refinedmods.refinedstorage.gui.widget.sidebutton

import com.refinedmods.refinedstorage.gui.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.client.resource.language.I18n
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Formatting

class WhitelistBlacklistSideButton(screen: BaseScreen<*>, private val parameter: TileDataParameter<Int, *>) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.translate("sidebutton.refinedstorage.mode").toString() + "\n" + Formatting.GRAY + I18n.translate("sidebutton.refinedstorage.mode." + if (parameter.value == IWhitelistBlacklist.WHITELIST) "whitelist" else "blacklist")
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.drawTexture(matrixStack, x, y, if (parameter.value == IWhitelistBlacklist.WHITELIST) 0 else 16, 64, 16, 16)
    }

    override fun onPress() {
        TileDataManager.setParameter(parameter, if (parameter.value == IWhitelistBlacklist.WHITELIST) IWhitelistBlacklist.BLACKLIST else IWhitelistBlacklist.WHITELIST)
    }
}