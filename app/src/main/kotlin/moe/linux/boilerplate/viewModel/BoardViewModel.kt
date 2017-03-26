package moe.linux.boilerplate.viewModel

import android.databinding.BaseObservable
import android.databinding.ObservableArrayList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import moe.linux.boilerplate.api.StudentCard
import moe.linux.boilerplate.databinding.FragmentFrontBinding
import timber.log.Timber
import javax.inject.Inject

class BoardViewModel @Inject constructor(db: DatabaseReference) : BaseObservable() {
    val userList = ObservableArrayList<StudentCard>()

    val userDb = db.child("user")!!

    lateinit var binding: FragmentFrontBinding

    fun start() {
        userDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                p0?.children?.forEach {
                    Timber.d("list: ${it.value}")
                }
                Timber.d("add message : ${p0?.value.toString()}")
            }

            override fun onCancelled(p0: DatabaseError?) {
                Timber.e("error: ${p0?.message}")
            }
        })
    }
}
