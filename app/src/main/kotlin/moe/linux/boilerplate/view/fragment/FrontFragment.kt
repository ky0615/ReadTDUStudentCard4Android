package moe.linux.boilerplate.view.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moe.linux.boilerplate.databinding.FragmentFrontBinding
import moe.linux.boilerplate.view.activity.MainActivity
import moe.linux.boilerplate.viewModel.BoardViewModel
import moe.linux.boilerplate.viewModel.StudentCardListAdapter
import javax.inject.Inject

class FrontFragment : BaseFragment() {
    override val TAG: String = FrontFragment.TAG

    override fun getPage(): MainActivity.Page = MainActivity.Page.FRONT

    @Inject
    lateinit var viewModel: BoardViewModel

    lateinit var binding: FragmentFrontBinding

    companion object {
        val TAG: String = FrontFragment::class.simpleName ?: "UndefinedClass"

        fun newInstance(): FrontFragment {
            return FrontFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent.injectTo(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFrontBinding.inflate(inflater, container, false)
            .apply {
                viewModel = this@FrontFragment.viewModel
                list.layoutManager = LinearLayoutManager(context)
                list.adapter = StudentCardListAdapter(context, viewModel.userList)
            }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.binding = binding
        viewModel.start()
    }
}
