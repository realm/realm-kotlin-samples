package io.realm.curatedsyncexamples.fieldencryption.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.curatedsyncexamples.fieldencryption.models.Dog


class LoginViewModel : ViewModel() {
    val dogs: MutableLiveData<List<Dog>> by lazy {
        MutableLiveData<List<Dog>>()
    }
}