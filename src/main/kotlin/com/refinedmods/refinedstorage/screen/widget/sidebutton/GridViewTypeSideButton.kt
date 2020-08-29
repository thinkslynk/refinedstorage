package com.refinedmods.refinedstorage.screen.widget.sidebutton
//
//import com.refinedmods.refinedstorage.api.network.grid.IGrid
//import com.refinedmods.refinedstorage.container.GridContainer
//import com.refinedmods.refinedstorage.screen.BaseScreen
//import net.minecraft.client.resource.language.I18n
//import net.minecraft.client.util.math.MatrixStack
//import net.minecraft.util.Formatting
//
//class GridViewTypeSideButton(screen: BaseScreen<GridContainer>, private val grid: IGrid?) : SideButton(screen) {
//    override fun getTooltip(): String {
//        return I18n.translate("sidebutton.refinedstorage.grid.view_type").toString() + "\n" + Formatting.GRAY + I18n.translate("sidebutton.refinedstorage.grid.view_type." + grid!!.viewType)
//    }
//
//    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
//        screen.drawTexture(matrixStack, x, y, (grid!!.viewType - if (grid.viewType >= 3) 3 else 0) * 16, 112, 16, 16)
//    }
//
//    override fun onPress() {
//        var type = grid!!.viewType
//        when (type) {
//            IGrid.VIEW_TYPE_NORMAL -> type = IGrid.VIEW_TYPE_NON_CRAFTABLES
//            IGrid.VIEW_TYPE_NON_CRAFTABLES -> type = IGrid.VIEW_TYPE_CRAFTABLES
//            IGrid.VIEW_TYPE_CRAFTABLES -> type = IGrid.VIEW_TYPE_NORMAL
//        }
//        grid.onViewTypeChanged(type)
//    }
//}