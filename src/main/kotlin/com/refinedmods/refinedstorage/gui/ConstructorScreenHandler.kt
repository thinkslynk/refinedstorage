package com.refinedmods.refinedstorage.gui

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.network.node.ConstructorNetworkNode
import com.refinedmods.refinedstorage.extensions.getCustomLogger
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WBox
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.data.Axis
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory


class ConstructorScreenHandler(
        syncId: Int,
        playerInventory: PlayerInventory,
        node: ConstructorNetworkNode?
): SyncedGuiDescription(
        RS.CONSTRUCTOR_SCREEN_HANDLER,
        syncId,
        playerInventory)
{
    companion object{
        const val SECTION_PADDING = 5
    }
    init {
        val upgrades = node?.upgrades ?: SimpleInventory(4)
        val filter = node?.itemFilters ?: SimpleInventory(1)

        val root = WBox(Axis.HORIZONTAL)
        setRootPanel(root)

        val left = WBox(Axis.VERTICAL)

        // Item to construct and player inventory
        val invPanel = createPlayerInventoryPanel()
        val filterSlot = WItemSlot.of(filter, 0, 1, 1)
        val center = WBox(Axis.VERTICAL)
        center.spacing = SECTION_PADDING
        center.horizontalAlignment = HorizontalAlignment.CENTER
        center.add(filterSlot)
        center.add(invPanel)

        val right = WItemSlot.of(upgrades, 0, 1, upgrades.size())

        // Compose
        root.add(left)
        root.add(center)
        root.add(right)

        root.validate(this)
    }

}