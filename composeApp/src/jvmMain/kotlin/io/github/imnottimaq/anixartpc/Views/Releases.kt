package io.github.imnottimaq.anixartpc.Views

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import io.github.imnottimaq.anixartpc.DisplayImageFromInternet
import io.github.imnottimaq.anixartpc.Models
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun ReleasesScreen(releases: List<Models.Release>, onReleaseClick: (Int) -> Unit) {
    LazyColumn {
        items(releases) { release: Models.Release ->
            Box(
                modifier = Modifier
                    .clickable(onClick = { onReleaseClick(release.id) })
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
            ){
                Row(
                    modifier = Modifier.fillMaxWidth()
                ){
                    DisplayImageFromInternet(release.posterUrl)
                    Spacer(Modifier.width(10.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = release.title,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Rating",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "%.2f".format(release.grade),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = release.description.toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
