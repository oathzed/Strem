package com.strem.app.di

import com.strem.app.data.repository.ContentRepository
import com.strem.app.data.repository.PlaybackRepository
import com.strem.app.data.repository.StremioRepository
import com.strem.app.data.repository.TraktRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::ContentRepository)
    singleOf(::TraktRepository)
    singleOf(::StremioRepository)
    singleOf(::PlaybackRepository)
}