package com.refinedmods.refinedstorage.screen.grid.filtering

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import java.util.function.Predicate

class TagGridFilter(tagName: String) : Predicate<IGridStack> {
    private val tagName: String = tagName.toLowerCase()
    override fun test(stack: IGridStack): Boolean {
        return stack.tags.stream().anyMatch { name: String? -> name!!.toLowerCase().contains(tagName) }
    }

}