package com.refinedmods.refinedstorage.gui.screenhandlers

import com.refinedmods.refinedstorage.RSGui
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.ScreenHandlerContext

class AlternativesScreenHandler(player: PlayerEntity, windowId: Int = 0) :
    BaseScreenHandler(RSGui.ALTERNATIVES, ScreenHandlerContext.EMPTY, player, windowId)