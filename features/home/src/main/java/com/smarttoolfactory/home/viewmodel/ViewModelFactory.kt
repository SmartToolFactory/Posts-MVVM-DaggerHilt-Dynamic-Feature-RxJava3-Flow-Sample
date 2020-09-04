package com.smarttoolfactory.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.domain.usecase.GetPostsWithStatusUseCaseFlow
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope

// TODO Add SavedStateHandle to ViewModelFactory

class ViewModelFactory

class PostStatusViewModelFactory @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val getPostsUseCase: GetPostsWithStatusUseCaseFlow
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass != PostStatusViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        return PostStatusViewModel(
            coroutineScope,
            getPostsUseCase
        ) as T
    }
}
