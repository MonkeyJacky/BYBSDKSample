package com.byb.game.flappybird;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class BiwanRequest {
    private static final String BASE_URL = "https://biwan.wanlege.com/";

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    //product price均为测试数据，方便显示
    public static void testOrderReq(final HttpHandler handler, final String appid, final JSONObject token, final String product, final Double price) {
        RequestParams params = new RequestParams();
        params.put("AppID", appid);
        params.put("productName", product);
        params.put("TotalFee", price);
        params.put("AccessToken", token);
        params.setUseJsonStreamer(true);

        Log.d("testOrderReqParams", params.toString());

        post("test/order/indent", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("testOrderReqResponse", response.toString());

                handler.handlePayOrder(response);
            }
        });
    }

    public static void testUserinfoReq(final HttpHandler handler, final String code) {
        RequestParams params = new RequestParams();
        params.put("Code", code);

        Log.d("testUserinfoReqParams", params.toString());

        post("test/app/redirect", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("testUserinfoReqResponse", response.toString());

                handler.handleAuth(response);
            }

        });
    }

}
