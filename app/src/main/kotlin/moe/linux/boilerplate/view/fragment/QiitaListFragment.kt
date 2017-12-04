package moe.linux.boilerplate.view.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moe.linux.boilerplate.api.qiita.QiitaApiClient
import moe.linux.boilerplate.databinding.FragmentQiitaListBinding
import moe.linux.boilerplate.view.activity.MainActivity
import moe.linux.boilerplate.viewModel.QiitaListAdapter
import moe.linux.boilerplate.viewModel.QiitaListViewModel
import timber.log.Timber
import javax.inject.Inject

class QiitaListFragment : BaseFragment() {
    override val TAG = QiitaListFragment.TAG

    companion object {
        val TAG = QiitaListFragment::class.simpleName ?: "UndefinedClass"
        fun newInstance(): QiitaListFragment {
            return QiitaListFragment()
        }
    }

    @Inject
    lateinit var qiitaClient: QiitaApiClient

    @Inject
    lateinit var qiitaListViewModel: QiitaListViewModel

    lateinit private var binding: FragmentQiitaListBinding

    override fun getPage(): MainActivity.Page = MainActivity.Page.Qiita

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent.injectTo(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentQiitaListBinding.inflate(inflater, container, false).apply {
            list.layoutManager = LinearLayoutManager(context)
            list.adapter = QiitaListAdapter(context!!, qiitaListViewModel.list)
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        qiitaListViewModel.start(
            {
                Timber.d("start")
                Timber.d("start")
                binding.progressBar.visibility = View.VISIBLE
            },
            {
                Timber.d("finish")
                binding.progressBar.visibility = View.GONE
            },
            {
                it.printStackTrace()
                Snackbar.make(binding.coordinatorLayout, "cause error: ${it.message}", Snackbar.LENGTH_LONG).show()
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        qiitaListViewModel.destroy()
    }
}