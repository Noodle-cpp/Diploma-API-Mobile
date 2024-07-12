package com.example.greensignal.presentation.ui.element

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import okhttp3.Route

@Composable
fun HeaderRowBackElement(navController: NavController, route: String, title: String,
                         additionalTitle: String? = null, additionalIcon: ImageVector? = null, additionalRoute: String? = null) {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.primary)) {
        Spacer(modifier = Modifier.height(15.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                ) {
                Row(modifier = Modifier.clickable {
                    navController.navigate(route) {
                        popUpTo(route) { inclusive = true }
                    }
                }) {
                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back",
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        style = MaterialTheme.typography.titleMedium,
                        text = title,
                        color = Color.White,
                    )
                }
            }

            if (additionalTitle != null && additionalRoute != null) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.align(Alignment.CenterEnd)
                        .clickable {
                            navController.navigate(additionalRoute) {
                                popUpTo(additionalRoute) { inclusive = true }
                            }
                        }) {
                        if (additionalIcon != null) {
                            Icon(
                                imageVector = additionalIcon,
                                contentDescription = "additional_action",
                                tint = Color.White
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Text(
                            style = MaterialTheme.typography.titleMedium,
                            text = additionalTitle,
                            color = Color.White,
                        )

                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
@Preview(showBackground = true)
fun Preview() {
    GreenSignalTheme {
        HeaderRowBackElement(rememberNavController(), "", "Личный кабинет",
                "Изменить", Icons.Default.Edit, "")
    }
}