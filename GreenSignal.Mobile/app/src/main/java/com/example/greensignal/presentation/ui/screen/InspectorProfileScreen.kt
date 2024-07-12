package com.example.greensignal.presentation.ui.screen

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.greensignal.R
import com.example.greensignal.data.remote.dto.response.InspectorStatus
import com.example.greensignal.presentation.event.InspectorAccountEvent
import com.example.greensignal.presentation.state.InspectorProfileState
import com.example.greensignal.presentation.ui.dialog.ConfirmationDialog
import com.example.greensignal.presentation.ui.dialog.FullScreenPhotoDialog
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme

@Composable
fun InspectorProfileScreen (navController: NavController,
                            state: InspectorProfileState,
                            onEvent: (InspectorAccountEvent) -> Unit) {

    val showDialog =  remember { mutableStateOf(false) }
    val showPhotoDialog = remember { mutableStateOf(false) }
    val showCertDialog = remember { mutableStateOf(false) }
    val showSignatureDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    if(showPhotoDialog.value)
        FullScreenPhotoDialog(setShowDialog = {
            showPhotoDialog.value = it
        }, state.photoFile!!)

    if(showCertDialog.value)
        FullScreenPhotoDialog(setShowDialog = {
            showCertDialog.value = it
        }, state.certFile!!)

    if(showSignatureDialog.value)
        FullScreenPhotoDialog(setShowDialog = {
            showSignatureDialog.value = it
        }, state.signatureFile!!)

    if(showDialog.value) {
        ConfirmationDialog(
            show = showDialog.value,
            onDismiss = { showDialog.value = false },
            onConfirm = {
                onEvent(InspectorAccountEvent.Logout)

                navController.navigate(Screen.HomeScreen.route) {
                    popUpTo(Screen.HomeScreen.route) { inclusive = true }
                }

                showDialog.value = false
            }
        )
    }

    LaunchedEffect(key1 = !state.isAuthorized) {
        if(!state.isAuthorized) {
            navController.navigate(Screen.AuthenticationScreen.route) {
                popUpTo(Screen.AuthenticationScreen.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(10.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        } else if (state.error != null) {

            HeaderRowBackElement(navController = navController, route = Screen.HomeScreen.route, title = stringResource(
                id = R.string.my_inspector_profile_title
            ))

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(10.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            onEvent(InspectorAccountEvent.GetInspectorProfile)
                        },
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "сard_refresh",
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = state.error.toString(),
                    modifier = Modifier,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                HeaderRowBackElement(navController = navController, route = Screen.PersonalAccountScreen.route, title = stringResource(
                    id = R.string.my_inspector_profile_title
                ))

                Box(modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxWidth()
                ) {
                    Column {

                        Text(
                            modifier = Modifier.padding(15.dp),
                            color = Color.White,
                            text = stringResource(id = R.string.account),
                            style = MaterialTheme.typography.titleLarge,
                        )

                        var refreshKey by remember { mutableStateOf(0) }

                        Row(modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(10.dp)) {

                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(state.photoFile)
                                    .crossfade(true)
                                    .build(),
                                loading = {
                                    CircularProgressIndicator(
                                        modifier = Modifier.padding(20.dp)
                                    )
                                    onEvent(InspectorAccountEvent.PhotoStateChanged(false))
                                },
                                error = {
                                    onEvent(InspectorAccountEvent.PhotoStateChanged(false))
                                    Icon(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp),
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Photo exception",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                success = {
                                    SubcomposeAsyncImageContent()
                                    onEvent(InspectorAccountEvent.PhotoStateChanged(true))
                                },
                                contentDescription = "photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .size(100.dp)
                                    .clickable(onClick = {
                                        if (state.isPhotoLoaded) {
                                            showPhotoDialog.value = true
                                        } else {
                                            onEvent(InspectorAccountEvent.UpdateInspectorProfilePhoto)
                                        }
                                    })
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                                Text(
                                    text = state.status.value,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.surface,
                                    textDecoration = TextDecoration.Underline
                                )

                                Text(
                                    text = state.fio,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                )
                            }
                        }
                    }
                }

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)) {
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                        .background(Color.White)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())) {

                        Row(modifier = Modifier
                            .align(Alignment.Center)
                            .padding(30.dp)
                        ) {
                            MenuBlock(onClickAction = {
                                navController.navigate(Screen.UpdateInspectorScreen.route)
                            },
                                icon = Icons.Default.Edit,
                                text = stringResource(id = R.string.change),
                                iconColor = MaterialTheme.colorScheme.secondary,
                                backgroundColor = CardDefaults.cardColors(Color.White))

                            Spacer(modifier = Modifier.width(30.dp))

                            MenuBlock(onClickAction = {
                                navController.navigate(Screen.SessionListScreen.route)
                            },
                                icon = Icons.AutoMirrored.Filled.List,
                                text = stringResource(id = R.string.session_list),
                                iconColor = MaterialTheme.colorScheme.primary,
                                backgroundColor = CardDefaults.cardColors(Color.White))

                            Spacer(modifier = Modifier.width(30.dp))

                            MenuBlock(onClickAction = {
                                showDialog.value = true
                            },
                                icon = Icons.AutoMirrored.Filled.ExitToApp,
                                text = stringResource(id = R.string.user_exit),
                                iconColor = Color.White,
                                backgroundColor = CardDefaults.cardColors(MaterialTheme.colorScheme.error))
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )


                Column(modifier = Modifier.padding(10.dp)) {

                    TextElement(stringResource(id = R.string.phone_number), state.phone)
                    TextElement(stringResource(id = R.string.user_cert_id), state.certificateId)
                    TextElement(stringResource(id = R.string.user_cert_date), state.certificateDate)
                    TextElement(stringResource(id = R.string.user_school_id), state.schoolId)

                    Spacer(modifier = Modifier.height(15.dp))

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = stringResource(id = R.string.documents),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .horizontalScroll(rememberScrollState())) {

                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(state.certFile)
                                .crossfade(true)
                                .build(),
                            loading = {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(20.dp)
                                )
                            },
                            error = {
                                onEvent(InspectorAccountEvent.CertStateChanged(false))
                                Icon(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Photo exception",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            success = {
                                SubcomposeAsyncImageContent()
                                onEvent(InspectorAccountEvent.CertStateChanged(true))
                            },
                            contentDescription = "photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .background(Color.White)
                                .size(125.dp)
                                .clickable(onClick = {
                                    if (state.isCertLoaded) {
                                        showCertDialog.value = true
                                    } else {
                                        onEvent(InspectorAccountEvent.UpdateInspectorProfileCert)
                                    }
                                })
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(state.signatureFile)
                                .crossfade(true)
                                .build(),
                            loading = {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(20.dp)
                                )
                            },
                            error = {
                                onEvent(InspectorAccountEvent.SignatureStateChanged(false))
                                Icon(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Photo exception",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            success = {
                                SubcomposeAsyncImageContent()
                                onEvent(InspectorAccountEvent.SignatureStateChanged(true))
                            },
                            contentDescription = "signature",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .background(Color.White)
                                .size(125.dp)
                                .clickable(onClick = {
                                    if (state.isSignatureLoaded) {
                                        showSignatureDialog.value = true
                                    } else {
                                        onEvent(InspectorAccountEvent.UpdateInspectorProfileCert)
                                    }
                                })
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun TextElement(title: String, value: String) {
    Spacer(modifier = Modifier.height(25.dp))

    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = value,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun MenuBlock(onClickAction: () -> Unit,
              icon: ImageVector,
              text: String,
              iconColor: Color,
              backgroundColor: CardColors
) {
    ElevatedCard(
        onClick = { onClickAction() },
        colors = backgroundColor,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .size(100.dp)
            .shadow(10.dp, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(50.dp),
                imageVector = icon,
                contentDescription = "MenuCard",
                tint = iconColor
            )

            Text(text = text,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = iconColor)
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Account",
    showBackground = true,
    heightDp = 1000)
@Composable
fun ProfileScreenPreview() {
    GreenSignalTheme {
        InspectorProfileScreen(
            navController = rememberNavController(),
            state = InspectorProfileState(fio = "Шибанова Валентина Сергеевна",
                phone = "+7 967 604 91 26",
                status = InspectorStatus.Active,
                certificateId = "333 3333",
                schoolId = "333 33333 33"),
            onEvent = {}
        )
    }
}
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Account",
    showBackground = true,
    heightDp = 1000)
@Composable
fun ProfileScreenPreviewLoading() {
    GreenSignalTheme {
        InspectorProfileScreen(
            navController = rememberNavController(),
            state = InspectorProfileState(fio = "Шибанова Валентина Сергеевна",
                phone = "+7 967 604 91 26",
                status = InspectorStatus.Active,
                certificateId = "333 3333",
                schoolId = "333 33333 33",
                isLoading = true),
            onEvent = {}
        )
    }
}
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Preview("Green Signal Account",
    showBackground = true,
    heightDp = 1000)
@Composable
fun ProfileScreenPreviewError() {
    GreenSignalTheme {
        InspectorProfileScreen(
            navController = rememberNavController(),
            state = InspectorProfileState(fio = "Шибанова Валентина Сергеевна",
                phone = "+7 967 604 91 26",
                certificateDate = "20.09.2023",
                status = InspectorStatus.Active,
                certificateId = "333 3333",
                schoolId = "333 33333 33",
                error = "Ошибочка выскочила :("),
            onEvent = {}
        )
    }
}