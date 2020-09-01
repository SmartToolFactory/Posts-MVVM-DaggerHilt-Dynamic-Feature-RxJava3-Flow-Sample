// package com.smarttoolfactory.domain.di
//
// import com.smarttoolfactory.data.repository.PostStatusRepository
// import com.smarttoolfactory.domain.usecase.GetPostListUseCaseFlow
// import com.smarttoolfactory.domain.usecase.GetPostListUseCaseRxJava3
// import com.smarttoolfactory.domain.usecase.GetPostsWithStatusUseCaseFlow
// import dagger.hilt.EntryPoint
// import dagger.hilt.InstallIn
// import dagger.hilt.android.components.ApplicationComponent
//
// @EntryPoint
// @InstallIn(ApplicationComponent::class)
// interface DomainModuleDependencies {
//
//    /*
//     Provision methods to provide dependencies to components that depend on this component
//   */
//
//    fun postStatusRepository(): PostStatusRepository
//
//    fun getPostListUseCaseFlow(): GetPostListUseCaseFlow
//    fun getPostListUseCaseRxJava3(): GetPostListUseCaseRxJava3
//    fun getPostsWithStatusUseCaseFlow(): GetPostsWithStatusUseCaseFlow
// }
