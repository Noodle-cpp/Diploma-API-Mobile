package com.example.greensignal.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.greensignal.R
import com.example.greensignal.domain.model.response.Petition
import com.example.greensignal.presentation.event.PetitionListEvent
import com.example.greensignal.presentation.state.PetitionListState
import com.example.greensignal.presentation.ui.element.HeaderRowBackElement
import com.example.greensignal.presentation.ui.element.PetitionListElement
import com.example.greensignal.presentation.ui.navigation.Screen
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme
import kotlinx.coroutines.flow.Flow

@Composable
fun PetitionListScreen(navController: NavController,
                       state: PetitionListState,
                       onEvent: (PetitionListEvent) -> Unit) {
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                PetitionList(navController, state, state.petitions)
            }
        }
    }
}

@Composable
private fun PetitionList(navController: NavController,
                         state: PetitionListState,
                         items: Flow<PagingData<Petition>>
) {
    Box {
        if(state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(10.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        } else {
            ListColumn(items, navController)
        }
    }
}

@Composable
private fun ListColumn(items: Flow<PagingData<Petition>>,
                       navController: NavController) {

    val petitions: LazyPagingItems<Petition> = items.collectAsLazyPagingItems()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(petitions.itemCount) { index ->
            PetitionListElement(petitions[index]!!, navController)
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

@Composable
@Preview(showBackground = true)
fun PetitionListScreenPreview() {
    GreenSignalTheme {
        PetitionListScreen(navController = rememberNavController(), state = PetitionListState()) {
        }
    }
}