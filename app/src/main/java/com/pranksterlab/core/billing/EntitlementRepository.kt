package com.pranksterlab.core.billing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface EntitlementRepository {
    val featureGate: Flow<FeatureGate>

    suspend fun refreshEntitlement()
}

class UnconfiguredEntitlementRepository : EntitlementRepository {
    private val gate = MutableStateFlow(FeatureGate.unconfiguredFree())

    override val featureGate: StateFlow<FeatureGate> = gate

    override suspend fun refreshEntitlement() {
        gate.value = FeatureGate.unconfiguredFree()
    }
}
