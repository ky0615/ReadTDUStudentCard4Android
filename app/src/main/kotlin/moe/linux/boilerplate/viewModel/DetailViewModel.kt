package moe.linux.boilerplate.viewModel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.v4.util.PatternsCompat
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import moe.linux.boilerplate.api.StudentCard
import moe.linux.boilerplate.util.view.Navigator
import timber.log.Timber
import javax.inject.Inject

class DetailViewModel @Inject constructor(db: DatabaseReference, val navigator: Navigator) : BaseObservable() {

    val userDb = db.child("user")!!

    lateinit var firebaseCompletionListener: DatabaseReference.CompletionListener

    @Bindable
    var studentCard: StudentCard = StudentCard()

    fun start() {

    }

    fun save(v: View) {
        studentCard.mail.takeIf(String::isNotEmpty)?.also {
            if (!PatternsCompat.EMAIL_ADDRESS.matcher(it).matches()) {
                Toast.makeText(navigator.activity, "メールアドレスの書式が合いません", Toast.LENGTH_LONG).show()
                return
            }
        }
        Timber.d("save!!")
        Timber.d(studentCard.toString())
        userDb.child(studentCard.number).setValue(studentCard, firebaseCompletionListener)
    }
}
