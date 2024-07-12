package com.example.greensignal.presentation.ui.screen

import android.graphics.Bitmap
import android.graphics.Picture
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.greensignal.R
import com.example.greensignal.presentation.event.AuthenticationEvent
import com.example.greensignal.presentation.state.AuthenticationState
import com.example.greensignal.presentation.ui.element.AddPhotoElement
import com.example.greensignal.presentation.ui.element.DrawingField
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun AuthenticationScreen(
    navController: NavController,
    state: AuthenticationState,
    onEvent: (AuthenticationEvent) -> Unit,
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LaunchedEffect(key1 = state.isGetCodeSuccess ) {
            if (state.isGetCodeSuccess)
                Toast.makeText(
                    context,
                    context.getString(R.string.code_send_success),
                    Toast.LENGTH_LONG
                ).show()
        }
        LaunchedEffect(key1 = state.error) {
            state.error?.let { error ->
                Toast.makeText(
                    context,
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        if(state.isAuthorize) {
            when (state.selectedTabIndex) {
                0 -> {
                    navController.navigateUp()
                    navController.navigate(Screen.PersonalAccountScreen.route)
                    {
                        popUpTo(Screen.AuthenticationScreen.route) { inclusive = true }
                    }
                }
                1 -> {
                    RegistrationComplete(navController = navController)
                }
            }
        } else {
            MainScreen(navController = navController, state, onEvent)
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun MainScreen(navController: NavController, 
               state: AuthenticationState,
               onEvent: (AuthenticationEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        HeaderRowBackElement(navController = navController, route = Screen.HomeScreen.route, title = stringResource(id = R.string.home_screen))

        Column(modifier = Modifier.padding(15.dp)) {
            Spacer(modifier = Modifier.height(15.dp))

            Text(text = stringResource(id = R.string.inspector_authorization),
                style = MaterialTheme.typography.titleLarge
            )
        }

        TabRow(selectedTabIndex = state.selectedTabIndex) {
            state.tabOptions.forEachIndexed { index, title ->
                Tab(
                    selected = state.selectedTabIndex == index,
                    onClick = {  onEvent(AuthenticationEvent.TabChanged(index)) },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            when (state.selectedTabIndex) {
                0 -> {
                    LoginForm(onEvent, state)
                }
                1 -> {
                    RegistrationForm(onEvent = onEvent, state = state)
                }
            }
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun LoginForm(onEvent: (AuthenticationEvent) -> Unit,
              state: AuthenticationState) {
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = stringResource(id = R.string.app_name).uppercase(),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.padding(10.dp))

        OutlinedTextField(
            value = state.loginPhone,
            onValueChange = {
                if(it.length < 3) onEvent(AuthenticationEvent.PhoneChanged("+7 "))
                else if (it.length <= 16) onEvent(AuthenticationEvent.PhoneChanged(it))
            },
            label = { Text(stringResource(id = R.string.phone_number)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            isError = state.loginPhoneError != null
        )
        state.loginPhoneError?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Row {
            OutlinedTextField(
                value = state.loginCode,
                onValueChange = {
                    if (it.length <= 4) onEvent(AuthenticationEvent.CodeChanged(it))
                },
                label = { Text(stringResource(id = R.string.sms_code)) },
                singleLine = true,
                modifier = Modifier.width(150.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                shape = RoundedCornerShape(
                    topStart = 5.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp,
                    bottomStart = 5.dp,
                ),
                isError = state.loginCodeError != null
            )

            Button(
                onClick = {
                    onEvent(AuthenticationEvent.GetCode)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp, 0.dp, 0.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 5.dp,
                    bottomEnd = 5.dp,
                    bottomStart = 0.dp,
                ),
            ) {
                if (state.isCodeLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(2.dp)
                            .align(Alignment.CenterVertically),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.get_code),
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }
        }

        Spacer(modifier = Modifier.padding(15.dp))

        Button(
            onClick = {
                onEvent(AuthenticationEvent.Authentication)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
        ) {
            if(state.isLoading)
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(2.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            else Text(
                    text = stringResource(id = R.string.authorize),
                    style = MaterialTheme.typography.bodyLarge,
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun RegistrationForm(onEvent: (AuthenticationEvent) -> Unit,
                     state: AuthenticationState) {
    val focusManager = LocalFocusManager.current
    val picture = remember { Picture() }

    fun createBitmapFromPicture(picture: Picture): Bitmap {
        val bitmap = Bitmap.createBitmap(
            picture.width,
            picture.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawPicture(picture)
        return bitmap
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(id = R.string.app_name).uppercase(),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.padding(10.dp))

        OutlinedTextField(
            value = state.registrationFIO,
            onValueChange = {
                onEvent(AuthenticationEvent.FioChanged(it))
            },
            label = { Text(stringResource(id = R.string.user_fio)) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            isError = state.registrationFIOError != null
        )
        state.registrationFIOError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
        ) {

            AddPhotoElement(file = state.registrationPhotoUri)

            Spacer(modifier = Modifier.width(10.dp))

            if (state.registrationPhotoUri.value != Uri.EMPTY) {
                Text(
                    text = "Файл загружен",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge,
                )
            } else {
                state.registrationPhotoUriError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(10.dp))

        OutlinedTextField(
            value = state.registrationCertificateId.take(8),
            onValueChange = {
                onEvent(AuthenticationEvent.CertificateIdChanged(it))
            },
            label = { Text(stringResource(id = R.string.user_cert_id)) },
            placeholder = { Text("123-4567") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = state.registrationCertificateIdError != null
        )
        state.registrationCertificateIdError?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
        ) {

            AddPhotoElement(file = state.registrationCertificateUri)

            Spacer(modifier = Modifier.width(10.dp))

            if (state.registrationCertificateUri.value != Uri.EMPTY) {
                Text(
                    text = "Файл загружен",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                state.registrationCertificateUriError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(10.dp))

        val isOpen = remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
        ) {
            OutlinedTextField(
                readOnly = true,
                value = state.registrationCertificateDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                label = { Text(stringResource(id = R.string.user_cert_date)) },
                onValueChange = {
                },
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    isOpen.value = true
                                }
                            }
                        }
                    }
            )

            Spacer(modifier = Modifier.width(10.dp))

            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    isOpen.value = true
                }
            ) {
                Icon(
                    modifier = Modifier.size(60.dp),
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Calendar"
                )
            }
        }

        if (isOpen.value) {
            val calendar = Calendar.getInstance()
            calendar.set(
                LocalDate.now().year,
                LocalDate.now().month.value,
                LocalDate.now().dayOfMonth
            )

            val datePickerState =
                rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)

            DatePickerDialog(
                onDismissRequest = { },
                confirmButton = {
                    Button(onClick = {
                        onEvent(
                            AuthenticationEvent.CertificateDateChanged(
                                LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(datePickerState.selectedDateMillis!!),
                                    ZoneId.systemDefault()
                                )
                            )
                        )
                        isOpen.value = false
                    }) {
                        Text(
                            style = MaterialTheme.typography.bodyLarge,
                            text = stringResource(id = R.string.apply)
                        )
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        isOpen.value = false
                    }) {
                        Text(stringResource(id = R.string.close))
                    }
                }
            ) {
                DatePicker(state = datePickerState,
                    title = {
                        Text(
                            text = stringResource(id = R.string.select_date_cert),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(
                                PaddingValues(
                                    start = 24.dp,
                                    end = 12.dp,
                                    top = 16.dp
                                )
                            )
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.padding(10.dp))

        OutlinedTextField(
            value = state.registrationSchoolId.take(12),
            onValueChange = {
                onEvent(AuthenticationEvent.SchoolIdChanged(it))
            },
            label = { Text(stringResource(id = R.string.user_school_id)) },
            placeholder = { Text("123-45678-90") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = state.registrationSchoolIdError != null
        )
        state.registrationSchoolIdError?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.padding(10.dp))

        OutlinedTextField(
            value = state.registrationPhone,
            onValueChange = {
                if (it.length < 3) onEvent(AuthenticationEvent.PhoneChanged("+7 "))
                else if (it.length <= 16) onEvent(AuthenticationEvent.PhoneChanged(it))
            },
            label = { Text(stringResource(id = R.string.phone_number)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            isError = state.registrationPhoneError != null
        )
        state.registrationPhoneError?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.padding(15.dp))

        DrawingField(picture, false, {})

        Spacer(modifier = Modifier.padding(15.dp))

        Row {
            OutlinedTextField(
                value = state.registrationCode,
                onValueChange = {
                    if (it.length <= 4) onEvent(AuthenticationEvent.CodeChanged(it))
                },
                label = { Text(stringResource(id = R.string.sms_code)) },
                singleLine = true,
                modifier = Modifier.width(150.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                shape = RoundedCornerShape(
                    topStart = 5.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp,
                    bottomStart = 5.dp,
                ),
                isError = state.registrationCodeError != null
            )

            Button(
                onClick = {
                    onEvent(AuthenticationEvent.GetCode)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp, 0.dp, 0.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 5.dp,
                    bottomEnd = 5.dp,
                    bottomStart = 0.dp,
                ),
            ) {
                if (state.isCodeLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(2.dp)
                            .align(Alignment.CenterVertically),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.get_code),
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(15.dp))

        Button(
            onClick = {
                onEvent(AuthenticationEvent.CreateInspector(createBitmapFromPicture(picture)))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
        ) {
            if (state.isLoading)
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(2.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            else Text(
                text = stringResource(id = R.string.registration),
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Spacer(modifier = Modifier.padding(15.dp))
    }
}

@Composable
fun RegistrationComplete(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = stringResource(id = R.string.registration_congrats),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                navController.navigateUp()
                navController.navigate(Screen.PersonalAccountScreen.route) {
                    popUpTo(Screen.AuthenticationScreen.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.ok_text),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Login",
    showBackground = true)
@Composable
fun GreenSignalLoginScreenPreview() {
    GreenSignalTheme {
        AuthenticationScreen(navController = rememberNavController(),
            state = AuthenticationState(),
            onEvent = {})
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Login",
    showBackground = true)
@Composable
fun GreenSignalLoginScreenCodeLoadingPreview() {
    GreenSignalTheme {
        AuthenticationScreen(navController = rememberNavController(),
            state = AuthenticationState(isCodeLoading = true),
            onEvent = {})
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Login",
    showBackground = true)
@Composable
fun GreenSignalLoginScreenLoadingPreview() {
    GreenSignalTheme {
        AuthenticationScreen(navController = rememberNavController(),
            state = AuthenticationState(isLoading = true),
            onEvent = {})
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Login",
    showBackground = true,
    heightDp = 1500)
@Composable
fun GreenSignalRegistrationPreview() {
    GreenSignalTheme {
        AuthenticationScreen(navController = rememberNavController(),
            state = AuthenticationState(selectedTabIndex = 1),
            onEvent = {})
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Login",
    showBackground = true,
    heightDp = 1500)
@Composable
fun GreenSignalCodeLoadingRegistrationPreview() {
    GreenSignalTheme {
        AuthenticationScreen(navController = rememberNavController(),
            state = AuthenticationState(isCodeLoading = true, selectedTabIndex = 1),
            onEvent = {})
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Login",
    showBackground = true,
    heightDp = 1500)
@Composable
fun GreenSignalLoadingRegistrationPreview() {
    GreenSignalTheme {
        AuthenticationScreen(navController = rememberNavController(),
            state = AuthenticationState(selectedTabIndex = 1, isLoading = true),
            onEvent = {})
    }
}

@Preview("Green Signal Login",
    showBackground = true)
@Composable
fun GreenSignalCompleteRegistrationPreview() {
    GreenSignalTheme {
        RegistrationComplete(navController = rememberNavController())
    }
}
