package com.refinedmods.refinedstorage

import com.refinedmods.refinedstorage.block.ConstructorBlock
import com.refinedmods.refinedstorage.gui.screenhandlers.AlternativesScreenHandler
import com.refinedmods.refinedstorage.gui.screenhandlers.AmountScreenHandler
import com.refinedmods.refinedstorage.gui.screenhandlers.ConstructorScreenHandler
import com.refinedmods.refinedstorage.gui.screenhandlers.FilterScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

object RSGui {
    val AMOUNT = ScreenHandlerRegistry.registerExtended(Identifier(RS.ID, "amount_screen"))
    { windowId, playerInventory, buf->
        AmountScreenHandler(windowId, playerInventory.player)
    }

    val ALTERNATIVES = ScreenHandlerRegistry.registerExtended(Identifier(RS.ID, "alternatives_screen"))
    { windowId, playerInventory, buf->
        AlternativesScreenHandler(windowId, playerInventory.player)
    }

    val FILTER = ScreenHandlerRegistry.registerExtended(Identifier(RS.ID, "filter_screen"))
    { windowId, playerInventory, buf->
        FilterScreenHandler(windowId, playerInventory.player, stack= ItemStack.EMPTY)
    }

    val CONSTRUCTOR = ScreenHandlerRegistry.registerExtended(Identifier(RS.ID, ConstructorBlock.ID))
    { windowId, playerInventory, buf->
        ConstructorScreenHandler(windowId, playerInventory.player, buf)
    }

}