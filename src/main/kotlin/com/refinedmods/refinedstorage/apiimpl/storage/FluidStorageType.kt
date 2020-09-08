package com.refinedmods.refinedstorage.apiimpl.storage

import com.thinkslynk.fabric.annotations.registry.Category
import com.thinkslynk.fabric.annotations.registry.RegisterArgument

// It seems this is measuring in millibuckets? Might need to change depending on the fluid api

@RegisterArgument
enum class FluidStorageType(
        val displayName: String,
        val capacity: Int
) {
    @Category("survival")
    SIXTY_FOUR_K("64k", 64000),
    @Category("survival")
    TWO_HUNDRED_FIFTY_SIX_K("256k", 256000),
    @Category("survival")
    THOUSAND_TWENTY_FOUR_K("1024k", 1024000),
    @Category("survival")
    FOUR_THOUSAND_NINETY_SIX_K("4096k", 4096000),
    CREATIVE("creative", -1);

    override fun toString(): String {
        return displayName
    }
}