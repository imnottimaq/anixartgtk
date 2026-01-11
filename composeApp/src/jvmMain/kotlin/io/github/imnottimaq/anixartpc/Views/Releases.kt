package io.github.imnottimaq.anixartpc.Views

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import io.github.imnottimaq.anixartpc.DisplayImageFromInternet
import io.github.imnottimaq.anixartpc.Models
@Composable
fun ReleasesScreen(releases: List<Models.Release>, onReleaseClick: (Int) -> Unit) {
    LazyColumn {
        items(releases) { release: Models.Release ->
            Box(
                Modifier.clickable(onClick = { onReleaseClick(release.id) })
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
            ){
                Row(Modifier.fillMaxWidth()){
                    DisplayImageFromInternet(release.posterUrl)
                    Column() {
                        Row(){
                            Text(release.title)
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = release.grade.toString()
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(release.description.toString())
                    }
                }
            }
        }
    }
}
