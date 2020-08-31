package com.refinedmods.refinedstorage.integration.rei

import com.refinedmods.refinedstorage.RS
import me.shedaniel.rei.api.DisplayHelper
import me.shedaniel.rei.api.DisplayHelper.DisplayBoundsProvider
import me.shedaniel.rei.api.EntryRegistry
import me.shedaniel.rei.api.OverlayDecider
import me.shedaniel.rei.api.RecipeHelper
import me.shedaniel.rei.api.plugins.REIPluginV0
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.Identifier

@Environment(EnvType.CLIENT)
class RSReiPlugin : REIPluginV0 {
    companion object {
        private val ID: Identifier = Identifier(RS.ID, "plugin")
    }

    override fun registerEntries(entryRegistry: EntryRegistry) {
        super.registerEntries(entryRegistry)
    }

    override fun getPluginIdentifier() = ID

    override fun registerRecipeDisplays(recipeHelper: RecipeHelper) {
        //recipeHelper.
        //registration.addUniversalRecipeTransferHandler(GridRecipeTransferHandler())
    }

    override fun registerBounds(displayHelper: DisplayHelper) {
        displayHelper.registerHandler(OverlayDecider)
        // TODO registration.addGuiContainerHandler(BaseScreen.class, new GuiContainerHandler());

        // TODO: https://github.com/mezz/JustEnoughItems/issues/1307
        // registration.addGhostIngredientHandler(BaseScreen.class, new GhostIngredientHandler());
    }
}