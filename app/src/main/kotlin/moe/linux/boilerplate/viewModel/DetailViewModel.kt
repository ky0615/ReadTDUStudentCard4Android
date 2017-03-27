package moe.linux.boilerplate.viewModel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.view.View
import com.google.firebase.database.DatabaseReference
import moe.linux.boilerplate.api.StudentCard
import timber.log.Timber
import javax.inject.Inject

class DetailViewModel @Inject constructor(db: DatabaseReference) : BaseObservable() {

    @Bindable
    var textName = ""

    @Bindable
    var textNumber = ""

    val userDb = db.child("user")!!

    lateinit var firebaseCompletionListener: DatabaseReference.CompletionListener

    var studentCard: StudentCard = StudentCard()
        set(value) {
            this.textName = value.name
            this.textNumber = value.number
        }

    fun start() {

    }

    fun save(v: View) {
        Timber.d("save!!")
        studentCard.apply {
            name = textName
            number = textNumber
        }
        userDb.child(studentCard.number).setValue(studentCard, firebaseCompletionListener)
    }
}
