package moe.linux.boilerplate.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import moe.linux.boilerplate.databinding.FragmentDetailBinding
import moe.linux.boilerplate.view.activity.MainActivity
import moe.linux.boilerplate.viewModel.DetailViewModel
import javax.inject.Inject

class DetailFragment : BaseFragment() {
    override val TAG: String = DetailFragment.TAG

    companion object {
        val TAG = DetailFragment::class.simpleName ?: "UndefinedClass"
        fun newInstance(): DetailFragment {
            return DetailFragment()
        }
    }

    @Inject
    lateinit var viewModel: DetailViewModel

    lateinit private var binding: FragmentDetailBinding

    override fun getPage(): MainActivity.Page = MainActivity.Page.Detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent.injectTo(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        viewModel.firebaseCompletionListener = DatabaseReference.CompletionListener { databaseError, databaseReference ->
            if (databaseError != null) {
                Toast.makeText(context, "error: ${databaseError.message}", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "success save!", Toast.LENGTH_SHORT).show()
                getMainActivity().switchFragment(getMainActivity().frontFragment, FrontFragment.TAG)
            }
        }
        return binding.root
    }
}
