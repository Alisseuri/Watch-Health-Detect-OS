package com.chrisp.healthdetect.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSearchDialog(
    users: List<UserDisplay>,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onUserSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = OxygenBlue),
            modifier = Modifier.fillMaxHeight(0.8f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(painterResource(id = R.drawable.exit), contentDescription = "Tutup", tint = Color.White)
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari data pengguna....") },
                    leadingIcon = { Icon(painterResource(id = R.drawable.search), contentDescription = "Cari") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(Color.White)
                ) {
                    items(users) { user ->
                        UserListItem(user = user, onClick = { onUserSelected(user.id) })
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun UserListItem(user: UserDisplay, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = user.name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            maxLines = 20,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = user.dobString,
            color = Color.Gray,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Preview(name = "Dialog Pencarian - Awal", showBackground = true, backgroundColor = 0x80000000)
@Composable
fun UserSearchDialogPreview() {
    // 1. Buat data palsu dengan tipe yang benar (UserDisplay)
    val dummyUsers = listOf(
        UserDisplay(id = "1", name = "Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", dobString = "09/09/1999"),
        UserDisplay(id = "2", name = "Budi Santoso", dobString = "15/03/1994"),
        UserDisplay(id = "3", name = "Citra Lestari", dobString = "21/11/1996"),
        UserDisplay(id = "4", name = "Dewi Anggraini", dobString = "05/07/1990"),
        UserDisplay(id = "5", name = "Eka Prasetya", dobString = "12/01/2001")
    )

        UserSearchDialog(
            users = dummyUsers,
            searchQuery = "",
            onQueryChange = {},
            onUserSelected = {},
            onDismiss = {}
        )
}

@Preview(name = "Dialog Pencarian - Terfilter", showBackground = true, backgroundColor = 0x80000000)
@Composable
fun UserSearchDialogFilteredPreview() {
    val dummyUsers = listOf(
        UserDisplay(id = "1", name = "Chris P.", dobString = "09/09/1999"),
        UserDisplay(id = "2", name = "Budi Santoso", dobString = "15/03/1994"),
        UserDisplay(id = "3", name = "Citra Lestari", dobString = "21/11/1996"),
        UserDisplay(id = "4", name = "Dewi Anggraini", dobString = "05/07/1990")
    )

        UserSearchDialog(
            users = dummyUsers.filter { it.name.contains("a", ignoreCase = true) },
            searchQuery = "a",
            onQueryChange = {},
            onUserSelected = {},
            onDismiss = {}
        )
}
