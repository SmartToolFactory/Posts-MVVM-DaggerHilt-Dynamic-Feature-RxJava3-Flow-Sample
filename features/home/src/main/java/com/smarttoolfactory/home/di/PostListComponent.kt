package com.smarttoolfactory.home.di

import androidx.fragment.app.Fragment
import com.smarttoolfactory.core.di.CoreModuleDependencies
import com.smarttoolfactory.home.postlist.PostListFlowFragment
import com.smarttoolfactory.home.postlist.PostListRxJava3Fragment
import com.smarttoolfactory.home.postlist.PostListWithStatusFragment
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [CoreModuleDependencies::class],
    modules = [PostListModule::class]
)
interface PostListComponent {

    fun inject(postListWithStatusFragment: PostListWithStatusFragment)
    fun inject(postListFlowFragment: PostListFlowFragment)
    fun inject(postListRxJava3Fragment: PostListRxJava3Fragment)

    @Component.Factory
    interface Factory {
        fun create(
            dependentModule: CoreModuleDependencies,
            @BindsInstance fragment: Fragment
        ): PostListComponent
    }
}
