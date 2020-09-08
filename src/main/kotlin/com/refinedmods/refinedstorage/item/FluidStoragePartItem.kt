package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType
import com.thinkslynk.fabric.annotations.registry.Category
import com.thinkslynk.fabric.annotations.registry.RegisterItem
import com.thinkslynk.fabric.generated.MyItemGroups
import net.minecraft.item.Item

@RegisterItem(RS.ID, "$0_storage_part")
class FluidStoragePartItem(
    @Suppress("UNUSED_PARAMETER") @Category("survival")
    type: FluidStorageType
) : Item(Settings().group(MyItemGroups.CURED_STORAGE)) {
    companion object {
        fun getByType(type: FluidStorageType): FluidStoragePartItem {
            return when (type) {
                FluidStorageType.SIXTY_FOUR_K -> TODO()
                FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K -> TODO()
                FluidStorageType.THOUSAND_TWENTY_FOUR_K -> TODO()
                FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K -> TODO()
                else -> throw IllegalArgumentException("Cannot get storage part of $type")
            }
        }
    }
}