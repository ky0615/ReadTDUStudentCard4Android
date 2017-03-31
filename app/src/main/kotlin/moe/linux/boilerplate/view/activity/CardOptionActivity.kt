package moe.linux.boilerplate.view.activity

import android.os.Bundle
import moe.linux.boilerplate.api.StudentCard

class CardOptionActivity : BaseActivity() {

    val studentCard by lazy { intent.getParcelableExtra<StudentCard>("card") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.injectTo(this)
    }
}
