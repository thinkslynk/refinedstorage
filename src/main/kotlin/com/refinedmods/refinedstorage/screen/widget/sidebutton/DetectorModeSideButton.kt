package com.refinedmods.refinedstorage.screen.widget.sidebutton
//
//import com.refinedmods.refinedstorage.container.DetectorContainer
//import com.refinedmods.refinedstorage.screen.BaseScreen
//import com.refinedmods.refinedstorage.tile.data.TileDataManager
//import net.minecraft.client.resource.language.I18n
//import net.minecraft.client.util.math.MatrixStack
//import net.minecraft.util.Formatting
//
//class DetectorModeSideButton(screen: BaseScreen<DetectorContainer>) : SideButton(screen) {
//    override fun getTooltip(): String {
//        // TODO DetectorTile
//        return "FIXME"
////        return I18n.translate("sidebutton.refinedstorage.detector.mode").toString() + "\n" + Formatting.GRAY + I18n.translate("sidebutton.refinedstorage.detector.mode." + DetectorTile.MODE.value)
//    }
//
//    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
//        // TODO DetectorTile
////        screen.drawTexture(matrixStack, x, y, DetectorTile.MODE.value * 16, 176, 16, 16)
//    }
//
//    override fun onPress() {
//        // TODO DetectorTile
////        var mode = DetectorTile.MODE.value
////        when (mode) {
////            DetectorNetworkNode.MODE_EQUAL -> mode = DetectorNetworkNode.MODE_ABOVE
////            DetectorNetworkNode.MODE_ABOVE -> mode = DetectorNetworkNode.MODE_UNDER
////            DetectorNetworkNode.MODE_UNDER -> mode = DetectorNetworkNode.MODE_EQUAL
////        }
////        TileDataManager.setParameter(DetectorTile.MODE, mode)
//    }
//}