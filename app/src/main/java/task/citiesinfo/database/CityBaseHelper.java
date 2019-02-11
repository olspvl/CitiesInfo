package task.citiesinfo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.List;
import java.util.Set;

import static task.citiesinfo.database.CitiesDbSchema.*;

public class CityBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "citiesBase.db";
    private static final int VERSION = 1;

    public CityBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("CityDbCtrl", "Entered onCreate");
        db.execSQL("create table " + CityTable.NAME + "(" +
                CityTable.Cols.COUNTRY + ", " +
                CityTable.Cols.CITY + ", " +
                "primary key (" + CityTable.Cols.COUNTRY + ", "
                + CityTable.Cols.CITY + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(List<CountryCity> countryCitySet) {
        SQLiteDatabase db = this.getWritableDatabase();
        String insertion = "INSERT OR REPLACE INTO " + CityTable.NAME + " ( " + CityTable.Cols.COUNTRY + ", " +
                CityTable.Cols.CITY + ") VALUES ( ?, ? )";
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(insertion);
        for(CountryCity entry: countryCitySet) {
            stmt.bindString(1, entry.getCountry());
            stmt.bindString(2, entry.getCity());
            stmt.execute();
            stmt.clearBindings();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
