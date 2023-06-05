package io.realm.curatedsyncexamples.fieldencryption.ui.dogs

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.realm.curatedsyncexamples.fieldencryption.models.Dog


//@Preview(showBackground = true)
//@Composable
//fun DogsScreenPreview() {
//    val dogs = MutableLiveData<List<Dog>>()
//    DogsScreen(dogs)
//}

@Composable
fun DogsScreen(
    dogsViewModel: DogsViewModel,
    onLogout: () -> Unit
) {
    val dogs by dogsViewModel.dogs.observeAsState(emptyList())

    LazyColumn {
        items(
            dogs,
            key = {
                it._id.toHexString()
            }
        ) { dog ->
            Text(
                text = dog.name!!.value
            )
        }
    }
}

@Composable
fun DogsList(dogList: List<Dog>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(
            dogList
        ) { dog ->
            Text(
                text = dog.name!!.value,
                modifier = modifier
            )
        }
    }
}