package moe.linux.boilerplate.di

import android.content.Context
import android.os.Vibrator
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(val activity: AppCompatActivity) {

    @Provides
    fun provideActivity(): AppCompatActivity = activity

    @Provides
    fun provideLayoutInflater(): LayoutInflater = activity.layoutInflater

    @Provides
    fun privideVibrator(): Vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
}