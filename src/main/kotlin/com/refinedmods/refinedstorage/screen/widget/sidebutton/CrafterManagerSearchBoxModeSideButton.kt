package com.refinedmods.refinedstorage.screen.widget.sidebutton

// TODO Crafter
//import com.refinedmods.refinedstorage.api.network.grid.IGrid
//import com.refinedmods.refinedstorage.tile.data.TileDataManager
//import net.minecraft.client.util.math.MatrixStack
//
//class CrafterManagerSearchBoxModeSideButton(screen: CrafterManagerScreen) : SideButton(screen) {
//    override fun getTooltip(): String {
//        return I18n.format("sidebutton.refinedstorage.grid.search_box_mode").toString() + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.grid.search_box_mode." + (screen as CrafterManagerScreen).crafterManager.getSearchBoxMode())
//    }
//
//    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
//        val mode = (screen as CrafterManagerScreen).crafterManager.getSearchBoxMode()
//        screen.blit(matrixStack, x, y, if (mode == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED || mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED) 16 else 0, 96, 16, 16)
//    }
//
//    fun onPress() {
//        var mode = (screen as CrafterManagerScreen).crafterManager.getSearchBoxMode()
//        if (mode == IGrid.SEARCH_BOX_MODE_NORMAL) {
//            mode = IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED
//        } else if (mode == IGrid.SEARCH_BOX_MODE_NORMAL_AUTOSELECTED) {
//            mode = if (JeiIntegration.isLoaded) {
//                IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED
//            } else {
//                IGrid.SEARCH_BOX_MODE_NORMAL
//            }
//        } else if (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED) {
//            mode = IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED
//        } else if (mode == IGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED_AUTOSELECTED) {
//            mode = IGrid.SEARCH_BOX_MODE_NORMAL
//        }
//        TileDataManager.setParameter(CrafterManagerTile.SEARCH_BOX_MODE, mode)
//    }
//}