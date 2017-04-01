package moe.linux.boilerplate.view.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import moe.linux.boilerplate.R
import moe.linux.boilerplate.api.StudentCard
import moe.linux.boilerplate.databinding.FragmentCardMenuDialogBinding
import moe.linux.boilerplate.di.FragmentComponent
import moe.linux.boilerplate.di.FragmentModule
import moe.linux.boilerplate.view.activity.BaseActivity
import moe.linux.boilerplate.viewModel.CardMenuViewModel
import javax.inject.Inject

class CardMenuDialogFragment : DialogFragment() {
    companion object {
        val CARD = "card"

        fun getInstanse(studentCard: StudentCard) =
            CardMenuDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(CARD, studentCard)
                }
            }
    }

    val fragmentComponent: FragmentComponent by lazy {
        getBaseActivity().activityComponent.plus(FragmentModule(this))
    }

    fun getBaseActivity(): BaseActivity = activity as BaseActivity

    val studentCard: StudentCard by lazy { arguments.getParcelable<StudentCard>(CARD) }

    val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }


    @Inject
    lateinit var viewModel: CardMenuViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        fragmentComponent.injectTo(this)
        viewModel.studentCard = studentCard
        return Dialog(activity, R.style.AppTheme_FullScreenDialog).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            val binding = FragmentCardMenuDialogBinding.inflate(inflater, null, false)
            binding.viewModel = viewModel
            setContentView(binding.root)
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}
