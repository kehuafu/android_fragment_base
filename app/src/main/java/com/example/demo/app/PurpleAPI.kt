@file:Suppress("KDocUnresolvedReference")

package com.example.demo.app

import com.example.demo.test.bean.Token
import com.example.demo.test.body.TestBody
import retrofit2.http.Body
import retrofit2.http.POST


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