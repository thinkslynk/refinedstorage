package com.refinedmods.refinedstorage.screen.widget.sidebutton

//import com.refinedmods.refinedstorage.container.DiskManipulatorContainer
//import com.refinedmods.refinedstorage.screen.BaseScreen
//import com.refinedmods.refinedstorage.tile.data.TileDataManager
//import net.minecraft.client.resource.language.I18n
//import net.minecraft.client.util.math.MatrixStack
//import net.minecraft.util.Formatting
//
//class IoModeSideButton(screen: BaseScreen<DiskManipulatorContainer>) : SideButton(screen) {
//    override fun getTooltip(): String {
//        return I18n.translate("sidebutton.refinedstorage.iomode").toString() + "\n" + Formatting.GRAY + I18n.translate("sidebutton.refinedstorage.iomode." + if (DiskManipulatorTile.IO_MODE.value == DiskManipulatorNetworkNode.IO_MODE_INSERT) "insert" else "extract")
//    }
//
//    override fun renderButtonIcon(matrixStack: MatrixStack, x: Int, y: Int) {
//        screen.drawTexture(matrixStack, x, y, if (DiskManipulatorTile.IO_MODE.value == DiskManipulatorNetworkNode.IO_MODE_EXTRACT) 0 else 16, 160, 16, 16)
//    }
//
//    override fun onPress() {
//        TileDataManager.setParameter(DiskManipulatorTile.IO_MODE, if (DiskManipulatorTile.IO_MODE.value == DiskManipulatorNetworkNode.IO_MODE_INSERT) DiskManipulatorNetworkNode.IO_MODE_EXTRACT else DiskManipulatorNetworkNode.IO_MODE_INSERT)
//    }
//}