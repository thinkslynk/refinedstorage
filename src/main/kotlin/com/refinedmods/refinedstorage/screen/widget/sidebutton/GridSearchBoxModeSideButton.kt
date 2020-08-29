package com.refinedmods.refinedstorage.screen.widget.sidebutton
//
//import com.refinedmods.refinedstorage.api.network.grid.IGrid
//import com.refinedmods.refinedstorage.screen.grid.GridScreen
//import net.minecraft.client.resource.language.I18n
//import net.minecraft.client.util.math.MatrixStack
//import net.minecraft.util.Formatting
//
//class GridSearchBoxModeSideButton(screen: GridScreen) : SideButton(screen) {
//    override fun getTooltip(): String {
//        return I18n.translate("sidebutton.refinedstorage.grid.search_box_mode").toString() + "\n" + Formatting.GRAY + I18n.translate("sidebutton.refinedstorage.grid.search_box_mode." + (screen as GridScreen).grid!!.searchBoxMode)
//    }
//
//    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
//        val mode = (screen as GridScreen).grid!!.searchBoxMode
//        screen.drawTexture(matrixStack, x, y, if (mode == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED || mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED) 16 else 0, 96, 16, 16)
//    }
//
//    override fun onPress() {
//        var mode = (screen as GridScreen).grid!!.searchBoxMode
//        when (mode) {
//            IGrid.SEARCH_BOX_MODE_NORMAL -> mode = IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED
//            IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED -> mode = IGrid.SEARCH_BOX_MODE_NORMAL
//            // TODO Replace with REI integration
////                    if (JeiIntegration.isLoaded) {
////                IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED
////            } else {
////                IGrid.SEARCH_BOX_MODE_NORMAL
////            }
//            IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED -> mode = IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED
//            IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED -> mode = IGrid.SEARCH_BOX_MODE_NORMAL
//        }
//        screen.grid!!.onSearchBoxModeChanged(mode)
//        screen.searchField!!.setMode(mode)
//    }
//}