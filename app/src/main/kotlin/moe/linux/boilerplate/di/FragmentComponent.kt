package moe.linux.boilerplate.di

import dagger.Subcomponent
import moe.linux.boilerplate.di.scope.FragmentScope
import moe.linux.boilerplate.view.fragment.*

@FragmentScope
@Subcomponent(modules = arrayOf(
    FragmentModule::class
))
interface FragmentComponent {
    fun injectTo(fragment: CardMenuDialogFragment)
    fun injectTo(fragment: FrontFragment)
    fun injectTo(fragment: MainFragment)
    fun injectTo(fragment: QiitaListFragment)
    fun injectTo(fragment: DetailFragment)
}
