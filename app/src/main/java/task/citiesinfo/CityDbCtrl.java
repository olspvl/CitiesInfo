package task.citiesinfo;

import android.content.Context;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import task.citiesinfo.database.CityBaseHelper;
import task.citiesinfo.database.CountryCity;

import static task.citiesinfo.database.CitiesDbSchema.*;

public class CityDbCtrl {
    private static final String TAG = "CityDbCtrl";

    private static CityDbCtrl sCtrl;

    private SQLiteDatabase mDatabase;
    private CityBaseHelper mCityBaseHelper;

    private String currentCountry;

    private CityDbCtrl(Context context) {
        Context c = context.getApplicationContext();
        mCityBaseHelper = new CityBaseHelper(c);
        mDatabase = mCityBaseHelper.getWritableDatabase();
    }

    public static CityDbCtrl get(Context context) {
        if(sCtrl == null) {
            sCtrl = new CityDbCtrl(context);
        }
        return sCtrl;
    }


    public void fillDb(String s) {
        List<CountryCity> countryCityList = new ArrayList<>();
        JSONObject jsonObject;
        JSONArray cities;
        Iterator<String> countries;
        try {
            jsonObject = new JSONObject(s);
            countries = jsonObject.keys();
            String country;
            while(countries.hasNext()) {
                country = countries.next();
                cities = jsonObject.getJSONArray(country);
                for(int i = 0; i < cities.length(); i++) {
                    countryCityList.add(new CountryCity(country, (String) cities.get(i)));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Invalid string. Is not a json type." + e);
            e.printStackTrace();
        }
        mCityBaseHelper.insert(countryCityList);
    }

    public List<String> getCountries() {
        List<String> countries = new ArrayList<>();
        try (CursorWrapper cursorWrapper = new CursorWrapper(mDatabase.query(true, CityTable.NAME,
                new String[]{CityTable.Cols.COUNTRY},
                null, null, null, null, null, null))) {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                countries.add(cursorWrapper.getString(0));
                cursorWrapper.moveToNext();
            }
        }
        return countries;
    }

    public List<String> getCities(String country) {
        currentCountry = country;
        List<String> cities = new ArrayList<>();
        try (CursorWrapper cursorWrapper = new CursorWrapper(mDatabase.query(true, CityTable.NAME,
                new String[]{CityTable.Cols.CITY}, CityTable.Cols.COUNTRY + " = ?",
                new String[] {currentCountry}, null, null, null, null))) {
            cursorWrapper.moveToFirst();
            while(!cursorWrapper.isAfterLast()) {
                cities.add(cursorWrapper.getString(0));
                cursorWrapper.moveToNext();
            }
        }
        return cities;
    }

    public String getCurrentCountry() {
        return currentCountry;
    }
}
