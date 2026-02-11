package com.kurban.calory.ios

import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.initKoinIos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.Koin
import org.koin.core.KoinApplication

class IosAppContainer internal constructor(
    val mainBridge: MainBridge,
    val profileBridge: ProfileBridge,
    val onboardingBridge: OnboardingBridge,
    val customFoodBridge: CustomFoodBridge,
    private val scopeHandle: ScopeDisposableHandle,
) : DisposableHandle {
    override fun dispose() {
        scopeHandle.dispose()
    }
}

fun createIosAppContainer(): IosAppContainer {
    val koin = obtainKoin()
    val dispatchers = koin.get<AppDispatchers>()
    val bridgeScope = CoroutineScope(SupervisorJob() + dispatchers.main)
    val scopeHandle = ScopeDisposableHandle(bridgeScope)

    return IosAppContainer(
        mainBridge = MainBridge(koin, bridgeScope),
        profileBridge = ProfileBridge(koin, bridgeScope),
        onboardingBridge = OnboardingBridge(koin, bridgeScope),
        customFoodBridge = CustomFoodBridge(koin, bridgeScope),
        scopeHandle = scopeHandle,
    )
}

private fun obtainKoin(): Koin {
    val app = koinApplication ?: initKoinIos().also { koinApplication = it }
    return app.koin
}

private var koinApplication: KoinApplication? = null
