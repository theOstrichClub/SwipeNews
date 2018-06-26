package com.bizmedia.shokitakeda.placeholder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        OkHttpClient client;
        ObjectMapper objectMapper = new ObjectMapper();

        final String URL =
                "https://api.cognitive.microsoft.com/bing/v7.0/news/search?count=100&q=eスポーツ&mkt=ja-JP&w=1000&h=1000";
        final String KEY = "";

        client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL)
                .header("Ocp-Apim-Subscription-Key", KEY)
                .addHeader("Content-Type", "application/octet-stream")
                .build();

        final ArrayList<String> jsonArray = new ArrayList<String>();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 取得が成功したかどうか
                if(response.isSuccessful()) {
                    String string = response.body().string();
                    try {
                        JSONObject json = new JSONObject(string);

                        JSONArray prefecturesObject =
                                json.getJSONArray("value");
                        ObjectMapper mapper = new ObjectMapper();
                        for(int i=0; i<prefecturesObject.length(); ++i) {
                            JSONObject local = prefecturesObject.getJSONObject(i);
                            Info info = new Info();
                            if(local.has("image")) {
                                Log.d("infoPrint", String.valueOf(local));
                                info.url = local.getJSONObject("image").getJSONObject("thumbnail").getString("contentUrl");
                                info.name = local.getString("name");
                                info.age = local.getString("description");
                                info.location = local.getString("datePublished");
                                String item = mapper.writeValueAsString(info);
                                jsonArray.add(item);

                            }
                        }

                        // UI反映
                        runOnUiThread(new Runnable() {
                            public void run() {
                                mSwipeView = (SwipePlaceHolderView)findViewById(R.id.swipeView);
                                mContext = getApplicationContext();
                                mSwipeView.getBuilder()
                                        .setDisplayViewCount(3)
                                        .setSwipeDecor(new SwipeDecor()
                                                .setPaddingTop(20)
                                                .setRelativeScale(0.01f)
                                                .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                                                .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));
                                for(Profile profile : Utils.loadProfiles(jsonArray, getApplicationContext())){
                                    mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
                                }
                            }
                        });
                    }catch (JSONException jsonEx) {
                        jsonEx.printStackTrace();
                    }
                } else {
                    throw new IOException();
                }
            }
        });
    }
}