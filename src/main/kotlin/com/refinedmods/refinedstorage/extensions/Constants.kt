package com.refinedmods.refinedstorage.extensions

object Constants {

    object NBT{
        const val LIST_TAG = 9
        const val COMPOUND_TAG = 10

        @Deprecated("no forge", ReplaceWith("Constants.NBT.LIST_TAG"))
        const val TAG_LIST = LIST_TAG
        @Deprecated("no forge", ReplaceWith("Constants.NBT.COMPOUND_TAG"))
        const val TAG_COMPOUND = COMPOUND_TAG
    }
}