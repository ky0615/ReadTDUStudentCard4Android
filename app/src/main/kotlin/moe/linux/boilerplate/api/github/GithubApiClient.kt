package moe.linux.boilerplate.api.github

import io.reactivex.Single
import moe.linux.boilerplate.api.AbstructApiClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubApiClient @Inject constructor(val githubApiService: GithubApiService) : AbstructApiClient() {
    fun showCommitsList(author: String, repo: String): Single<List<CommitsResponse>> {
        return bindThread(githubApiService.showCommitsList(author, repo))
    }
}
