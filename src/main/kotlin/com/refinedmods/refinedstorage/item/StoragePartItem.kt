package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType
import com.thinkslynk.fabric.annotations.registry.Category
import com.thinkslynk.fabric.annotations.registry.RegisterItem
import com.thinkslynk.fabric.generated.ItemRegistryGenerated
import com.thinkslynk.fabric.generated.MyItemGroups
import net.minecraft.item.Item

@RegisterItem(RS.ID, "$0_storage_part")
class StoragePartItem(
    @Suppress("UNUSED_PARAMETER") @Category("survival")
    type: ItemStorageType
) : Item(Settings().group(MyItemGroups.CURED_STORAGE)) {
    companion object {
        fun getByType(type: ItemStorageType): StoragePartItem {
            return when (type) {
                ItemStorageType.ONE_K -> ItemRegistryGenerated.ONE_K_STORAGE_PART_ITEM
                ItemStorageType.FOUR_K -> ItemRegistryGenerated.FOUR_K_STORAGE_PART_ITEM
                ItemStorageType.SIXTEEN_K -> ItemRegistryGenerated.SIXTEEN_K_STORAGE_PART_ITEM
                ItemStorageType.SIXTY_FOUR_K -> ItemRegistryGenerated.SIXTY_FOUR_K_STORAGE_PART_ITEM
                else -> throw IllegalArgumentException("Cannot get storage part of $type")
            }
        }
    }
}