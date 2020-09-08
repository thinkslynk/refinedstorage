package com.refinedmods.refinedstorage.item.blockitem

import com.refinedmods.refinedstorage.block.BaseBlock
import com.refinedmods.refinedstorage.block.BlockDirection
import com.refinedmods.refinedstorage.extensions.BlockItemUseContext
import com.refinedmods.refinedstorage.extensions.Properties
import com.refinedmods.refinedstorage.extensions.getFace
import com.refinedmods.refinedstorage.extensions.getPos
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItemFor
import net.minecraft.block.BlockState
import net.minecraft.item.BlockItem

@RegisterBlockItemFor(BaseBlock::class)
class BaseBlockItem(private val block: BaseBlock, builder: Properties?) :
    BlockItem(block, builder) {
    override fun place(context: BlockItemUseContext, state: BlockState): Boolean {
        val result: Boolean = super.place(context, state)
        if (result && block.direction != BlockDirection.NONE) {
            context.world.setBlockState(
                context.getPos(), state.with(
                    block.direction.property, block.direction.getFrom(
                        context.getFace(),
                        context.getPos(),
                        context.player!! // FIXME: Can cause NPE
                    )
                )
            )
        }
        return result
    }
}