package com.refinedmods.refinedstorage.screen.grid.stack

import com.refinedmods.refinedstorage.api.storage.tracker.StorageTrackerEntry
import com.refinedmods.refinedstorage.gui.screen.BaseScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import java.util.*

interface IGridStack {
    val id: UUID?

    val otherId: UUID?
    fun updateOtherId(otherId: UUID?)
    val name: String?
    val modId: String
    val modName: String
    val tags: Set<String?>
    val tooltip: List<Text>
    val quantity: Int
    val formattedFullQuantity: String?
    fun draw(matrixStack: MatrixStack?, screen: BaseScreen<*>?, x: Int, y: Int)
    val ingredient: Any

    var trackerEntry: StorageTrackerEntry?
    val isCraftable: Boolean
}