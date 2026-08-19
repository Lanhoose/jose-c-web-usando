package com.getech.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TechScaffold(
    title: String,
    onMenu: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                title = { Text(title, color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (onBack != null) IconButton(onClick = onBack) {
                        Text("‹", fontSize = MaterialTheme.typography.headlineLarge.fontSize, color = Cyan)
                    } else if (onMenu != null) IconButton(onClick = onMenu) {
                        Text("☰", color = TextPrimary, fontSize = MaterialTheme.typography.titleLarge.fontSize)
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xCC0B1220))
            )
        },
        content = content
    )
}

@Composable
fun TechCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Card),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border.copy(alpha=.8f))
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            if (title != null) Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) Text(icon, fontSize = MaterialTheme.typography.titleLarge.fontSize)
                Spacer(Modifier.width(if (icon != null) 10.dp else 0.dp))
                Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            content()
        }
    }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Blue, contentColor = Color.White),
        shape = RoundedCornerShape(9.dp)
    ) { Text(text, fontWeight = FontWeight.Bold) }
}

@Composable
fun SecondaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        border = androidx.compose.foundation.BorderStroke(1.dp, Border),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
        shape = RoundedCornerShape(9.dp)
    ) { Text(text) }
}

@Composable
fun StatCard(number: String, label: String, modifier: Modifier = Modifier) {
    TechCard(modifier = modifier.widthIn(min=145.dp)) {
        Text(number, color = Cyan, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = TextSecondary)
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.headlineSmall, color = TextPrimary,
        modifier = Modifier.padding(vertical = 10.dp))
}
