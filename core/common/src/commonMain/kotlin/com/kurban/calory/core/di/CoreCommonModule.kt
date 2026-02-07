package com.kurban.calory.core.di

import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.ui.time.DayProvider
import com.kurban.calory.core.ui.time.DefaultDayProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val coreCommonModule = module {
    single { AppDispatchers(io = Dispatchers.IO, main = Dispatchers.Main, default = Dispatchers.Default) }
    single<DayProvider> { DefaultDayProvider() }
}
