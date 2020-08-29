package com.refinedmods.refinedstorage.screen.widget.sidebutton

//import com.refinedmods.refinedstorage.container.CrafterContainer
//import com.refinedmods.refinedstorage.screen.BaseScreen
//import com.refinedmods.refinedstorage.tile.data.TileDataManager
//import net.minecraft.client.resource.language.I18n
//import net.minecraft.client.util.math.MatrixStack
//import net.minecraft.util.Formatting
//
//class CrafterModeSideButton(screen: BaseScreen<CrafterContainer>) : SideButton(screen) {
//    override fun getTooltip(): String {
//        // TODO Crafter gui
//        return "Fixme"
////        return I18n.translate("sidebutton.refinedstorage.crafter_mode").toString() + "\n" + Formatting.GRAY + I18n.translate("sidebutton.refinedstorage.crafter_mode." + CrafterTile.MODE.value)
//    }
//
//    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
////        screen.drawTexture(matrixStack, x, y, CrafterTile.MODE.value * 16, 0, 16, 16)
//    }
//
//    override fun onPress() {
////        TileDataManager.setParameter(CrafterTile.MODE, CrafterTile.MODE.value + 1)
//    }
//}