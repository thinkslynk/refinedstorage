package com.refinedmods.refinedstorage.integration.rei

import com.refinedmods.refinedstorage.extensions.getCustomLogger
import com.refinedmods.refinedstorage.gui.screen.ConstructorScreen
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.DisplayHelper

class FixReiOverlap: DisplayHelper.DisplayBoundsProvider<ConstructorScreen> {
    companion object{
        private val log = getCustomLogger(FixReiOverlap::class)
    }
    override fun getScreenBounds(screen: ConstructorScreen): Rectangle {
        log.info("Get screen bounds -- Size (${screen.width} x ${screen.height}) Pos -- (${screen.getX()}, ${screen.getY()})")
        return Rectangle(screen.width, screen.height)
    }

    override fun getBaseSupportedClass(): Class<ConstructorScreen> {
        return ConstructorScreen::class.java
    }
}