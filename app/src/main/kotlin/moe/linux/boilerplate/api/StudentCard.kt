package moe.linux.boilerplate.api

import moe.linux.boilerplate.BuildConfig
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.util.*

@PaperParcel
data class StudentCard(
    var number: String = "",
    var name: String = "",
    var mail: String = "",
    var raw: List<String> = emptyList()
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelStudentCard.CREATOR
    }

    fun mailList(): Array<String> {
        return ArrayList<String>()
            .apply {
                add(number.toLowerCase() + BuildConfig.MAIL_PREFIX)
                if (!mail.isNullOrEmpty()) add(mail)
            }
            .toArray(arrayOf())
    }
}
