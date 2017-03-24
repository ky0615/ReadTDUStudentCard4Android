package moe.linux.boilerplate.api.github

import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import java.util.*

@PaperParcel
data class CommitsResponse(

    val sha: String,

    val commit: Commit,

    val url: String,

    val author: User,

    val committer: User
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelCommitsResponse.CREATOR
    }
}

@PaperParcel
data class Commit(
    val author: CommitUser,

    val committer: CommitUser,

    val message: String,

    val url: String,

    @SerializedName("comment_count")
    val commentCount: Int
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelCommit.CREATOR
    }
}

@PaperParcel
data class CommitUser(
    val name: String,

    val email: String,

    val date: Date
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelCommitUser.CREATOR
    }
}

@PaperParcel
data class User(
    val login: String,

    val id: Int,

    val avatar_url: String
) : PaperParcelable {
    companion object {
        @JvmField val CREATOR = PaperParcelUser.CREATOR
    }
}