package com.refinedmods.refinedstorage.apiimpl.network.node
//
//import com.refinedmods.refinedstorage.api.network.grid.*
//import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler
//import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler
//import com.refinedmods.refinedstorage.api.network.security.Permission
//import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache
//import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener
//import com.refinedmods.refinedstorage.api.util.Action
//import com.refinedmods.refinedstorage.api.util.IFilter
//import com.refinedmods.refinedstorage.api.util.IStackList
//import com.refinedmods.refinedstorage.apiimpl.API
//import com.refinedmods.refinedstorage.block.GridBlock
//import com.refinedmods.refinedstorage.block.NetworkNodeBlock
//import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
//import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
//import com.refinedmods.refinedstorage.inventory.item.FilterItemHandler
//import com.refinedmods.refinedstorage.inventory.item.validator.ItemValidator
//import com.refinedmods.refinedstorage.inventory.listener.InventoryListener
//import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener
//import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener
//import com.refinedmods.refinedstorage.tile.config.IType
//import com.refinedmods.refinedstorage.tile.data.TileDataManager
//import com.refinedmods.refinedstorage.tile.grid.GridTile
//import net.minecraft.block.BlockState
//import net.minecraft.block.entity.BlockEntity
//import net.minecraft.entity.player.PlayerEntity
//import net.minecraft.inventory.CraftingInventory
//import net.minecraft.inventory.CraftingResultInventory
//import net.minecraft.inventory.Inventory
//import net.minecraft.item.ItemStack
//import net.minecraft.nbt.CompoundTag
//import net.minecraft.server.network.ServerPlayerEntity
//import net.minecraft.util.Identifier
//import net.minecraft.util.math.BlockPos
//import net.minecraft.world.World
//import reborncore.common.fluid.container.FluidInstance
//import java.util.*
//import java.util.function.Consumer
//import kotlin.collections.HashSet
//import kotlin.collections.List
//import kotlin.collections.MutableSet
//
//class GridNetworkNode(world: World, pos: BlockPos?, type: GridType) : NetworkNode(world, pos), INetworkAwareGrid, IType {
//    private val allowedTagList: AllowedTagList = AllowedTagList(Runnable { updateAllowedTags() })
//    private val craftingContainer: Container = object : Container(ContainerType.CRAFTING, 0) {
//        fun canInteractWith(player: PlayerEntity?): Boolean {
//            return false
//        }
//
//        fun onCraftMatrixChanged(inventory: Inventory?) {
//            if (!world.isClient) {
//                onCraftingMatrixChanged()
//            }
//        }
//    }
//    private var currentRecipe: ICraftingRecipe? = null
//    private val matrix: CraftingInventory = CraftingInventory(craftingContainer, 3, 3)
//    private val result: CraftingResultInventory = CraftingResultInventory()
//    private val processingMatrix: BaseItemHandler = BaseItemHandler(9 * 2)
//            .addListener(NetworkNodeInventoryListener(this))
//            .addListener(InventoryListener<BaseItemHandler> { handler: BaseItemHandler?, slot: Int, reading: Boolean ->
//                if (!reading && slot < 9) {
//                    allowedTagList.clearItemTags(slot)
//                }
//            })
//    private val processingMatrixFluids: FluidInventory = FluidInventory(9 * 2, FluidAttributes.BUCKET_VOLUME * 64)
//            .addListener(NetworkNodeFluidInventoryListener(this))
//            .addListener(InventoryListener<FluidInventory> { handler: FluidInventory?, slot: Int, reading: Boolean ->
//                if (!reading && slot < 9) {
//                    allowedTagList.clearFluidTags(slot)
//                }
//            })
//    private var reading = false
//    private val craftingListeners: MutableSet<ICraftingGridListener?> = HashSet<ICraftingGridListener?>()
//    private val patterns: BaseItemHandler = object : BaseItemHandler(2) {
//        fun getSlotLimit(slot: Int): Int {
//            return if (slot == 1) 1 else super.getSlotLimit(slot)
//        }
//
//        @Nonnull
//        override fun insertItem(slot: Int, @Nonnull stack: ItemStack, simulate: Boolean): ItemStack {
//            // Allow in slot 0
//            // Disallow in slot 1
//            // Only allow in slot 1 when it isn't a blank pattern
//            // This makes it so that written patterns can be re-inserted in slot 1 to be overwritten again
//            // This makes it so that blank patterns can't be inserted in slot 1 through hoppers.
//            return if (slot == 0 || stack.getTag() != null) {
//                super.insertItem(slot, stack, simulate)
//            } else stack
//        }
//    }
//            .addValidator(ItemValidator(RSItems.PATTERN))
//            .addListener(NetworkNodeInventoryListener(this))
//            .addListener(InventoryListener<BaseItemHandler> { handler: BaseItemHandler, slot: Int, reading: Boolean ->
//                val pattern: ItemStack = handler.getStackInSlot(slot)
//                if (!reading && slot == 1 && !pattern.isEmpty()) {
//                    val processing = PatternItem.isProcessing(pattern)
//                    if (processing) {
//                        for (i in 0..8) {
//                            processingMatrix.setStackInSlot(i, PatternItem.getInputSlot(pattern, i))
//                            processingMatrixFluids.setFluid(i, PatternItem.getFluidInputSlot(pattern, i))
//                        }
//                        for (i in 0..8) {
//                            processingMatrix.setStackInSlot(9 + i, PatternItem.getOutputSlot(pattern, i))
//                            processingMatrixFluids.setFluid(9 + i, PatternItem.getFluidOutputSlot(pattern, i))
//                        }
//                        val allowedTagsFromPattern: AllowedTagList? = PatternItem.getAllowedTags(pattern)
//                        if (allowedTagsFromPattern != null) {
//                            allowedTagList.setAllowedItemTags(allowedTagsFromPattern.getAllowedItemTags())
//                            allowedTagList.setAllowedFluidTags(allowedTagsFromPattern.getAllowedFluidTags())
//                        }
//                    } else {
//                        for (i in 0..8) {
//                            matrix.setInventorySlotContents(i, PatternItem.getInputSlot(pattern, i))
//                        }
//                    }
//                    setProcessingPattern(processing)
//                    markDirty()
//                }
//            })
//    private val filters: List<IFilter<*>> = ArrayList<IFilter<*>>()
//    private val tabs: List<IGridTab> = ArrayList<IGridTab>()
//    private val filter: FilterItemHandler = FilterItemHandler(filters, tabs).addListener(NetworkNodeInventoryListener(this)) as FilterItemHandler
//    private val type: GridType
//    private var viewType: Int = IGrid.Companion.VIEW_TYPE_NORMAL
//    private var sortingDirection: Int = IGrid.Companion.SORTING_DIRECTION_DESCENDING
//    private var sortingType: Int = IGrid.Companion.SORTING_TYPE_QUANTITY
//    private var searchBoxMode: Int = IGrid.Companion.SEARCH_BOX_MODE_NORMAL
//    private var size: Int = IGrid.Companion.SIZE_STRETCH
//    private var tabSelected = -1
//    private var tabPage = 0
//    var isExactPattern = true
//    private var processingPattern = false
//    private var processingType: Int = IType.ITEMS
//    fun getAllowedTagList(): AllowedTagList {
//        return allowedTagList
//    }
//
//    private fun updateAllowedTags() {
//        markDirty()
//        val tile: BlockEntity? = world.getBlockEntity(pos)
//        if (tile is GridTile) {
//            (tile as GridTile?).getDataManager().sendParameterToWatchers(GridTile.ALLOWED_ITEM_TAGS)
//            (tile as GridTile?).getDataManager().sendParameterToWatchers(GridTile.ALLOWED_FLUID_TAGS)
//        }
//    }
//
//    override val energyUsage: Int
//        get() = when (type) {
//            GridType.NORMAL -> RS.SERVER_CONFIG.getGrid().getGridUsage()
//            GridType.CRAFTING -> RS.SERVER_CONFIG.getGrid().getCraftingGridUsage()
//            GridType.PATTERN -> RS.SERVER_CONFIG.getGrid().getPatternGridUsage()
//            GridType.FLUID -> RS.SERVER_CONFIG.getGrid().getFluidGridUsage()
//            else -> 0
//        }
//
//    fun setViewType(viewType: Int) {
//        this.viewType = viewType
//    }
//
//    fun setSortingDirection(sortingDirection: Int) {
//        this.sortingDirection = sortingDirection
//    }
//
//    fun setSortingType(sortingType: Int) {
//        this.sortingType = sortingType
//    }
//
//    fun setSearchBoxMode(searchBoxMode: Int) {
//        this.searchBoxMode = searchBoxMode
//    }
//
//    fun setTabSelected(tabSelected: Int) {
//        this.tabSelected = tabSelected
//    }
//
//    fun setTabPage(page: Int) {
//        tabPage = page
//    }
//
//    fun setSize(size: Int) {
//        this.size = size
//    }
//
//    fun isProcessingPattern(): Boolean {
//        return if (world.isClient) GridTile.PROCESSING_PATTERN.value else processingPattern
//    }
//
//    fun setProcessingPattern(processingPattern: Boolean) {
//        this.processingPattern = processingPattern
//    }
//
//    val gridType: GridType
//        get() = type
//
//    fun createListener(player: ServerPlayerEntity?): IStorageCacheListener<*> {
//        return if (type === GridType.FLUID) FluidGridStorageCacheListener(player, network) else ItemGridStorageCacheListener(player, network)
//    }
//
//    @get:Nullable
//    val storageCache: IStorageCache<*>?
//        get() = if (network != null) if (type === GridType.FLUID) network!!.fluidStorageCache else network!!.itemStorageCache else null
//
//    @get:Nullable
//    val itemHandler: IItemGridHandler?
//        get() = if (network != null) network!!.itemGridHandler else null
//
//    @get:Nullable
//    val fluidHandler: IFluidGridHandler?
//        get() = if (network != null) network!!.fluidGridHandler else null
//
//    override fun addCraftingListener(listener: ICraftingGridListener?) {
//        craftingListeners.add(listener)
//    }
//
//    override fun removeCraftingListener(listener: ICraftingGridListener?) {
//        craftingListeners.remove(listener)
//    }
//
//    val title: Text
//        get() = when (type) {
//            GridType.CRAFTING -> TranslationTextComponent("gui.refinedstorage.crafting_grid")
//            GridType.PATTERN -> TranslationTextComponent("gui.refinedstorage.pattern_grid")
//            GridType.FLUID -> TranslationTextComponent("gui.refinedstorage.fluid_grid")
//            else -> TranslationTextComponent("gui.refinedstorage.grid")
//        }
//
//    fun getPatterns(): IItemHandler {
//        return patterns
//    }
//
//    override fun getFilter(): IItemHandlerModifiable {
//        return filter
//    }
//
//    override fun getFilters(): List<IFilter<*>> {
//        return filters
//    }
//
//    override fun getTabs(): List<IGridTab> {
//        return tabs
//    }
//
//    val craftingMatrix: CraftingInventory
//        get() = matrix
//    val craftingResult: CraftingResultInventory
//        get() = result
//
//    fun getProcessingMatrix(): BaseItemHandler {
//        return processingMatrix
//    }
//
//    fun getProcessingMatrixFluids(): FluidInventory {
//        return processingMatrixFluids
//    }
//
//    override fun onCraftingMatrixChanged() {
//        if (currentRecipe == null || !currentRecipe.matches(matrix, world)) {
//            currentRecipe = world.recipeManager.getRecipe(IRecipeType.CRAFTING, matrix, world).orElse(null)
//        }
//        if (currentRecipe == null) {
//            result.setInventorySlotContents(0, ItemStack.EMPTY)
//        } else {
//            result.setInventorySlotContents(0, currentRecipe.getCraftingResult(matrix))
//        }
//        craftingListeners.forEach(Consumer<ICraftingGridListener?> { obj: ICraftingGridListener? -> obj.onCraftingMatrixChanged() })
//        if (!reading) {
//            markDirty()
//        }
//    }
//
//    override fun onRecipeTransfer(player: PlayerEntity?, recipe: Array<Array<ItemStack?>?>?) {
//        API.instance().getCraftingGridBehavior().onRecipeTransfer(this, player, recipe)
//    }
//
//    fun clearMatrix() {
//        for (i in 0 until processingMatrix.getSlots()) {
//            processingMatrix.setStackInSlot(i, ItemStack.EMPTY)
//        }
//        for (i in 0 until processingMatrixFluids.getSlots()) {
//            processingMatrixFluids.setFluid(i, FluidInstance.EMPTY)
//        }
//        for (i in 0 until matrix.getSizeInventory()) {
//            matrix.setInventorySlotContents(i, ItemStack.EMPTY)
//        }
//    }
//
//    override fun onClosed(player: PlayerEntity?) {
//        // NO OP
//    }
//
//    val isGridActive: Boolean
//        get() {
//            val state: BlockState = world.getBlockState(pos)
//            return if (state.getBlock() is GridBlock) {
//                state.get(NetworkNodeBlock.CONNECTED)
//            } else false
//        }
//    val slotId: Int
//        get() = -1
//
//    override fun onCrafted(player: PlayerEntity?, @Nullable availableItems: IStackList<ItemStack?>?, @Nullable usedItems: IStackList<ItemStack?>?) {
//        API.instance().getCraftingGridBehavior().onCrafted(this, currentRecipe, player, availableItems, usedItems)
//    }
//
//    override fun onClear(player: PlayerEntity?) {
//        if (type === GridType.CRAFTING && network != null && network!!.securityManager!!.hasPermission(Permission.INSERT, player)) {
//            for (i in 0 until matrix.getSizeInventory()) {
//                val slot: ItemStack = matrix.getStackInSlot(i)
//                if (!slot.isEmpty()) {
//                    matrix.setInventorySlotContents(i, network!!.insertItem(slot, slot.getCount(), Action.PERFORM))
//                    network!!.itemStorageTracker!!.changed(player, slot.copy())
//                }
//            }
//        } else if (type === GridType.PATTERN) {
//            clearMatrix()
//        }
//    }
//
//    override fun onCraftedShift(player: PlayerEntity?) {
//        API.instance().getCraftingGridBehavior().onCraftedShift(this, player)
//    }
//
//    fun onCreatePattern() {
//        if (canCreatePattern()) {
//            if (patterns.getStackInSlot(1).isEmpty()) {
//                patterns.extractItem(0, 1, false)
//            }
//            val pattern = ItemStack(RSItems.PATTERN)
//            PatternItem.setToCurrentVersion(pattern)
//            PatternItem.setProcessing(pattern, processingPattern)
//            if (!processingPattern) {
//                PatternItem.setExact(pattern, isExactPattern)
//            } else {
//                PatternItem.setAllowedTags(pattern, allowedTagList)
//            }
//            if (processingPattern) {
//                for (i in 0..17) {
//                    if (!processingMatrix.getStackInSlot(i).isEmpty()) {
//                        if (i >= 9) {
//                            PatternItem.setOutputSlot(pattern, i - 9, processingMatrix.getStackInSlot(i))
//                        } else {
//                            PatternItem.setInputSlot(pattern, i, processingMatrix.getStackInSlot(i))
//                        }
//                    }
//                    val fluid: FluidInstance = processingMatrixFluids.getFluid(i)
//                    if (!fluid.isEmpty()) {
//                        if (i >= 9) {
//                            PatternItem.setFluidOutputSlot(pattern, i - 9, fluid)
//                        } else {
//                            PatternItem.setFluidInputSlot(pattern, i, fluid)
//                        }
//                    }
//                }
//            } else {
//                for (i in 0..8) {
//                    val ingredient: ItemStack = matrix.getStackInSlot(i)
//                    if (!ingredient.isEmpty()) {
//                        PatternItem.setInputSlot(pattern, i, ingredient)
//                    }
//                }
//            }
//            patterns.setStackInSlot(1, pattern)
//        }
//    }
//
//    private val isPatternAvailable: Boolean
//        private get() = !(patterns.getStackInSlot(0).isEmpty() && patterns.getStackInSlot(1).isEmpty())
//
//    fun canCreatePattern(): Boolean {
//        if (!isPatternAvailable) {
//            return false
//        }
//        return if (isProcessingPattern()) {
//            var inputsFilled = 0
//            var outputsFilled = 0
//            for (i in 0..8) {
//                if (!processingMatrix.getStackInSlot(i).isEmpty()) {
//                    inputsFilled++
//                }
//                if (!processingMatrixFluids.getFluid(i).isEmpty()) {
//                    inputsFilled++
//                }
//            }
//            for (i in 9..17) {
//                if (!processingMatrix.getStackInSlot(i).isEmpty()) {
//                    outputsFilled++
//                }
//                if (!processingMatrixFluids.getFluid(i).isEmpty()) {
//                    outputsFilled++
//                }
//            }
//            inputsFilled > 0 && outputsFilled > 0
//        } else {
//            !result.getStackInSlot(0).isEmpty() && isPatternAvailable
//        }
//    }
//
//    override fun getViewType(): Int {
//        return if (world.isClient) GridTile.VIEW_TYPE.value else viewType
//    }
//
//    override fun getSortingDirection(): Int {
//        return if (world.isClient) GridTile.SORTING_DIRECTION.value else sortingDirection
//    }
//
//    override fun getSortingType(): Int {
//        return if (world.isClient) GridTile.SORTING_TYPE.value else sortingType
//    }
//
//    override fun getSearchBoxMode(): Int {
//        return if (world.isClient) GridTile.SEARCH_BOX_MODE.value else searchBoxMode
//    }
//
//    override fun getSize(): Int {
//        return if (world.isClient) GridTile.SIZE.value else size
//    }
//
//    override fun getTabSelected(): Int {
//        return if (world.isClient) GridTile.TAB_SELECTED.value else tabSelected
//    }
//
//    override fun getTabPage(): Int {
//        return if (world.isClient) GridTile.TAB_PAGE.value else Math.min(tabPage, totalTabPages)
//    }
//
//    val totalTabPages: Int
//        get() = Math.floor(Math.max(0, tabs.size - 1).toFloat() / IGrid.TABS_PER_PAGE as Float.toDouble()).toInt()
//
//    override fun onViewTypeChanged(type: Int) {
//        TileDataManager.setParameter(GridTile.VIEW_TYPE, type)
//    }
//
//    override fun onSortingTypeChanged(type: Int) {
//        TileDataManager.setParameter(GridTile.SORTING_TYPE, type)
//    }
//
//    override fun onSortingDirectionChanged(direction: Int) {
//        TileDataManager.setParameter(GridTile.SORTING_DIRECTION, direction)
//    }
//
//    override fun onSearchBoxModeChanged(searchBoxMode: Int) {
//        TileDataManager.setParameter(GridTile.SEARCH_BOX_MODE, searchBoxMode)
//    }
//
//    override fun onSizeChanged(size: Int) {
//        TileDataManager.setParameter(GridTile.SIZE, size)
//    }
//
//    override fun onTabSelectionChanged(tab: Int) {
//        TileDataManager.setParameter(GridTile.TAB_SELECTED, tab)
//    }
//
//    override fun onTabPageChanged(page: Int) {
//        if (page >= 0 && page <= totalTabPages) {
//            TileDataManager.setParameter(GridTile.TAB_PAGE, page)
//        }
//    }
//
//    override fun getType(): Int {
//        return if (world.isClient) GridTile.PROCESSING_TYPE.value else processingType
//    }
//
//    override fun setType(type: Int) {
//        processingType = type
//        markDirty()
//    }
//
//    val itemFilters: IItemHandlerModifiable
//        get() = processingMatrix
//    val fluidFilters: FluidInventory
//        get() = processingMatrixFluids
//
//    override fun read(tag: CompoundTag) {
//        super.read(tag)
//        if (tag.contains(NBT_ALLOWED_TAGS)) {
//            allowedTagList.readFromNbt(tag.getCompound(NBT_ALLOWED_TAGS))
//        }
//        reading = true
//        StackUtils.readItems(matrix, 0, tag)
//        StackUtils.readItems(patterns, 1, tag)
//        StackUtils.readItems(filter, 2, tag)
//        StackUtils.readItems(processingMatrix, 3, tag)
//        if (tag.contains(NBT_PROCESSING_MATRIX_FLUIDS)) {
//            processingMatrixFluids.readFromNbt(tag.getCompound(NBT_PROCESSING_MATRIX_FLUIDS))
//        }
//        if (tag.contains(NBT_TAB_SELECTED)) {
//            tabSelected = tag.getInt(NBT_TAB_SELECTED)
//        }
//        if (tag.contains(NBT_TAB_PAGE)) {
//            tabPage = tag.getInt(NBT_TAB_PAGE)
//        }
//        reading = false
//    }
//
//    override val id: Identifier
//        get() = GridUtils.getNetworkNodeId(type)
//
//    override fun write(tag: CompoundTag): CompoundTag {
//        super.write(tag)
//        tag.put(NBT_ALLOWED_TAGS, allowedTagList.writeToNbt())
//        StackUtils.writeItems(matrix, 0, tag)
//        StackUtils.writeItems(patterns, 1, tag)
//        StackUtils.writeItems(filter, 2, tag)
//        StackUtils.writeItems(processingMatrix, 3, tag)
//        tag.put(NBT_PROCESSING_MATRIX_FLUIDS, processingMatrixFluids.writeToNbt())
//        tag.putInt(NBT_TAB_SELECTED, tabSelected)
//        tag.putInt(NBT_TAB_PAGE, tabPage)
//        return tag
//    }
//
//    override fun writeConfiguration(tag: CompoundTag): CompoundTag {
//        super.writeConfiguration(tag)
//        tag.putInt(NBT_VIEW_TYPE, viewType)
//        tag.putInt(NBT_SORTING_DIRECTION, sortingDirection)
//        tag.putInt(NBT_SORTING_TYPE, sortingType)
//        tag.putInt(NBT_SEARCH_BOX_MODE, searchBoxMode)
//        tag.putInt(NBT_SIZE, size)
//        tag.putBoolean(NBT_EXACT_MODE, isExactPattern)
//        tag.putBoolean(NBT_PROCESSING_PATTERN, processingPattern)
//        tag.putInt(NBT_PROCESSING_TYPE, processingType)
//        return tag
//    }
//
//    override fun readConfiguration(tag: CompoundTag) {
//        super.readConfiguration(tag)
//        if (tag.contains(NBT_VIEW_TYPE)) {
//            viewType = tag.getInt(NBT_VIEW_TYPE)
//        }
//        if (tag.contains(NBT_SORTING_DIRECTION)) {
//            sortingDirection = tag.getInt(NBT_SORTING_DIRECTION)
//        }
//        if (tag.contains(NBT_SORTING_TYPE)) {
//            sortingType = tag.getInt(NBT_SORTING_TYPE)
//        }
//        if (tag.contains(NBT_SEARCH_BOX_MODE)) {
//            searchBoxMode = tag.getInt(NBT_SEARCH_BOX_MODE)
//        }
//        if (tag.contains(NBT_SIZE)) {
//            size = tag.getInt(NBT_SIZE)
//        }
//        if (tag.contains(NBT_EXACT_MODE)) {
//            isExactPattern = tag.getBoolean(NBT_EXACT_MODE)
//        }
//        if (tag.contains(NBT_PROCESSING_PATTERN)) {
//            processingPattern = tag.getBoolean(NBT_PROCESSING_PATTERN)
//        }
//        if (tag.contains(NBT_PROCESSING_TYPE)) {
//            processingType = tag.getInt(NBT_PROCESSING_TYPE)
//        }
//    }
//
//    override val drops: IItemHandler
//        get() = when (type) {
//            GridType.CRAFTING -> CombinedInvWrapper(filter, InvWrapper(matrix))
//            GridType.PATTERN -> CombinedInvWrapper(filter, patterns)
//            else -> CombinedInvWrapper(filter)
//        }
//
//    companion object {
//        @kotlin.jvm.JvmField
//        val ID: Identifier = Identifier(RS.ID, "grid")
//        @kotlin.jvm.JvmField
//        val CRAFTING_ID: Identifier = Identifier(RS.ID, "crafting_grid")
//        @kotlin.jvm.JvmField
//        val PATTERN_ID: Identifier = Identifier(RS.ID, "pattern_grid")
//        @kotlin.jvm.JvmField
//        val FLUID_ID: Identifier = Identifier(RS.ID, "fluid_grid")
//        const val NBT_VIEW_TYPE = "ViewType"
//        const val NBT_SORTING_DIRECTION = "SortingDirection"
//        const val NBT_SORTING_TYPE = "SortingType"
//        const val NBT_SEARCH_BOX_MODE = "SearchBoxMode"
//        private const val NBT_EXACT_MODE = "Exact"
//        const val NBT_TAB_SELECTED = "TabSelected"
//        const val NBT_TAB_PAGE = "TabPage"
//        const val NBT_SIZE = "Size"
//        private const val NBT_PROCESSING_PATTERN = "ProcessingPattern"
//        private const val NBT_PROCESSING_TYPE = "ProcessingType"
//        private const val NBT_PROCESSING_MATRIX_FLUIDS = "ProcessingMatrixFluids"
//        private const val NBT_ALLOWED_TAGS = "AllowedTags"
//    }
//
//    init {
//        this.type = type
//    }
//}