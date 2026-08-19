
package com.getech.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechScaffold(
    title: String,
    onMenu: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (onBack != null) IconButton(onClick = onBack) {
                        Text("‹", fontSize = 34.sp, color = MaterialTheme.colorScheme.primary)
                    } else if (onMenu != null) IconButton(onClick = onMenu) {
                        Text("☰", color = MaterialTheme.colorScheme.onSurface, fontSize = 22.sp)
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark()) Color(0xCC0B1220) else LightHeader
                )
            )
        },
        content = content
    )
}

@Composable
private fun isDark(): Boolean = MaterialTheme.colorScheme.background == Bg

@Composable
fun TechCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = .8f))
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            if (title != null) Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) Text(icon, fontSize = 22.sp)
                if (icon != null) Spacer(Modifier.width(10.dp))
                Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }
            content()
        }
    }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(9.dp)
    ) { Text(text, fontWeight = FontWeight.Bold) }
}

@Composable
fun SecondaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick, modifier = modifier,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        shape = RoundedCornerShape(9.dp)
    ) { Text(text) }
}

@Composable
fun DangerButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick, modifier = modifier,
        border = BorderStroke(1.dp, Red.copy(alpha=.5f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Red),
        shape = RoundedCornerShape(9.dp)
    ) { Text(text) }
}

@Composable
fun StatusBadge(text: String, ok: Boolean = true) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = (if (ok) Green else Red).copy(alpha = .12f),
        border = BorderStroke(1.dp, (if (ok) Green else Red).copy(alpha = .3f))
    ) {
        Text(
            text, color = if (ok) Green else Red,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize = 12.sp, fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatCard(number: String, label: String, modifier: Modifier = Modifier) {
    TechCard(modifier = modifier) {
        Text(number, color = MaterialTheme.colorScheme.primary, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 10.dp))
}
