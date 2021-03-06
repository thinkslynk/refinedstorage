package com.refinedmods.refinedstorage.integration.rei

//import com.refinedmods.refinedstorage.RS
//import com.refinedmods.refinedstorage.api.network.grid.GridType
//import net.minecraft.entity.player.PlayerEntity
//import net.minecraft.inventory.CraftingInventory
//import net.minecraft.item.ItemStack
//import reborncore.common.fluid.container.FluidInstance
//import java.util.*
//import java.util.stream.Collectors
//
//class GridRecipeTransferHandler : IRecipeTransferHandler<GridContainer?> {
//    val containerClass: Class<GridContainer>
//        get() = GridContainer::class.java
//
//    fun transferRecipe(container: GridContainer, recipeLayout: IRecipeLayout, player: PlayerEntity, maxTransfer: Boolean, doTransfer: Boolean): IRecipeTransferError? {
//        val grid = container.grid
//        if (doTransfer) {
//            LAST_TRANSFER_TIME = System.currentTimeMillis()
//            if (grid!!.gridType == GridType.PATTERN && !isCraftingRecipe(recipeLayout.getRecipeCategory())) {
//                val inputs: MutableList<ItemStack> = LinkedList()
//                val outputs: MutableList<ItemStack> = LinkedList()
//                val fluidInputs: MutableList<FluidInstance> = LinkedList<FluidInstance>()
//                val fluidOutputs: MutableList<FluidInstance> = LinkedList<FluidInstance>()
//                for (guiIngredient in recipeLayout.getItemStacks().getGuiIngredients().values()) {
//                    if (guiIngredient != null && guiIngredient.getDisplayedIngredient() != null) {
//                        val ingredient: ItemStack = guiIngredient.getDisplayedIngredient().copy()
//                        if (guiIngredient.isInput()) {
//                            inputs.add(ingredient)
//                        } else {
//                            outputs.add(ingredient)
//                        }
//                    }
//                }
//                for (guiIngredient in recipeLayout.getFluidInstances().getGuiIngredients().values()) {
//                    if (guiIngredient != null && guiIngredient.getDisplayedIngredient() != null) {
//                        val ingredient: FluidInstance = guiIngredient.getDisplayedIngredient().copy()
//                        if (guiIngredient.isInput()) {
//                            fluidInputs.add(ingredient)
//                        } else {
//                            fluidOutputs.add(ingredient)
//                        }
//                    }
//                }
//                RS.NETWORK_HANDLER.sendToServer(GridProcessingTransferMessage(inputs, outputs, fluidInputs, fluidOutputs))
//            } else {
//                RS.NETWORK_HANDLER.sendToServer(GridTransferMessage(
//                        recipeLayout.getItemStacks().getGuiIngredients(),
//                        container.inventorySlots.stream().filter({ s -> s.inventory is CraftingInventory }).collect(Collectors.toList())
//                ))
//            }
//        }
//        return null
//    }
//
//    private fun isCraftingRecipe(recipeCategory: IRecipeCategory<*>): Boolean {
//        return recipeCategory.getUid().equals(VanillaRecipeCategoryUid.CRAFTING)
//    }
//
//    companion object {
//        const val TRANSFER_SCROLLBAR_DELAY_MS: Long = 200
//        @JvmField
//        var LAST_TRANSFER_TIME: Long = 0
//    }
//}