package moe.linux.boilerplate.api

import nz.bradcampbell.paperparcel.PaperParcel

@PaperParcel
data class StudentCard(
    var number: String = "",
    var name: String = ""
)