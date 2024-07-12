package com.example.greensignal.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.greensignal.R
import com.example.greensignal.data.remote.dto.response.PetitionKind
import com.example.greensignal.data.remote.dto.response.PetitionStatus
import com.example.greensignal.domain.model.response.IncidentReport
import com.example.greensignal.domain.model.response.Petition
import com.example.greensignal.domain.model.response.Message
import com.example.greensignal.domain.model.response.MessageAttachment
import com.example.greensignal.domain.model.response.SavedFile
import com.example.greensignal.presentation.event.MessageEvent
import com.example.greensignal.presentation.state.MessageState
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.element.PetitionListElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun MessageScreen(navController: NavController,
                  state: MessageState,
                  onEvent: (MessageEvent) -> Unit,
                  messageId: String) {
    LaunchedEffect(key1 = true) {
        onEvent(MessageEvent.GetMessage(messageId))
    }

    val showDialog =  remember { mutableStateOf(false) }

    if(showDialog.value) {
        AttachPetitionToMessage(onEvent, state, setShowDialog = { showDialog.value = it })
    }
    else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Column {

                HeaderRowBackElement(
                    navController,
                    Screen.MessageListScreen.route,
                    stringResource(id = R.string.my_messages)
                )

                if (state.isLoading) {
                    Spacer(modifier = Modifier.height(50.dp))

                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                } else if (state.error.isNullOrEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        Text(
                            modifier = Modifier,
                            text = state.message.subject,
                            style = MaterialTheme.typography.titleLarge,
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        Row {
                            Text(
                                text = SimpleDateFormat("dd.MM.yyyy").format(state.message.createdAt),
                                style = MaterialTheme.typography.bodyLarge,
                            )

                            Spacer(modifier = Modifier.width(25.dp))

                            Text(
                                text = state.message.fromName,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(25.dp))

                        Text(
                            modifier = Modifier,
                            style = MaterialTheme.typography.bodyLarge,
                            text = state.message.content,
                        )

                        Spacer(modifier = Modifier.height(25.dp))

                        if (state.message.messageAttachments.isNotEmpty()) AttachmentCard(
                            state.message.messageAttachments,
                            onEvent
                        )

                        Spacer(modifier = Modifier.height(45.dp))

                        Button(
                            onClick = {
                                showDialog.value = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Прикрепить к обращению",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }

                        Spacer(modifier = Modifier.height(15.dp))

                        Button(
                            onClick = {

                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonColors(
                                MaterialTheme.colorScheme.error,
                                MaterialTheme.colorScheme.surface,
                                Color.LightGray,
                                Color.Black
                            )
                        ) {
                            Text(
                                text = "Удалить",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        if (state.petition != null) {
                            Text(
                                text = "Прикреплённое обращение",
                                style = MaterialTheme.typography.bodyLarge,
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 2.dp,
                                color = Color.LightGray
                            )

                            PetitionListElement(
                                petition = state.petition,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttachmentCard(attachments: MutableList<MessageAttachment>, onEvent: (MessageEvent) -> Unit) {

    val columnHeight = attachments.size * 40
    val context = LocalContext.current
    ElevatedCard(modifier = Modifier
        .fillMaxWidth()
        .shadow(10.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(20.dp),
    ) {
        LazyColumn(modifier = Modifier
            .padding(15.dp)
            .height(columnHeight.dp)) {
            items(attachments.size) { index ->
                Column {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.clickable(onClick = {

                    })) {
                        Icon(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "attachment"
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(text = attachments[index].savedFile.origName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable(onClick = {
                                    onEvent(
                                        MessageEvent.DownloadFile(
                                            context,
                                            attachments[index].savedFile
                                        )
                                    )
                                })
                                .align(Alignment.CenterVertically)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun AttachPetitionToMessage(onEvent: (MessageEvent) -> Unit,
                            state: MessageState,
                            setShowDialog: (Boolean) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        val selectedPetition = remember {
            mutableStateOf<Petition?>(state.petition)
        }
        Column {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.primary)) {
                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                    ) {
                        Row(modifier = Modifier.clickable {
                            setShowDialog(false)
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
                                text = stringResource(id = R.string.message),
                                color = Color.White,
                            )
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
            Box(modifier = Modifier.padding(10.dp)) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(10.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                } else {
                    val petitions: LazyPagingItems<Petition> =
                        state.petitions.collectAsLazyPagingItems()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 0.dp, 0.dp, 100.dp)
                    ) {
                        items(petitions.itemCount) { index ->
                            Column(modifier = Modifier.clickable(onClick = {
                                selectedPetition.value = petitions[index]
                            })) {

                                Spacer(modifier = Modifier.height(15.dp))

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Icon(
                                        imageVector = when (petitions[index]!!.kind) {
                                            PetitionKind.AirPollution -> Icons.Default.Build
                                            PetitionKind.SoilPollution -> Icons.Default.Close
                                            PetitionKind.Excavation -> Icons.Default.AddCircle
                                            PetitionKind.Dump -> Icons.Default.AccountBox
                                            PetitionKind.TreeCutting -> Icons.Default.DateRange
                                            PetitionKind.Radiation -> Icons.Default.Notifications
                                        },
                                        contentDescription = "petition_kind_icon",
                                        modifier = Modifier
                                            .size(30.dp)
                                            .align(Alignment.CenterVertically),
                                        tint = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.width(20.dp))

                                    Column {

                                        Text(
                                            text = "Обращение №${petitions[index]!!.serialNumber}",
                                            style = MaterialTheme.typography.titleLarge,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = petitions[index]!!.kind.title,
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodyLarge,
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row {

                                            Text(
                                                text = SimpleDateFormat("dd.MM.yyyy").format(
                                                    petitions[index]!!.date
                                                ),
                                                style = MaterialTheme.typography.bodyLarge,
                                            )

                                            Spacer(modifier = Modifier.width(30.dp))

                                            if (petitions[index]!!.status == PetitionStatus.Sent) {
                                                Text(
                                                    text = "ожид. ответа ${
                                                        Date().day.minus(
                                                            petitions[index]!!.createdAt.day
                                                        )
                                                    } дней",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                )
                                            } else {
                                                Text(
                                                    text = petitions[index]!!.status.title,
                                                    color = when (petitions[index]!!.status) {
                                                        PetitionStatus.Draft -> Color.Gray
                                                        PetitionStatus.Sent -> Color.Gray
                                                        PetitionStatus.Replied -> Color.Blue
                                                        PetitionStatus.Success -> MaterialTheme.colorScheme.primary
                                                        PetitionStatus.Failed -> MaterialTheme.colorScheme.error
                                                        PetitionStatus.Archived -> MaterialTheme.colorScheme.error
                                                    },
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            }
                                        }
                                    }

                                    RadioButton(
                                        selected = if (selectedPetition.value != null && petitions[index] != null)
                                            (selectedPetition.value!!.id == petitions[index]!!.id) else false,
                                        onClick = {
                                            selectedPetition.value = petitions[index]
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(15.dp))

                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 2.dp,
                                    color = Color.LightGray
                                )
                            }
                        }
                        petitions.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier.fillParentMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.padding(10.dp),
                                                color = MaterialTheme.colorScheme.secondary,
                                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        }
                                    }
                                }

                                loadState.refresh is LoadState.Error -> {
                                    val errorState = petitions.loadState.refresh as LoadState.Error
                                    item {
                                        //ErrorItem(errorState.error.localizedMessage)
                                    }
                                }

                                loadState.append is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.padding(10.dp),
                                                color = MaterialTheme.colorScheme.secondary,
                                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        }
                                    }
                                }

                                loadState.append is LoadState.Error -> {
                                    val errorState = petitions.loadState.append as LoadState.Error
                                    item {
                                        //ErrorItem(errorState.error.localizedMessage)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        ElevatedCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .shadow(7.dp, RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .height(100.dp),
            colors = CardDefaults.cardColors(Color.White),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(modifier = Modifier.fillMaxHeight()) {
                Button(
                    onClick = {
                        onEvent(MessageEvent.AttachPetition(selectedPetition.value))
                        setShowDialog(false)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = "Прикрепить к обращению",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MessageScreenPreview() {
    GreenSignalTheme {
        MessageScreen(navController = rememberNavController(),
                        state = MessageState(message = Message(
                            subject = "Привет мир",
                            fromAddress = "from@mail.ru",
                            createdAt = Date(),
                            fromName = "Иванов Иван",
                            content = "Съешь ещё этих мягких французских булок, да выпей же чаю",
                            seen = false,
                            messageAttachments = mutableListOf(
                                MessageAttachment(
                                    savedFile = SavedFile(origName = "Файл номер 1.doc")
                                ),
                                MessageAttachment(
                                    savedFile = SavedFile(origName = "Файл номер 2.doc")
                                ),
                                MessageAttachment(
                                    savedFile = SavedFile(origName = "Файл номер 3.doc")
                                ),
                                MessageAttachment(
                                    savedFile = SavedFile(origName = "Файл номер 4.doc")
                                )
                            ),
                            petitionId = "123"
                        ),
                            petition = Petition(
                                serialNumber = "11-12", status = PetitionStatus.Sent, kind = PetitionKind.Dump,
                                incidentReport = IncidentReport(
                                    incident = com.example.greensignal.domain.model.response.Incident(
                                        address = "Улица Пушкина дом Калатушкина номер 1"
                                    )
                                ),
                                date = Date()
                            )
                        ),
                        onEvent = {},
                        messageId = "123")
    }
}