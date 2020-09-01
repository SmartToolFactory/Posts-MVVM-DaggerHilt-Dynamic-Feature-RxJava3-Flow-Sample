// package com.smarttoolfactory.domain.di
//
// import com.smarttoolfactory.domain.dispatcher.UseCaseDispatchers
// import dagger.Module
// import dagger.Provides
// import dagger.hilt.InstallIn
// import dagger.hilt.android.components.ApplicationComponent
// import kotlinx.coroutines.Dispatchers
//
// @Module
// @InstallIn(ApplicationComponent::class)
// class DomainModule {
//
//    @Provides
//    fun provideUseCaseDispatchers(): UseCaseDispatchers {
//        return UseCaseDispatchers(Dispatchers.IO, Dispatchers.Default, Dispatchers.Main)
//    }
// }
