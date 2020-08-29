package com.refinedmods.refinedstorage.screen.widget.sidebutton
//
//import com.refinedmods.refinedstorage.api.network.grid.GridType
//import com.refinedmods.refinedstorage.api.network.grid.IGrid
//import com.refinedmods.refinedstorage.container.GridContainer
//import com.refinedmods.refinedstorage.screen.BaseScreen
//import net.minecraft.client.resource.language.I18n
//import net.minecraft.client.util.math.MatrixStack
//import net.minecraft.util.Formatting
//
//class GridSortingTypeSideButton(screen: BaseScreen<GridContainer>, private val grid: IGrid?) : SideButton(screen) {
//    override fun getTooltip(): String {
//        return I18n.translate("sidebutton.refinedstorage.grid.sorting.type").toString() + "\n" + Formatting.GRAY + I18n.translate("sidebutton.refinedstorage.grid.sorting.type." + grid!!.sortingType)
//    }
//
//    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
//        if (grid!!.sortingType == IGrid.SORTING_TYPE_LAST_MODIFIED) {
//            screen.drawTexture(matrixStack, x, y, 48, 48, 16, 16)
//        } else {
//            screen.drawTexture(matrixStack, x, y, grid.sortingType * 16, 32, 16, 16)
//        }
//    }
//
//    override fun onPress() {
//        // TODO GridNetworkNode
////        var type = grid!!.sortingType
////        when (type) {
////            IGrid.SORTING_TYPE_QUANTITY -> type = IGrid.SORTING_TYPE_NAME
////            IGrid.SORTING_TYPE_NAME -> type = (if (grid.gridType === GridType.FLUID) {
////                IGrid.SORTING_TYPE_LAST_MODIFIED
////            } else IGrid.SORTING_TYPE_ID)
////            IGrid.SORTING_TYPE_ID -> type = IGrid.SORTING_TYPE_LAST_MODIFIED
////            GridNetworkNode.SORTING_TYPE_LAST_MODIFIED -> type = if (grid.gridType === GridType.FLUID || !InventoryTweaksIntegration.isLoaded) {
////                IGrid.SORTING_TYPE_QUANTITY
////            } else {
////                IGrid.SORTING_TYPE_INVENTORYTWEAKS
////            }
////            GridNetworkNode.SORTING_TYPE_INVENTORYTWEAKS -> type = IGrid.SORTING_TYPE_QUANTITY
////        }
////        grid.onSortingTypeChanged(type)
//    }
//}