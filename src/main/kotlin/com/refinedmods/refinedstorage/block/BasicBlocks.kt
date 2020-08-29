package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem


@RegisterBlock(RS.ID, MachineCasingBlock.ID)
@RegisterBlockItem(RS.ID, MachineCasingBlock.ID, "MISC")
class MachineCasingBlock : BaseBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES) {
    companion object{
        const val ID = "machine_casing"
    }
}

@RegisterBlock(RS.ID, QuartzEnrichedIronBlock.ID)
@RegisterBlockItem(RS.ID, QuartzEnrichedIronBlock.ID, "CURED_STORAGE")
class QuartzEnrichedIronBlock : BaseBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES) {
    companion object {
        const val ID = "quartz_enriched_iron_block"
    }
}