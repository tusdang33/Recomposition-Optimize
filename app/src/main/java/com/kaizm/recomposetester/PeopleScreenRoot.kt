package com.kaizm.recomposetester

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PeopleScreenRoot(
    viewModel: PeopleViewModel = remember { PeopleViewModel() },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PeopleScreen(
        state = state,
        action = viewModel::onAction
    )
}

@Composable
fun PeopleScreen(
    state: PeopleScreenState,
    action: (PeopleAction) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.people,
                        key = { it.id }
                    ) { person ->
                        PersonItem(
                            person = person,
                            onAction = {
                                action(PeopleAction.OnLoadDetail(person.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PersonItem(
    person: PersonUi,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = person.name,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        when {
            person.detail != null -> {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = person.detail.bio
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = person.detail.phoneNumber,
                    textAlign = TextAlign.Right,
                    fontSize = 12.sp
                )
            }

            person.isLoadingDetail -> {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = { person.detailLoadingProgress }
                )
            }

            else -> {
                TextButton(
                    onClick = onAction,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Load detail")
                }
            }
        }
    }
}
