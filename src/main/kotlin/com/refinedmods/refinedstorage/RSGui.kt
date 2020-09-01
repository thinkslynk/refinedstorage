package com.refinedmods.refinedstorage

import com.refinedmods.refinedstorage.block.ConstructorBlock
import com.refinedmods.refinedstorage.container.FilterContainer
import com.refinedmods.refinedstorage.gui.screenhandlers.AlternativesScreenHandler
import com.refinedmods.refinedstorage.gui.screenhandlers.AmountScreenHandler
import com.refinedmods.refinedstorage.gui.screenhandlers.ConstructorScreenHandler
import com.refinedmods.refinedstorage.gui.screenhandlers.FilterScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.util.Identifier

object RSGui {
    val AMOUNT = ScreenHandlerRegistry.registerSimple(Identifier(RS.ID, "amount_screen")) { windowId, playerInventory->
        AmountScreenHandler(playerInventory.player, windowId=windowId)
    }

    val ALTERNATIVES = ScreenHandlerRegistry.registerSimple(Identifier(RS.ID, "alternatives_screen")) { windowId, playerInventory->
        AlternativesScreenHandler(playerInventory.player, windowId)
    }

    val FILTER = ScreenHandlerRegistry.registerSimple(Identifier(RS.ID, "filter_screen")) { windowId, playerInventory->
        FilterScreenHandler(playerInventory.player, ItemStack.EMPTY, windowId)
    }

    val CONSTRUCTOR = ScreenHandlerRegistry.registerSimple(Identifier(RS.ID, ConstructorBlock.ID)) { windowId, playerInventory->
        ConstructorScreenHandler(windowId, ScreenHandlerContext.EMPTY, playerInventory.player)
    }

}