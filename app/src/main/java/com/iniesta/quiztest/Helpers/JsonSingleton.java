package com.iniesta.quiztest.Helpers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class JsonSingleton {

    private static JsonSingleton mInstance;
    private RequestQueue requestQueue;
    private static Context context;

    private JsonSingleton (Context context){
        this.context = context;
        requestQueue = getRequestQueue();

    }

    public static synchronized JsonSingleton getInstance(Context context){
        if (mInstance == null) {
            mInstance = new JsonSingleton(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public<T> void  addToQueue (Request<T> request){
        getRequestQueue().add(request);
    }
}