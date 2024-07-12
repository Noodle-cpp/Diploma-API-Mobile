package com.example.greensignal.presentation.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.greensignal.R
import com.example.greensignal.domain.model.response.Message
import com.example.greensignal.domain.model.response.MessageAttachment
import com.example.greensignal.domain.model.response.SavedFile
import com.example.greensignal.presentation.event.CreateIncidentEvent
import com.example.greensignal.presentation.event.MessageListEvent
import com.example.greensignal.presentation.event.SessionListEvent
import com.example.greensignal.presentation.state.MessageListState
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.element.IncomeMessageElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import java.util.Date

@Composable
fun MessageListScreen(navController: NavController,
                      state: MessageListState,
                      onEvent: (MessageListEvent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column {

            HeaderRowBackElement(
                navController,
                Screen.PersonalAccountScreen.route,
                stringResource(id = R.string.my_inspector_profile_title)
            )

            if(state.isLoading) {
                Spacer(modifier = Modifier.height(50.dp))

                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        modifier = Modifier,
                        fontSize = 20.sp,
                        text = stringResource(id = R.string.my_messages),
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = state.filter,
                            onValueChange = {
                                onEvent(MessageListEvent.FilterChanged(it))
                            },
                            label = { Text(stringResource(id = R.string.description)) },
                            maxLines = 1,
                            modifier = Modifier.align(Alignment.CenterVertically),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { onEvent(MessageListEvent.GetMessages) }
                            ),
                        )

                        Icon(
                            modifier = Modifier
                                .size(57.dp)
                                .align(Alignment.CenterVertically)
                                .clickable(onClick = {
                                    onEvent(MessageListEvent.GetMessages)
                                }),
                            imageVector = Icons.Default.Search,
                            contentDescription = "search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val messages: LazyPagingItems<Message> =
                        state.messages.collectAsLazyPagingItems()

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(messages.itemCount) { index ->
                            IncomeMessageElement(messages[index]!!, navController)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        messages.apply {
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
                                    val errorState = messages.loadState.refresh as LoadState.Error
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
                                    val errorState = messages.loadState.append as LoadState.Error
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
    }
}

@Composable
@Preview(showBackground = true)
fun MessageListScreenPreview() {
    GreenSignalTheme {
        MessageListScreen(navController = rememberNavController(),
            state = MessageListState(messages = MutableStateFlow(
                PagingData.from(
                listOf(
                    Message(
                        subject = "Привет мир",
                        fromAddress = "from@mail.ru",
                        createdAt = Date(),
                        seen = false,
                        content = "Съешь ещё этих мягких французских булок, да выпей же чаю",
                        messageAttachments = mutableListOf(
                            MessageAttachment(
                                savedFile = SavedFile()
                            ),
                            MessageAttachment(
                                savedFile = SavedFile()
                            )
                        )),
                    Message(
                        subject = "Пока мир",
                        fromAddress = "from@mail.ru",
                        createdAt = Date(),
                        seen = true,
                        content = "Text(\n" +
                                "                text = message.subject,\n" +
                                "                fontWeight = FontWeight.ExtraBold,\n" +
                                "                maxLines = 1,\n" +
                                "                fontSize = 20.sp,\n" +
                                "                overflow = TextOverflow.Ellipsis\n" +
                                "            )\n" +
                                "\n" +
                                "            if(!message.seen) {\n" +
                                "                Icon(\n" +
                                "                    imageVector = Icons.Default.Notifications,\n" +
                                "                    contentDescription = \"attachment\",\n" +
                                "                    modifier = Modifier.align(Alignment.End)\n" +
                                "                )\n" +
                                "            }",
                        messageAttachments = mutableListOf(
                            MessageAttachment(
                                savedFile = SavedFile()
                            ),
                        )),
            )))
            ),
            onEvent = {}
        )
    }
}