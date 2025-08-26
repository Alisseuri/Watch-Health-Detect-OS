package com.chrisp.healthdetect.ui.oxygen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chrisp.healthdetect.R
import com.chrisp.healthdetect.ui.theme.HeartRateGreen
import com.chrisp.healthdetect.ui.theme.OxygenBlue
import com.chrisp.healthdetect.ui.util.InterpretationCard
import com.chrisp.healthdetect.ui.util.SectionTitle
import com.chrisp.healthdetect.ui.util.InterpretationCard
import com.chrisp.healthdetect.ui.util.DetailInterpretationSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OxygenDetailScreen(
    currentSpo2: Int,
    lastUpdateTimestamp: Long,
    onBackClick: () -> Unit
) {
    val interpretation = getOxygenInterpretation(currentSpo2)
    var isDetailExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Oxygen Level",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painterResource(id = R.drawable.arrow_left),
                            contentDescription = "Kembali",
                            tint = HeartRateGreen,
                            modifier = Modifier.height(60.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                MainSpo2Display(spo2 = currentSpo2, timestamp = lastUpdateTimestamp)

            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                SectionTitle(title = "Interpretasi")
                InterpretationCard(
                    text = interpretation.description
                )
            }

            item {
                DetailInterpretationSection(
                    title = "Interpretasi Data Oksigen",
                    borderColor = OxygenBlue,
                    isExpanded = isDetailExpanded,
                    onToggle = { isDetailExpanded = !isDetailExpanded },
                    tableContent = { OxygenInterpretationTable() }
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun OxygenDetailPreview() {
    OxygenDetailScreen(
        currentSpo2 = 98,
        lastUpdateTimestamp = System.currentTimeMillis(),
        onBackClick = {}
    )
}