package agency.digitera.android.promdate.data.db

import agency.digitera.android.promdate.data.FeedResponse
import agency.digitera.android.promdate.data.entities.Couple
import agency.digitera.android.promdate.util.ApiAccessor
import agency.digitera.android.promdate.util.PagingRequestHelper
import android.util.Log
import androidx.paging.PagedList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class CoupleBoundaryCallback(private val db: CoupleDb, private val token: String) :
    PagedList.BoundaryCallback<Couple>() {

    private val api = ApiAccessor()
    private val executor = Executors.newSingleThreadExecutor()
    private val helper = PagingRequestHelper(executor)

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()

        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) { helperCallback ->
            api.apiService.getFeed(token, 3 * PAGE_SIZE) //TODO: Get token
                .enqueue(object : Callback<FeedResponse> {

                    override fun onFailure(call: Call<FeedResponse>?, t: Throwable) {
                        Log.e("CoupleBoundaryCallback", "Failed to load data!")
                        helperCallback.recordFailure(t)
                    }

                    override fun onResponse(
                        call: Call<FeedResponse>?,
                        response: Response<FeedResponse>
                    ) {
                        val responseData = response.body()?.result?.couples
                        if (responseData != null) {
                            val couples: MutableList<Couple> = mutableListOf()
                            for (couple in responseData) {
                                val newCouple = Couple()
                                newCouple.user1 = couple[0]
                                newCouple.user2 = couple[1]
                                couples.add(newCouple)
                                maxLoaded += 1
                            }
                            executor.execute {
                                db.coupleDao().insert(couples)
                                helperCallback.recordSuccess()
                            }
                        } else {
                            Log.e("CoupleBoundaryCallback", "Cannot parse initial response from server.")
                        }
                    }
                })
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Couple) {
        super.onItemAtEndLoaded(itemAtEnd)

        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) { helperCallback ->
            api.apiService.getFeed(
                token,
                PAGE_SIZE, couplesOffset = maxLoaded
            ) //TODO: Fix
                .enqueue(object : Callback<FeedResponse> {

                    override fun onFailure(call: Call<FeedResponse>?, t: Throwable) {
                        Log.e("CoupleBoundaryCallback", "Failed to load data!")
                        helperCallback.recordFailure(t)
                    }

                    override fun onResponse(
                        call: Call<FeedResponse>?,
                        response: Response<FeedResponse>
                    ) {

                        val responseData = response.body()?.result?.couples
                        if (responseData != null) {
                            val couples: MutableList<Couple> = mutableListOf()
                            for (couple in responseData) {
                                val newCouple = Couple()
                                newCouple.user1 = couple[0]
                                newCouple.user2 = couple[1]
                                couples.add(newCouple)
                                maxLoaded += 1
                            }
                            executor.execute {
                                db.coupleDao().insert(couples)
                                helperCallback.recordSuccess()
                            }
                        } else {
                            Log.e("CoupleBoundaryCallback", "Cannot parse response from server.")
                        }
                    }
                })
        }
    }

    companion object {
        const val PAGE_SIZE = 10
        var maxLoaded = 0
    }
}