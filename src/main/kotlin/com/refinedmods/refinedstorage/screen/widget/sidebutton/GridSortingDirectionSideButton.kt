package com.refinedmods.refinedstorage.screen.widget.sidebutton
//
//import com.refinedmods.refinedstorage.api.network.grid.IGrid
//import com.refinedmods.refinedstorage.container.GridContainer
//import com.refinedmods.refinedstorage.screen.BaseScreen
//import net.minecraft.client.resource.language.I18n
//import net.minecraft.client.util.math.MatrixStack
//import net.minecraft.util.Formatting
//
//class GridSortingDirectionSideButton(screen: BaseScreen<GridContainer>, private val grid: IGrid?) : SideButton(screen) {
//    override fun getTooltip(): String {
//        return I18n.translate("sidebutton.refinedstorage.grid.sorting.direction").toString() + "\n" + Formatting.GRAY + I18n.translate("sidebutton.refinedstorage.grid.sorting.direction." + grid!!.sortingDirection)
//    }
//
//    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
//        screen.drawTexture(matrixStack, x, y, grid!!.sortingDirection * 16, 16, 16, 16)
//    }
//
//    override fun onPress() {
//        var dir = grid!!.sortingDirection
//        when (dir) {
//            IGrid.SORTING_DIRECTION_ASCENDING -> dir = IGrid.SORTING_DIRECTION_DESCENDING
//            IGrid.SORTING_DIRECTION_DESCENDING -> dir = IGrid.SORTING_DIRECTION_ASCENDING
//        }
//        grid.onSortingDirectionChanged(dir)
//    }
//}