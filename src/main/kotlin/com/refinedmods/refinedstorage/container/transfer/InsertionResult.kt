package com.refinedmods.refinedstorage.container.transfer

import net.minecraft.item.ItemStack

internal class InsertionResult {
    val type: InsertionResultType
    var value: ItemStack? = null
        private set

    constructor(value: ItemStack?) {
        type = InsertionResultType.CONTINUE_IF_POSSIBLE
        this.value = value
    }

    constructor(type: InsertionResultType) {
        this.type = type
    }
}