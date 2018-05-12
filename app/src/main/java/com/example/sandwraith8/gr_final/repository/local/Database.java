package com.example.sandwraith8.gr_final.repository.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sandwraith8.gr_final.R;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by sandwraith8 on 14/04/2018.
 */

public class Database extends SQLiteOpenHelper {

    private static final String DB_NAME = "Database";
    private static final int VERSION = 1;
    private Context context;


    public Database(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        String filePath = "android.resource://" + context.getPackageName() + "/raw/" + "names_database_sql.sql";
        InputStream inputStream = context.getResources().openRawResource(R.raw.names_database_sql);
        Scanner scanner = new Scanner(inputStream);
        StringBuilder sql = new StringBuilder();
        while (scanner.hasNextLine()) {
            sql.append(scanner.nextLine());
        }
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS first (\n" +
                "  firstname text NOT NULL\n" +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS contact (\n" +
                "  id text PRIMARY KEY,\n" +
                "  image text,\n" +
                "  name text,\n" +
                "  email text,\n" +
                "  phone text\n" +
                ");");
        sqLiteDatabase.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
