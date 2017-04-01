package moe.linux.boilerplate.viewModel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.v4.app.DialogFragment
import android.support.v4.util.PatternsCompat
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import moe.linux.boilerplate.api.StudentCard
import moe.linux.boilerplate.util.view.FragmentNavigator
import moe.linux.boilerplate.util.view.ViewModel
import timber.log.Timber
import javax.inject.Inject

class CardMenuViewModel @Inject constructor(db: DatabaseReference, val navigator: FragmentNavigator) : BaseObservable(), ViewModel {

    @Bindable
    var studentCard: StudentCard = StudentCard()

    val userDb = db.child("user")!!

    fun start() {

    }

    override fun destroy() {

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

        userDb.child(studentCard.number).setValue(studentCard, { databaseError, databaseReference ->
            Toast.makeText(navigator.activity, "修正しました", Toast.LENGTH_LONG).show()
            dismiss()
        })

    }

    fun remove(v: View) {
        Timber.d("remove!")

        userDb.child(studentCard.number).removeValue({ databaseError, databaseReference ->
            Toast.makeText(navigator.activity, "削除しました", Toast.LENGTH_LONG).show()
            dismiss()
        })
    }

    fun sendMail(v: View) {
        navigator.navigateSendEmailWithBCC(studentCard.mailList())
    }

    private fun dismiss() {
        if (navigator.fragment is DialogFragment)
            navigator.fragment.dismiss()
        else
            throw RuntimeException("require extend the DialogFragment")
    }
}
