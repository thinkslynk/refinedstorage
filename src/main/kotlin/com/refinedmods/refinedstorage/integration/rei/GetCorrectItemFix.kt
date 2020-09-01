//package com.refinedmods.refinedstorage.integration.rei
//
//import com.refinedmods.refinedstorage.container.BaseContainer
//import com.refinedmods.refinedstorage.screen.BaseScreen
//import com.refinedmods.refinedstorage.screen.grid.GridScreen
//import me.shedaniel.rei.api.EntryStack
//import me.shedaniel.rei.api.FocusedStackProvider
//import net.fabricmc.fabric.api.resource.ResourceManagerHelper
//import net.minecraft.client.MinecraftClient
//import net.minecraft.client.gui.screen.Screen
//import net.minecraft.client.gui.screen.ingame.HandledScreen
//import net.minecraft.item.ItemStack
//import net.minecraft.resource.ResourceManager
//import net.minecraft.util.TypedActionResult
//
////TODO I think this is the correct replacement
//class GetCorrectItemFix : FocusedStackProvider {
//    override fun provide(screen: Screen): TypedActionResult<EntryStack> {
//        val mouse = MinecraftClient.getInstance().mouse
//        if (screen is GridScreen) {
//            if (!screen.searchField.isFocused && screen.isOverSlotArea(mouse.x, mouse.y)) {
//                return if (screen.slotNumber >= 0 && screen.slotNumber < screen.view.getStacks().size) {
//
//                    TypedActionResult.success(
//                            EntryStack.create(
//                                    screen.view.getStacks()[screen.slotNumber].ingredient as ItemStack // TODO ???
//                            )
//                    )
//                } else TypedActionResult.fail(null)
//            }
//        }
//
//        if (screen is BaseContainer) {
//            // TODO fluid
////            for (slot in screen.screenHandler.getFluidSlots()) {
////                val fluidInSlot: FluidInstance = slot.fluidInventory.getFluid(slot.getSlotIndex())
////                if (!fluidInSlot.isEmpty && RenderUtils.inBounds(slot.xPos, slot.yPos, 18, 18, mouseX, mouseY)) {
////                    return fluidInSlot
////                }
////            }
//        }
//
//        return TypedActionResult.pass(null)
//    }
//}
//
//// OLD
////class GuiContainerHandler : IGuiContainerHandler<HandledScreen<*>> {
////
////    fun getIngredientUnderMouse(screen: BaseScreen<*>, _mouseX: Double, _mouseY: Double): Any? {
////        val mouseX = _mouseX - screen.guiLeft
////        val mouseY = _mouseY - screen.guiTop
////
////        if (screen is GridScreen) {
////            if (!screen.searchField.isFocused && screen.isOverSlotArea(mouseX, mouseY)) {
////                return if (screen.slotNumber >= 0 && screen.slotNumber < screen.view.getStacks().size) {
////                    screen.view.getStacks()[screen.slotNumber].ingredient
////                } else null
////            }
////        }
////
////        if (screen.screenHandler is BaseContainer) {
////            // TODO fluid
//////            for (slot in screen.screenHandler.getFluidSlots()) {
//////                val fluidInSlot: FluidInstance = slot.fluidInventory.getFluid(slot.getSlotIndex())
//////                if (!fluidInSlot.isEmpty && RenderUtils.inBounds(slot.xPos, slot.yPos, 18, 18, mouseX, mouseY)) {
//////                    return fluidInSlot
//////                }
//////            }
////        }
////        return null
////    }
////}