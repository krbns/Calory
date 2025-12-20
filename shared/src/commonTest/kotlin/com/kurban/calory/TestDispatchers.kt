package com.kurban.calory

import com.kurban.calory.core.domain.AppDispatchers
import kotlinx.coroutines.test.TestDispatcher

fun testDispatchers(dispatcher: TestDispatcher) = AppDispatchers(
    io = dispatcher,
    main = dispatcher,
    default = dispatcher
)
