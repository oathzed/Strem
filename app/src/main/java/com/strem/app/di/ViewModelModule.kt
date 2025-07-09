package com.strem.app.di

import com.strem.app.ui.details.DetailsViewModel
import com.strem.app.ui.home.HomeViewModel
import com.strem.app.ui.player.PlayerViewModel
import com.strem.app.ui.search.SearchViewModel
import com.strem.app.ui.settings.SettingsViewModel
import com.strem.app.ui.sources.SourcesViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::DetailsViewModel)
    viewModelOf(::SourcesViewModel)
    viewModelOf(::PlayerViewModel)
    viewModelOf(::SettingsViewModel)
}