package com.smarttoolfactory.post_detail.di

import androidx.fragment.app.Fragment
import com.smarttoolfactory.core.di.CoreModuleDependencies
import com.smarttoolfactory.post_detail.PostDetailFragment
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [CoreModuleDependencies::class],
    modules = [PostDetailModule::class]
)
interface PostDetailComponent {

    fun inject(postDetailFragment: PostDetailFragment)

    @Component.Factory
    interface Factory {
        fun create(
            dependentModule: CoreModuleDependencies,
            @BindsInstance fragment: Fragment
        ): PostDetailComponent
    }
}
