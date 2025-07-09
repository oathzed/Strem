package com.strem.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.strem.app.data.database.entity.ContentEntity

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ContentRow(
    title: String,
    items: List<ContentEntity>,
    onItemClick: (ContentEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TvLazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            items(items) { item ->
                ContentCard(
                    content = item,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.width(160.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}