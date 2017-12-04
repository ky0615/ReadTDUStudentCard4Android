package moe.linux.boilerplate.di

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import moe.linux.boilerplate.util.view.FragmentNavigator

@Module
class FragmentModule(val fragment: Fragment) {

    @Provides
    fun provideContext(): Context = fragment.context!!

    @Provides
    fun provideFragmentManager(): FragmentManager = fragment.fragmentManager!!

    @Provides
    fun provideFragmentNavigator(activity: AppCompatActivity) = FragmentNavigator(activity, fragment)
}
