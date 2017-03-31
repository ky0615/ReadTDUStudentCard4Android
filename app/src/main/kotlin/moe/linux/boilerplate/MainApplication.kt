package moe.linux.boilerplate

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import dagger.Lazy
import moe.linux.boilerplate.di.AppComponent
import moe.linux.boilerplate.di.AppModule
import moe.linux.boilerplate.di.DaggerAppComponent
import timber.log.Timber
import javax.inject.Inject

class MainApplication : Application() {

    @Inject
    lateinit var debugTree: Lazy<Timber.DebugTree>

    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        component.injectTo(this)

        if (BuildConfig.DEBUG)
            Timber.plant(debugTree.get())
    }
}
