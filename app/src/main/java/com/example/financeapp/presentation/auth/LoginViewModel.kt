package com.example.financeapp.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _state = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> = _state

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            val result = repository.signInWithGoogle(account)
            _state.value = if (result.isSuccess) LoginState.Success
            else LoginState.Error(result.exceptionOrNull()?.message ?: "Sign-in failed")
        }
    }
}