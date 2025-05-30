package com.example.greensignal.presentation.ui.element

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.greensignal.R
import com.example.greensignal.data.remote.dto.response.IncidentKind
import com.example.greensignal.presentation.event.CreateIncidentEvent
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalAccountMenuElement(title: String, iconName: String, icon: ImageBitmap, route: String, navController: NavController) {
    ElevatedCard(modifier = Modifier
        .width(160.dp)
        .height(200.dp)
        .shadow(7.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(20.dp),
        onClick = {
            navController.navigate(route)
        }
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Image(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(120.dp),
            bitmap = icon,
            contentDescription = iconName,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Text(text = title,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(5.dp),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary)
    }
}

@Preview(showBackground = true)
@Composable
fun PersonalAccountMenuElementPreview() {
    GreenSignalTheme {
        PersonalAccountMenuElement(
            title =  "Профиль",
            iconName =  "Profile",
            icon = ImageBitmap.imageResource(R.drawable.profile),
            route = "",
            navController = rememberNavController()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PersonalAccountMenuSelectPreview() {
    GreenSignalTheme {
        PersonalAccountMenuElement(
            title =  "Профиль",
            iconName =  "Profile",
            icon = ImageBitmap.imageResource(R.drawable.profile),
            route = "",
            navController = rememberNavController()
        )
    }
}