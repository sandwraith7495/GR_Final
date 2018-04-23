package com.example.sandwraith8.gr_final.repository.local;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.sandwraith8.gr_final.repository.local.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandwraith8 on 14/04/2018.
 */

public class HumanNameRepository {

    public List<String> getNames(Context context) {
//        DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
//        List<String> names = new ArrayList<>();
//        try {
//            DocumentBuilder builder = fac.newDocumentBuilder();
//            @SuppressLint("ResourceType") InputStream in = activity.getResources().openRawResource(R.xml.first);
//            Document doc = builder.parse(in);
//            Element root = doc.getDocumentElement();
//            Node database = root.getChildNodes().item(0);
//            NodeList tables = database.getChildNodes();
//            int length = tables.getLength();
//            for (int i = 0; i < length; i++) {
//                Element column = (Element) tables.item(i).getFirstChild();
//                names.add(column.getAttribute("firstname"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return names;
        Database database = new Database(context);
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db
                .rawQuery("SELECT * FROM first", null);
        List<String> names = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return names;
    }
}
