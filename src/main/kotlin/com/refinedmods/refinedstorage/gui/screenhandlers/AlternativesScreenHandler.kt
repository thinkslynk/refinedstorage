package com.refinedmods.refinedstorage.gui.screenhandlers

import com.refinedmods.refinedstorage.RSGui
import com.refinedmods.refinedstorage.data.BaseBlockEntityData
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.ScreenHandlerContext

class AlternativesScreenHandler(
    windowId: Int = 0,
    player: PlayerEntity,
    entityData: BaseBlockEntityData? = null
) : BaseScreenHandler(windowId, player, entityData, RSGui.ALTERNATIVES)