package com.refinedmods.refinedstorage.gui

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

class ConstructorScreen(
        gui: ConstructorScreenHandler,
        player: PlayerEntity,
        title: Text
) : CottonInventoryScreen<ConstructorScreenHandler>(gui, player, title)