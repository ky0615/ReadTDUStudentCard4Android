package moe.linux.boilerplate.view.activity

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.media.AudioManager
import android.media.SoundPool
import android.nfc.NfcAdapter
import android.nfc.tech.NfcF
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.widget.Toast
import io.reactivex.Observable
import moe.linux.boilerplate.R
import moe.linux.boilerplate.api.NfcReadUtil
import moe.linux.boilerplate.api.github.GithubApiClient
import moe.linux.boilerplate.api.qiita.QiitaApiClient
import moe.linux.boilerplate.databinding.ActivityMainBinding
import moe.linux.boilerplate.view.fragment.BaseFragment
import moe.linux.boilerplate.view.fragment.DetailFragment
import moe.linux.boilerplate.view.fragment.FrontFragment
import moe.linux.boilerplate.view.fragment.QiitaListFragment
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding

    val nfcReadUtil = NfcReadUtil()

    @Inject
    lateinit var client: GithubApiClient

    @Inject
    lateinit var qiitaClient: QiitaApiClient

    @Inject
    lateinit var vibrator: Vibrator

    lateinit var onStateChange: Observable<Page>

    val frontFragment: FrontFragment by lazyFragment(FrontFragment.TAG, { FrontFragment.newInstance() })

    val qiitaListFragment: QiitaListFragment by lazyFragment(QiitaListFragment.TAG, { QiitaListFragment.newInstance() })

    val detailFragment: DetailFragment by lazyFragment(DetailFragment.TAG, { DetailFragment.newInstance() })

    val handler = Handler()

    val nfcAdapter: NfcAdapter by lazy {
        NfcAdapter.getDefaultAdapter(applicationContext)
            .apply {
                enableReaderMode(this@MainActivity, { tag ->
                    handler.post {
                        Timber.d("recieve the card: ${tag.id}")
                        try {
                            val readTag = nfcReadUtil.readTag(tag)
                            Timber.d(readTag.toString())
                            Toast.makeText(this@MainActivity, "name : ${readTag.name}\nnumber: ${readTag.number}", Toast.LENGTH_LONG).show()
                            soundPool.play(gateSuccess, 1F, 1F, 0, 0, 1F)
                            vibrator.vibrate(250)
                            switchFragment(detailFragment, DetailFragment.TAG)
                            detailFragment.viewModel.studentCard = readTag
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@MainActivity, "読み取りに失敗しました。\n${e.message}", Toast.LENGTH_SHORT).show()
                            soundPool.play(gateFailed, 1F, 1F, 0, 0, 1F)
                        }
                    }
                },
                    NfcAdapter.FLAG_READER_NFC_F
                        or NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS
                    , null)
            }
    }

    val pendingIntent by lazy { PendingIntent.getActivity(this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0) }

    val intentFilter by lazy {
        arrayOf(
            IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
                .apply { addDataType("text/plain") }
        )
    }

    val soundPool by lazy { SoundPool(1, AudioManager.STREAM_MUSIC, 0) }
    var gateSuccess = -1
    var gateFailed = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.injectTo(this)

        gateSuccess = soundPool.load(this, R.raw.gate_success, 1)
        gateFailed = soundPool.load(this, R.raw.gate_failed, 1)

        initView()
        initFragment(savedInstanceState)

        onStateChange.subscribe({
            when (it) {
                MainActivity.Page.FRONT -> frontFragment
                MainActivity.Page.Detail -> detailFragment
                MainActivity.Page.Qiita -> qiitaListFragment
            }.apply {
                switchFragment(this, this.TAG)
            }
        })

    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(this, binding.drawer, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()

        onStateChange = Observable.merge<Page>(listOf(
            Observable.create<Page> { subscribe ->
                binding.navView.setNavigationItemSelectedListener {
                    subscribe.onNext(Page.parseWithId(it.itemId))
                    binding.drawer.closeDrawer(GravityCompat.START)
                    true
                }
            },
            Observable.create<Page> { subscribe ->
                binding.bottomMenu.setOnNavigationItemSelectedListener {
                    subscribe.onNext(Page.parseWithId(it.itemId))
                    true
                }
            }))
            .publish()
            .refCount()
    }

    private fun initFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null)
            switchFragment(frontFragment, FrontFragment.TAG)
    }

    override fun onResume() {
        super.onResume()
        Timber.d("start nfc scanning")
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, arrayOf(arrayOf(NfcF::class.java.name)))
    }

    override fun onPause() {
        super.onPause()
        Timber.d("stop nfc scanning")
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }

    override fun onBackPressed() {
        // TODO fragment backstackも考慮するように変更
        Timber.d("backstack count: ${supportFragmentManager.backStackEntryCount}")
        if (binding.drawer.isDrawerOpen(GravityCompat.START))
            binding.drawer.closeDrawer(GravityCompat.START)
        else if (switchFragment(frontFragment, FrontFragment.TAG))
            Timber.d("back to front page")
        else
            super.onBackPressed()

        updateView()
    }

    fun switchFragment(fragment: Fragment, tag: String): Boolean {
        if (fragment.isAdded) return false

        val manager = supportFragmentManager

        manager.beginTransaction().also { ft ->
            val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.contentFrame)
            if (currentFragment != null)
                ft.detach(currentFragment)
            if (fragment.isDetached)
                ft.attach(fragment)
            else
                ft.add(R.id.contentFrame, fragment, tag)
        }
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()

        manager.executePendingTransactions()

        updateView()

        return true
    }

    private fun updateView() {
        val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.contentFrame)
        if (currentFragment is BaseFragment) {
            currentFragment.getPage().also {
                binding.toolbar.title = getString(it.title)
                binding.navView.menu?.findItem(it.id)
                    .apply {
                        if (this == null)
                            binding.navView.menu?.setGroupCheckable(R.id.mainMenu, false, false)
                    }?.isChecked = true
                binding.bottomMenu.menu.findItem(it.id)
                    .apply {
                        if (this == null)
                            binding.bottomMenu.menu.setGroupCheckable(R.id.mainMenu, false, false)
                    }?.isChecked = true
            }
        }
    }

    enum class Page(@IdRes val id: Int, @StringRes val title: Int) {
        FRONT(R.id.navHome, R.string.app_name), // other page
        Detail(R.id.navDetail, R.string.menu_detail),
        Qiita(R.id.navQiita, R.string.menu_qiita),
        ;

        companion object {
            fun parseWithId(id: Int): Page {
                return values().find { it.id == id } ?: FRONT
            }
        }
    }

    private fun <T : Fragment> lazyFragment(tag: String, non: () -> T): Lazy<T> =
        lazy { supportFragmentManager.findFragmentByTag(tag) as? T ?: non() }
}
