package com.elhazent.picodiploma.myasynctaskloader;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.AsyncTaskLoader;
import android.util.Log;

    import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MyAsyncTaskLoader extends AsyncTaskLoader<ArrayList<WeatherItems>> {
    private ArrayList<WeatherItems> mData;
    private boolean mHasResult = false;
    private String cities;

    MyAsyncTaskLoader(final Context context, String city) {
        super(context);
        onContentChanged();
        this.cities = city;
    }

    @Override
    protected void onStartLoading(){
        if (takeContentChanged()){
            forceLoad();
        }else if (mHasResult){
            deliverResult(mData);
        }
    }
    @Override
    public void deliverResult(final ArrayList<WeatherItems> data){
        mData = data;
        mHasResult = true;
        super.deliverResult(data);
    }
    @Override
    protected void onReset(){
        super.onReset();
        onStopLoading();
        if (mHasResult){
            mData = null;
            mHasResult = false;
        }
    }
    private static final String API_KEY = "c0c00bfb30f28736dfdedc2eea78bdef";

    @Nullable
    @Override
    public ArrayList<WeatherItems> loadInBackground() {
        SyncHttpClient client = new SyncHttpClient();
        final ArrayList<WeatherItems>weatherItemses = new ArrayList<>();
                String url = "http://api.openweathermap.org/data/2.5/group?id=" + cities + "&units=metric&appid=" + API_KEY;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onStart(){
                super.onStart();
                Log.v("Response", "Masuk");
                setUseSynchronousMode(true);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject responseObject = new JSONObject(result);
                    JSONArray list = responseObject.getJSONArray("List");
                    for (int i = 0; i < list.length(); i++){
                        JSONObject weather = list.getJSONObject(i);
                        WeatherItems weatherItems = new WeatherItems(weather);
                        weatherItemses.add(weatherItems);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Log.e("Response", "failure");
            }
        });
        return weatherItemses;
    }
}