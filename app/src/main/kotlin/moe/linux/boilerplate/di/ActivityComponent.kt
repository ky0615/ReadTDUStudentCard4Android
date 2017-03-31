package moe.linux.boilerplate.di

import dagger.Subcomponent
import moe.linux.boilerplate.di.scope.ActivityScope
import moe.linux.boilerplate.view.activity.CardOptionActivity
import moe.linux.boilerplate.view.activity.MainActivity

@ActivityScope
@Subcomponent(modules = arrayOf(
    ActivityModule::class
))
interface ActivityComponent {
    fun injectTo(activity: MainActivity)

    fun injectTo(activity: CardOptionActivity)

    fun plus(module: FragmentModule): FragmentComponent
}
