package com.strem.app.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedTextField
import androidx.tv.material3.Switch
import androidx.tv.material3.Text
import com.strem.app.BuildConfig
import com.strem.app.R
import com.strem.app.data.database.entity.StremioAddonEntity
import com.strem.app.ui.components.ErrorView
import com.strem.app.ui.components.LoadingView
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                LoadingView()
            }
            uiState.error != null -> {
                ErrorView(
                    error = uiState.error!!,
                    onRetry = { /* Reload settings */ }
                )
            }
            else -> {
                SettingsContent(
                    uiState = uiState,
                    onResumePlaybackChanged = { viewModel.setResumePlayback(it) },
                    onCacheStreamsChanged = { viewModel.setCacheStreams(it) },
                    onAddStremioAddon = { viewModel.addStremioAddon(it) },
                    onRemoveStremioAddon = { viewModel.removeStremioAddon(it) },
                    onLoginToTrakt = { /* Open Trakt auth screen */ },
                    onLogoutFromTrakt = { viewModel.logoutFromTrakt() },
                    onSetTmdbApiKey = { viewModel.setTmdbApiKey(it) },
                    onSetTraktClientId = { viewModel.setTraktClientId(it) },
                    onSetTraktClientSecret = { viewModel.setTraktClientSecret(it) },
                    onClearCache = { viewModel.clearCache() },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    onResumePlaybackChanged: (Boolean) -> Unit,
    onCacheStreamsChanged: (Boolean) -> Unit,
    onAddStremioAddon: (String) -> Unit,
    onRemoveStremioAddon: (String) -> Unit,
    onLoginToTrakt: () -> Unit,
    onLogoutFromTrakt: () -> Unit,
    onSetTmdbApiKey: (String) -> Unit,
    onSetTraktClientId: (String) -> Unit,
    onSetTraktClientSecret: (String) -> Unit,
    onClearCache: () -> Unit,
    onBackClick: () -> Unit
) {
    var showAddAddonDialog by remember { mutableStateOf(false) }
    var showApiKeysDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Back button
            Button(
                onClick = onBackClick,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text("Back")
            }
            
            Text(
                text = stringResource(R.string.nav_settings),
                style = MaterialTheme.typography.headlineLarge
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Settings content
        TvLazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Trakt Integration
            item {
                SettingsSectionHeader(title = stringResource(R.string.trakt_integration))
                
                if (uiState.traktUsername != null) {
                    SettingsRow(
                        title = stringResource(R.string.logged_in_as, uiState.traktUsername),
                        action = {
                            Button(onClick = onLogoutFromTrakt) {
                                Text(stringResource(R.string.logout_from_trakt))
                            }
                        }
                    )
                } else {
                    SettingsRow(
                        title = stringResource(R.string.not_logged_in),
                        action = {
                            Button(onClick = onLoginToTrakt) {
                                Text(stringResource(R.string.login_to_trakt))
                            }
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Stremio Addons
            item {
                SettingsSectionHeader(title = stringResource(R.string.stremio_addons))
                
                SettingsRow(
                    title = stringResource(R.string.add_addon),
                    action = {
                        Button(onClick = { showAddAddonDialog = true }) {
                            Text(stringResource(R.string.add_addon))
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Stremio Addon List
            items(uiState.stremioAddons) { addon ->
                StremioAddonItem(
                    addon = addon,
                    onRemove = { onRemoveStremioAddon(addon.id) }
                )
            }
            
            // Playback Settings
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionHeader(title = stringResource(R.string.playback_settings))
                
                SettingsRow(
                    title = stringResource(R.string.resume_playback),
                    action = {
                        Switch(
                            checked = uiState.resumePlayback,
                            onCheckedChange = onResumePlaybackChanged
                        )
                    }
                )
                
                SettingsRow(
                    title = stringResource(R.string.cache_streams),
                    action = {
                        Switch(
                            checked = uiState.cacheStreams,
                            onCheckedChange = onCacheStreamsChanged
                        )
                    }
                )
                
                SettingsRow(
                    title = stringResource(R.string.clear_cache),
                    action = {
                        Button(onClick = onClearCache) {
                            Text(stringResource(R.string.clear_cache))
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // API Keys
            item {
                SettingsSectionHeader(title = stringResource(R.string.api_keys))
                
                SettingsRow(
                    title = stringResource(R.string.api_keys),
                    action = {
                        Button(onClick = { showApiKeysDialog = true }) {
                            Text(stringResource(R.string.api_keys))
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // About
            item {
                SettingsSectionHeader(title = stringResource(R.string.about))
                
                SettingsRow(
                    title = stringResource(R.string.version),
                    value = BuildConfig.VERSION_NAME
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    // Add Addon Dialog
    if (showAddAddonDialog) {
        AddAddonDialog(
            onAdd = { url ->
                onAddStremioAddon(url)
                showAddAddonDialog = false
            },
            onDismiss = { showAddAddonDialog = false }
        )
    }
    
    // API Keys Dialog
    if (showApiKeysDialog) {
        ApiKeysDialog(
            onSave = { tmdbApiKey, traktClientId, traktClientSecret ->
                if (tmdbApiKey.isNotEmpty()) {
                    onSetTmdbApiKey(tmdbApiKey)
                }
                if (traktClientId.isNotEmpty()) {
                    onSetTraktClientId(traktClientId)
                }
                if (traktClientSecret.isNotEmpty()) {
                    onSetTraktClientSecret(traktClientSecret)
                }
                showApiKeysDialog = false
            },
            onDismiss = { showApiKeysDialog = false }
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsRow(
    title: String,
    value: String? = null,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        value?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        action?.invoke()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun StremioAddonItem(
    addon: StremioAddonEntity,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = addon.name,
                style = MaterialTheme.typography.bodyLarge
            )
            
            addon.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        
        Button(onClick = onRemove) {
            Text(stringResource(R.string.delete))
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AddAddonDialog(
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var addonUrl by remember { mutableStateOf("") }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(500.dp)
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.add_addon),
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = addonUrl,
                onValueChange = { addonUrl = it },
                label = { Text(stringResource(R.string.addon_url)) },
                placeholder = { Text(stringResource(R.string.addon_url_hint)) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.cancel))
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Button(
                    onClick = { onAdd(addonUrl) },
                    modifier = Modifier.weight(1f),
                    enabled = addonUrl.isNotEmpty()
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ApiKeysDialog(
    onSave: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var tmdbApiKey by remember { mutableStateOf("") }
    var traktClientId by remember { mutableStateOf("") }
    var traktClientSecret by remember { mutableStateOf("") }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(500.dp)
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.api_keys),
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = tmdbApiKey,
                onValueChange = { tmdbApiKey = it },
                label = { Text(stringResource(R.string.tmdb_api_key)) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = traktClientId,
                onValueChange = { traktClientId = it },
                label = { Text(stringResource(R.string.trakt_client_id)) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = traktClientSecret,
                onValueChange = { traktClientSecret = it },
                label = { Text(stringResource(R.string.trakt_client_secret)) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.cancel))
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Button(
                    onClick = { onSave(tmdbApiKey, traktClientId, traktClientSecret) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}