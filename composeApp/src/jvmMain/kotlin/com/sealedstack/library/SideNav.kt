package com.sealedstack.library

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SideNav(
    onNavEvent: (NavEvent) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()
        .combinedClickable(
            onClick = {
                onNavEvent.invoke(
                    NavEvent(
                        type = NavEventType.HOME
                    )
                )
            }
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.Home,
            contentDescription = "Home",
        )
        Text("Home")
    }
    Row(modifier = Modifier.fillMaxWidth()
        .combinedClickable(
            onClick = {
                onNavEvent.invoke(
                    NavEvent(
                        type = NavEventType.EXPLORE
                    )
                )
            }
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.Explore,
            contentDescription = "Explore",
        )
        Text("Explore")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onNavEvent.invoke(
                        NavEvent(
                            type = NavEventType.LIBRARY
                        )
                    )
                }
            )
    ) {
        Icon(
            imageVector = Icons.Outlined.LibraryMusic,
            contentDescription = "Library",
        )
        Text("Library")
    }
}
