package com.refinedmods.refinedstorage.config

import reborncore.common.config.Config

open class ServerConfig {
    companion object{
        // Controller
        @JvmField
        @Config(config = "server", category = "controller", key = "useEnergy", comment = "Whether the Controller uses energy")
        var controllerUseEnergy: Boolean = true

        @JvmField
        @Config(config = "server", category = "controller", key = "capacity", comment = "The energy capacity of the Controller")
        var controllerCapacity: Double = 32000.0

        @JvmField
        @Config(config = "server", category = "controller", key = "baseUsage", comment = "The base energy used by the Controller")
        var controllerBaseUsage: Double = 0.0

        @JvmField
        @Config(config = "server", category = "controller", key = "maxTransfer", comment = "The maximum energy that the Controller can receive")
        var controllerMaxTransfer: Double = Double.MAX_VALUE

        // Cable
        @JvmField
        @Config(config = "server", category = "cable", key = "usage", comment = "The energy used by the Cable")
        var cableUsage: Double = 0.0

        // Disk Drive
        @JvmField
        @Config(config = "server", category = "diskDrive", key = "usage", comment = "The energy used by the Disk Drive")
        var diskDriveUsage: Double = 0.0

        @JvmField
        @Config(config = "server", category = "diskDrive", key = "diskUsage", comment = "The energy used per disk in the Disk Drive")
        var diskDriveDiskUsage: Double = 1.0

        // Grids
        @JvmField
        @Config(config = "server", category = "grid", key = "gridUsage", comment = "The energy used by Grids")
        var gridUsage: Double = 2.0

        @JvmField
        @Config(config = "server", category = "grid", key = "craftingGridUsage", comment = "The energy used by Crafting Grids")
        var craftingGridUsage: Double = 4.0

        @JvmField
        @Config(config = "server", category = "grid", key = "patternGridUsage", comment = "The energy used by Pattern Grids")
        var patternGridUsage: Double = 4.0

        @JvmField
        @Config(config = "server", category = "grid", key = "fluidGridUsage", comment = "The energy used by Fluid Grids")
        var fluidGridUsage: Double = 2.0

        // Upgrades
        @JvmField
        @Config(config = "server", category = "upgrades", key = "rangeUpgradeUsage", comment = "The additional energy used by the Range Upgrade")
        var rangeUpgradeUsage: Double = 8.0

        @JvmField
        @Config(config = "server", category = "upgrades", key = "speedUpgradeUsage", comment = "The additional energy used by the Speed Upgrade")
        var speedUpgradeUsage: Double = 2.0

        @JvmField
        @Config(config = "server", category = "upgrades", key = "craftingUpgradeUsage", comment = "The additional energy used by the Crafting Upgrade")
        var craftingUpgradeUsage: Double = 5.0

        @JvmField
        @Config(config = "server", category = "upgrades", key = "stackUpgradeUsage", comment = "The additional energy used by the Stack Upgrade")
        var stackUpgradeUsage: Double = 12.0

        @JvmField
        @Config(config = "server", category = "upgrades", key = "silkTouchUpgradeUsage", comment = "The additional energy used by the Silk Touch Upgrade")
        var silkTouchUpgradeUsage: Double = 15.0

        @JvmField
        @Config(config = "server", category = "upgrades", key = "fortune1UpgradeUsage", comment = "The additional energy used by the Fortune 1 Upgrade")
        var fortune1UpgradeUsage: Double = 10.0

        @JvmField
        @Config(config = "server", category = "upgrades", key = "fortune2UpgradeUsage", comment = "The additional energy used by the Fortune 2 Upgrade")
        var fortune2UpgradeUsage: Double = 12.0

        @JvmField
        @Config(config = "server", category = "upgrades", key = "fortune3UpgradeUsage", comment = "The additional energy used by the Fortune 3 Upgrade")
        var fortune3UpgradeUsage: Double = 14.0

        @JvmField
        @Config(config = "server", category = "upgrades", key = "regulatorUpgradeUsage", comment = "The additional energy used by the Regulator Upgrade")
        var regulatorUpgradeUsage: Double = 15.0

        // Storage Block
        @JvmField
        @Config(config = "server", category = "storageBlock", key = "oneKUsage", comment = "The energy used by the 1k Storage Block")
        var oneKStorageBlockUsage: Double = 2.0

        @JvmField
        @Config(config = "server", category = "storageBlock", key = "fourKUsage", comment = "The energy used by the 4k Storage Block")
        var fourKStorageBlockUsage: Double = 4.0

        @JvmField
        @Config(config = "server", category = "storageBlock", key = "sixteenKUsage", comment = "The energy used by the 16k Storage Block")
        var sixteenKStorageBlockUsage: Double = 6.0

        @JvmField
        @Config(config = "server", category = "storageBlock", key = "sixtyFourKUsage", comment = "The energy used by the 64k Storage Block")
        var sixtyFourKStorageBlockUsage: Double = 8.0

        @JvmField
        @Config(config = "server", category = "storageBlock", key = "creativeUsage", comment = "The energy used by the Creative Storage Block")
        var creativeStorageBlockUsage: Double = 10.0

        // Fluid Storage Block
        @JvmField
        @Config(config = "server", category = "fluidStorageBlock", key = "sixtyFourKUsage", comment = "The energy used by the 64K Fluid Storage Block")
        var sixtyFourKFluidStorageBlockUsage: Double = 2.0

        @JvmField
        @Config(config = "server", category = "fluidStorageBlock", key = "twoHundredFiftySixKUsage", comment = "The energy used by the 256K Fluid Storage Block")
        var twoHundredFiftySixKFluidStorageBlockUsage: Double = 4.0

        @JvmField
        @Config(config = "server", category = "fluidStorageBlock", key = "thousandTwentyFourKUsage", comment = "The energy used by the 1024K Fluid Storage Block")
        var thousandTwentyFourKFluidStorageBlockUsage: Double = 6.0

        @JvmField
        @Config(config = "server", category = "fluidStorageBlock", key = "fourThousandNinetySixKUsage", comment = "The energy used by the 4096K Fluid Storage Block")
        var fourThousandNinetySixKFluidStorageBlockUsage: Double = 8.0

        @JvmField
        @Config(config = "server", category = "fluidStorageBlock", key = "creativeUsage", comment = "The energy used by the Creative Fluid Storage Block")
        var creativeFluidStorageBlockUsage: Double = 10.0

        // External Storage
        @JvmField
        @Config(config = "server", category = "externalStorage", key = "usage", comment = "The energy used by the External Storage")
        var externalStorageUsage: Double = 6.0

        // Importer
        @JvmField
        @Config(config = "server", category = "Importer", key = "usage", comment = "The energy used by the Importer")
        var importerUsage: Double = 1.0

        // Exporter
        @JvmField
        @Config(config = "server", category = "Exporter", key = "usage", comment = "The energy used by the Exporter")
        var exporterUsage: Double = 1.0

        // Network Receiver
        @JvmField
        @Config(config = "server", category = "networkReceiver", key = "usage", comment = "The energy used by the Network Receiver")
        var networkReceiverUsage: Double = 0.0

        // Network Transmitter
        @JvmField
        @Config(config = "server", category = "networkTransmitter", key = "usage", comment = "The energy used by the Network Transmitter")
        var networkTransmitterUsage: Double = 64.0

        // Relay
        @JvmField
        @Config(config = "server", category = "relay", key = "usage", comment = "The energy used by the Relay")
        var relayUsage: Double = 1.0

        // Detector
        @JvmField
        @Config(config = "server", category = "detector", key = "usage", comment = "The energy used by the Detector")
        var detectorUsage: Double = 2.0

        // Security Manager
        @JvmField
        @Config(config = "server", category = "securityManager", key = "usage", comment = "The energy used by the Security Manager")
        var securityManagerUsage: Double = 4.0

        @JvmField
        @Config(config = "server", category = "securityManager", key = "usagePerCard", comment = "The additional energy used by Security Cards in the Security Manager")
        var securityManagerUsagePerCard: Double = 10.0

        // Interface
        @JvmField
        @Config(config = "server", category = "interface", key = "usage", comment = "The energy used by the Interface")
        var interfaceUsage: Double = 2.0

        // Fluid Interface
        @JvmField
        @Config(config = "server", category = "fluidInterface", key = "usage", comment = "The energy used by the Fluid Interface")
        var fluidInterfaceUsage: Double = 2.0

        // Wireless Transmitter
        @JvmField
        @Config(config = "server", category = "wirelessTransmitter", key = "usage", comment = "The energy used by the Wireless Transmitter")
        var wirelessTransmitterUsage: Double = 8.0

        @JvmField
        @Config(config = "server", category = "wirelessTransmitter", key = "baseRange", comment = "The base range of the Wireless Transmitter")
        var wirelessTransmitterBaseRange: Double = 16.0

        @JvmField
        @Config(config = "server", category = "wirelessTransmitter", key = "rangePerUpgrade", comment = "The additional range per Range Upgrade in the Wireless Transmitter")
        var wirelessTransmitterRangePerUpgrade: Double = 8.0

        // Storage Monitor
        @JvmField
        @Config(config = "server", category = "storageMonitor", key = "usage", comment = "The energy used by the Storage Monitor")
        var storageMonitorUsage: Double = 3.0

        // Wireless Grid
        @JvmField
        @Config(config = "server", category = "wirelessGrid", key = "useEnergy", comment = "Whether the Wireless Grid uses energy")
        var wirelessGridUseEnergy: Boolean = true

        @JvmField
        @Config(config = "server", category = "wirelessGrid", key = "capacity", comment = "The energy capacity of the Wireless Grid")
        var wirelessGridCapacity: Double = 3200.0

        @JvmField
        @Config(config = "server", category = "wirelessGrid", key = "openUsage", comment = "the energy used by the Wireless Grid to open")
        var wirelessGridOpenUsage: Double = 30.0

        @JvmField
        @Config(config = "server", category = "wirelessGrid", key = "extractUsage", comment = "The energy used by the Wireless Grid to extract items")
        var wirelessGridExtractUsage: Double = 5.0

        @JvmField
        @Config(config = "server", category = "wirelessGrid", key = "insertUsage", comment = "The energy used by the Wireless Grid to insert items")
        var wirelessGridInsertUsage: Double = 5.0

        // Wireless Fluid Grid
        @JvmField
        @Config(config = "server", category = "wirelessFluidGrid", key = "useEnergy", comment = "Whether the Wireless Fluid Grid uses energy")
        var wirelessFluidGridUseEnergy: Boolean = true

        @JvmField
        @Config(config = "server", category = "wirelessFluidGrid", key = "capacity", comment = "The energy capacity of the Wireless Fluid Grid")
        var wirelessFluidGridCapacity: Double = 3200.0

        @JvmField
        @Config(config = "server", category = "wirelessFluidGrid", key = "openUsage", comment = "The energy used by the Wireless Fluid Grid to open")
        var wirelessFluidGridOpenUsage: Double = 30.0

        @JvmField
        @Config(config = "server", category = "wirelessFluidGrid", key = "extractUsage", comment = "The energy used by the Wireless Fluid Grid to extract fluids")
        var wirelessFluidGridExtractUsage: Double = 5.0

        @JvmField
        @Config(config = "server", category = "wirelessFluidGrid", key = "insertUsage", comment = "The energy used by the Wireless Fluid Grid to insert fluids")
        var wirelessFluidGridInsertUsage: Double = 5.0

        // Portable Grid
        @JvmField
        @Config(config = "server", category = "portableGrid", key = "useEnergy", comment = "Whether the Portable Grid uses energy")
        var portableGridUseEnergy: Boolean = true

        @JvmField
        @Config(config = "server", category = "portableGrid", key = "capacity", comment = "The energy capacity of the Portable Grid")
        var portableGridCapacity: Double = 3200.0

        @JvmField
        @Config(config = "server", category = "portableGrid", key = "openUsage", comment = "The energy used by the Portable Grid to open")
        var portableGridOpenUsage: Double = 30.0

        @JvmField
        @Config(config = "server", category = "portableGrid", key = "extractUsage", comment = "The energy used by the Portable Grid to extract items or fluids")
        var portableGridExtractUsage: Double = 5.0

        @JvmField
        @Config(config = "server", category = "portableGrid", key = "insertUsage", comment = "The energy used by the Portable Grid to insert items or fluids")
        var portableGridInsertUsage: Double = 5.0

        // Constructor
        @JvmField
        @Config(config = "server", category = "constructor", key = "usage", comment = "The energy used by the Constructor")
        var constructorUsage: Double = 3.0

        // Destructor
        @JvmField
        @Config(config = "server", category = "destructor", key = "usage", comment = "The energy used by the Destructor")
        var destructorUsage: Double = 3.0

        // Disk Manipulator
        @JvmField
        @Config(config = "server", category = "diskManipulator", key = "usage", comment = "The energy used by the Disk Manipulator")
        var diskManipulatorUsage: Double = 4.0

        // Crafter
        @JvmField
        @Config(config = "server", category = "crafter", key = "usage", comment = "The energy used by the Crafter")
        var crafterUsage: Double = 4.0

        @JvmField
        @Config(config = "server", category = "crafter", key = "patternUsage", comment = "The energy used for every Pattern in the Crafter")
        var crafterPatternUsage: Double = 1.0

        // Crafter Manager
        @JvmField
        @Config(config = "server", category = "crafterManager", key = "usage", comment = "The energy used by the Crafter Manager")
        var crafterMonitorUsage: Double = 8.0

        // Crafting Monitor
        @JvmField
        @Config(config = "server", category = "craftingMonitor", key = "usage", comment = "The energy used by the Crafting Monitor")
        var craftingMonitorUsage: Double = 8.0

        // Wireless Crafting Monitor
        @JvmField
        @Config(config = "server", category = "wirelessCraftingMonitor", key = "useEnergy", comment = "Whether the Wireless Crafting Monitor uses energy")
        var wirelessCraftingMonitorUseEnergy: Boolean = true

        @JvmField
        @Config(config = "server", category = "wirelessCraftingMonitor", key = "capacity", comment = "The energy capacity of the Wireless Crafting Monitor")
        var wirelessCraftingMonitorCapacity: Double = 3200.0

        @JvmField
        @Config(config = "server", category = "wirelessCraftingMonitor", key = "openUsage", comment = "The energy used by the Wireless Crafting Monitor to open")
        var wirelessCraftingMonitorOpenUsage: Double = 30.0

        @JvmField
        @Config(config = "server", category = "wirelessCraftingMonitor", key = "cancelUsage", comment = "The energy used by the Wireless Crafting Monitor to cancel a crafting task")
        var wirelessCraftingMonitorCancelUsage: Double = 5.0

        @JvmField
        @Config(config = "server", category = "wirelessCraftingMonitor", key = "cancelAllUsage", comment = "The energy used by the Wireless Crafting Monitor to cancel all crafting tasks")
        var wirelessCraftingMonitorCancelAllUsage: Double = 10.0

        // Autocrafting
        @JvmField
        @Config(config = "server", category = "autocrafting", key = "calculationTimeoutMs", comment = "The autocrafting calculation timeout in milliseconds, crafting tasks taking longer than this to calculate are cancelled to avoid server strain")
        var autocraftingCalculationTimeoutMs: Int = 5000
    }
}