package com.example.greensignal.presentation.ui.element

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.greensignal.domain.model.response.Message
import com.example.greensignal.domain.model.response.MessageAttachment
import com.example.greensignal.domain.model.response.SavedFile
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun IncomeMessageElement(message: Message, navController: NavController) {
    ElevatedCard(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = {
            navController.navigate(Screen.MessageScreen.withArgs(message.id))
        }),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(20.dp),
        ) {
        Column(modifier = Modifier.padding(10.dp)) {

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = message.subject,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                if(!message.seen) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "attachment",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(35.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = message.fromName,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = SimpleDateFormat("dd.MM.yyyy").format(message.createdAt),
                    fontSize = 15.sp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    Icon(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "attachment"
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "${message.messageAttachments.size} вложений",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(name = "Message Element",
    showBackground = true)
fun IncomeMessageElementPreview() {
    GreenSignalTheme {
        IncomeMessageElement(message =
        Message(
            subject = "Привет мир",
            fromAddress = "from@mail.ru",
            createdAt = Date(),
            fromName = "Иванов Иван",
            content = "Съешь ещё этих мягких французских булок, да выпей же чаю",
            seen = false,
            messageAttachments = mutableListOf(
                MessageAttachment(
                    savedFile = SavedFile()
                    ),
                MessageAttachment(
                    savedFile = SavedFile()
                    )
            )),
            navController = rememberNavController()
        )
    }
}