package moe.linux.boilerplate.api

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
data class StudentCard(
    var number: String = "",
    var name: String = "",
    var raw: List<String> = emptyList()
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelStudentCard.CREATOR
    }
}
