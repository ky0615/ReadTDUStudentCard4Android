package moe.linux.boilerplate.viewModel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.ObservableArrayList
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import moe.linux.boilerplate.R
import moe.linux.boilerplate.api.StudentCard
import moe.linux.boilerplate.databinding.FragmentFrontBinding
import moe.linux.boilerplate.databinding.ViewStudentCardBinding
import moe.linux.boilerplate.util.view.DataBindingViewHolder
import moe.linux.boilerplate.util.view.Navigator
import moe.linux.boilerplate.util.view.ObservableListRecyclerAdapter
import moe.linux.boilerplate.util.view.ViewModel
import moe.linux.boilerplate.view.fragment.CardMenuDialogFragment
import timber.log.Timber
import javax.inject.Inject

class BoardViewModel @Inject constructor(db: DatabaseReference, val navigator: Navigator) : BaseObservable(), ViewModel {

    val userList = ObservableArrayList<StudentCardViewModel>()

    val userDb = db.child("user")!!

    lateinit var binding: FragmentFrontBinding

    val userDbEventListener = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot?) {
            userList.clear()
            p0?.children?.forEach {
                userList.add(it.getValue(StudentCard::class.java).run {
                    Timber.d("set: $name")
                    convertToViewModel()
                })
            }
        }

        override fun onCancelled(p0: DatabaseError?) {
            Timber.e("error: ${p0?.message}")
        }
    }

    fun start() {
        userList.clear()
        userDb.addValueEventListener(userDbEventListener)
    }

    fun StudentCard.convertToViewModel() = StudentCardViewModel(navigator, this)

    override fun destroy() {
        userDb.removeEventListener(userDbEventListener)
        userList.clear()
    }
}

class StudentCardViewModel(val navigator: Navigator, val studentCard: StudentCard) : BaseObservable(), ViewModel {
    override fun destroy() {
    }

    fun onClick(view: View) {
        Timber.d("click")
        CardMenuDialogFragment.getInstanse(studentCard).show(navigator.activity.supportFragmentManager, "")
//        Timber.d("click with: ${stock.title}")
//        navigator.navigateToWebPage(stock.url)
    }
}

class StudentCardListAdapter(context: Context, list: ObservableArrayList<StudentCardViewModel>)
    : ObservableListRecyclerAdapter<StudentCardViewModel, DataBindingViewHolder<ViewStudentCardBinding>>(context, list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder<ViewStudentCardBinding>
        = DataBindingViewHolder(context, R.layout.view_student_card, parent)

    override fun onBindViewHolder(holder: DataBindingViewHolder<ViewStudentCardBinding>, position: Int) {
        holder.binding.viewModel = getItem(position)
        holder.binding.executePendingBindings()
    }
}