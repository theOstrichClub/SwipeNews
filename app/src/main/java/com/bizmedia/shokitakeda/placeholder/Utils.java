package com.bizmedia.shokitakeda.placeholder;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phlight on 07.06.2018.
 */
public class Utils {

    private static final String TAG = "Utils";

    public static List<Profile> loadProfiles(ArrayList<String> array, Context context){
        try{
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
//            JSONArray array = new JSONArray(loadJSONFromAsset(context, "profiles.json"));
            List<Profile> profileList = new ArrayList<>();
//            for(int i=0;i<array.length();i++){
//                Log.d("jsonPrint", String.valueOf(array.getString(i)));
//                Profile profile = gson.fromJson(array.getString(i), Profile.class);
//                profileList.add(profile);
//            }
            ObjectMapper mapper = new ObjectMapper();
            for(int i=0;i<array.size();i++){
                Profile profile = new Profile();
                try {
                    Info info = mapper.readValue(array.get(i), Info.class);
                    profile.setName(info.name);
                    profile.setImageUrl(info.url);
                    profile.setLocation(info.location);
                    profile.setAge(info.age);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profileList.add(profile);
            }
            return profileList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static String loadJSONFromAsset(Context context, String jsonFileName) {
        String json = null;
        InputStream is=null;
        try {
            AssetManager manager = context.getAssets();
            Log.d(TAG,"path "+jsonFileName);
            is = manager.open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}