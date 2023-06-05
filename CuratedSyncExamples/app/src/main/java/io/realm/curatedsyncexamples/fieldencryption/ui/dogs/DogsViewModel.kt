package io.realm.curatedsyncexamples.fieldencryption.ui.dogs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.curatedsyncexamples.fieldencryption.models.Dog


class DogsViewModel : ViewModel() {
    val dogs: MutableLiveData<List<Dog>> by lazy {
        MutableLiveData<List<Dog>>()
    }
}