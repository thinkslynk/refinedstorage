package com.refinedmods.refinedstorage.apiimpl.autocrafting.task.v6.calculator

import com.refinedmods.refinedstorage.api.util.IStackList

class PossibleInputs<T>(
        private val possibilities: List<T>
) {
    private var pos = 0
    fun get(): T {
        return possibilities[pos]
    }

    // Return false if we're exhausted.
    fun cycle(): Boolean {
        if (pos + 1 >= possibilities.size) {
            pos = 0
            return false
        }
        pos++
        return true
    }

    fun sort(mutatedStorage: IStackList<T>, results: IStackList<T>) {
        possibilities.sortedWith(java.util.Comparator { a: T, b: T ->
            val ar: Int = mutatedStorage.getCount(a)
            val br: Int = mutatedStorage.getCount(b)
            br - ar
        })
        possibilities.sortedWith(java.util.Comparator { a: T, b: T ->
            val ar: Int = results.getCount(a)
            val br: Int = results.getCount(b)
            br - ar
        })
    }
}