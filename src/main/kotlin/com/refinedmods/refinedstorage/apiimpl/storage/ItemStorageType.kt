package com.refinedmods.refinedstorage.apiimpl.storage

import com.thinkslynk.fabric.annotations.registry.Category
import com.thinkslynk.fabric.annotations.registry.RegisterArgument

@RegisterArgument
enum class ItemStorageType(val displayName: String, val capacity: Int) {
    @Category("survival")
    ONE_K("1k", 1000),
    @Category("survival")
    FOUR_K("4k", 4000),
    @Category("survival")
    SIXTEEN_K("16k", 16000),
    @Category("survival")
    SIXTY_FOUR_K("64k", 64000),
    CREATIVE("creative", -1);

    override fun toString(): String {
        return displayName
    }
}