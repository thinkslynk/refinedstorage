//package com.refinedmods.refinedstorage.integration.rei
//
//import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
//import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
//import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot
//import com.refinedmods.refinedstorage.screen.BaseScreen
//import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
//import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer
//import me.shedaniel.rei.api.AutoTransferHandler
//import mezz.jei.api.gui.handlers.IGhostIngredientHandler
//import net.minecraft.client.renderer.Rectangle2d
//import net.minecraft.inventory.container.Slot
//import net.minecraft.item.ItemStack
//import net.minecraftforge.fluids.FluidInstance
//import org.lwjgl.system.CallbackI
//import reborncore.common.fluid.container.FluidInstance
//import java.util.*
//import me.shedaniel.rei.api.REIHelper
//import me.shedaniel.rei.api.
//
//
//class ReiAutoTransfer: AutoTransferHandler {
//    override fun handle(ctx: AutoTransferHandler.Context): AutoTransferHandler.Result {
//        val result = AutoTransferHandler.Result.createSuccessful()
//        ctx.container.slots
//                .forEach {slot ->
//
//                }
//
//
//
//
//
//
//
//
//        val targets: MutableList<Target<CallbackI.I>> = ArrayList<Target<CallbackI.I>>()
//        for (slot in gui.getContainer().inventorySlots) {
//            if (!slot.isEnabled()) {
//                continue
//            }
//            val bounds = Rectangle2d(gui.getGuiLeft() + slot.xPos, gui.getGuiTop() + slot.yPos, 17, 17)
//            if (ingredient is ItemStack) {
//                if (slot is LegacyFilterSlot || slot is FilterSlot) {
//                    targets.add(object : Target<CallbackI.I>() {
//                        val area: Rectangle2d
//                            get() = bounds
//
//                        fun accept(ingredient: CallbackI.I) {
//                            slot.putStack(ingredient as ItemStack)
//
//                            // RS.INSTANCE.network.sendToServer(new MessageSlotFilterSet(slot.slotNumber, (ItemStack) ingredient));
//                        }
//                    })
//                }
//            } else if (ingredient is FluidInstance) {
//                if (slot is FluidFilterSlot) {
//                    targets.add(object : Target<CallbackI.I>() {
//                        val area: Rectangle2d
//                            get() = bounds
//
//                        fun accept(ingredient: CallbackI.I) {
//                            // RS.INSTANCE.network.sendToServer(new MessageSlotFilterSetFluid(slot.slotNumber, StackUtils.copy((FluidInstance) ingredient, Fluid.BUCKET_VOLUME)));
//                        }
//                    })
//                }
//            }
//        }
//        return targets
//    }
//}