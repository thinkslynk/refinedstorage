package com.refinedmods.refinedstorage.apiimpl.network.security

import com.refinedmods.refinedstorage.api.network.security.ISecurityCard
import com.refinedmods.refinedstorage.api.network.security.Permission
import java.util.*

class SecurityCard(
        override val owner: UUID?
) : ISecurityCard {
    private val permissions: Map<Permission, Boolean> = EnumMap(Permission::class.java)

    override fun hasPermission(permission: Permission): Boolean {
        return permissions.getOrDefault(permission, true)
    }
}