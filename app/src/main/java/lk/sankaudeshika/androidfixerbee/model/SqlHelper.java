package lk.sankaudeshika.androidfixerbee.model;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqlHelper extends SQLiteOpenHelper {

    public static SqlHelper sqlHelperObject;
    public SqlHelper(@Nullable Context context, @Nullable String name, @Nullable android.database.sqlite.SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE actions (\n" +
                "    action_id   INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    action_name TEXT    NOT NULL,\n" +
                "    action_date TEXT    NOT NULL,\n" +
                "    action_time TEXT    NOT NULL\n" +
                ");");
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
