@file:Suppress("KDocUnresolvedReference")

package com.example.mvr.core.net

import com.example.mvr.test.bean.Token
import com.example.mvr.test.body.TestBody
import retrofit2.http.*

/**
 * Created by light
 *
 * on 2021/09/06
 *
 * desc:
 *
 */
interface PurpleAPI {

    @POST("auth/test")
    suspend fun test(@Body testBody: TestBody): Token

}