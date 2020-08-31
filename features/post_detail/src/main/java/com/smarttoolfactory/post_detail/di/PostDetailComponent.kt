package com.smarttoolfactory.post_detail.di

import androidx.fragment.app.Fragment
import com.smarttoolfactory.domain.di.DomainModuleDependencies
import com.smarttoolfactory.post_detail.PostDetailFragment
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [DomainModuleDependencies::class],
    modules = [PostDetailModule::class]
)
interface PostDetailComponent {

    fun inject(postDetailFragment: PostDetailFragment)

    @Component.Factory
    interface Factory {
        fun create(
            dependentModule: DomainModuleDependencies,
            @BindsInstance fragment: Fragment
        ): PostDetailComponent
    }
}
