package com.refinedmods.refinedstorage.gui.screenhandlers

import com.refinedmods.refinedstorage.RSGui
import com.refinedmods.refinedstorage.data.BaseBlockEntityData
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.ScreenHandlerContext

class AlternativesScreenHandler(
    player: PlayerEntity,
    entityData: BaseBlockEntityData,
    windowId: Int = 0
) : BaseScreenHandler(RSGui.ALTERNATIVES, entityData, player, windowId)