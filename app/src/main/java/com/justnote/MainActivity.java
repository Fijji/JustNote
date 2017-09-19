package com.justnote;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.justnote.R.id.tvText;

/**
 * MainActivity class
 * @author Kyryl Potapenko
 * @since 2017-01-03
 */
public class MainActivity extends AppCompatActivity {

    private static final int CM_DELETE_ID = 1;
    DBHelper dbHelper;

    final String ATTRIBUTE_NAME_TEXT = "text";
    final String ATTRIBUTE_NAME_ALLTEXT = "alltext";

    ListView lvSimple;
    SimpleAdapter sAdapter;
    ArrayList<Map<String, String>> data;
    private static Map<String, String> m = new HashMap<String, String>();
    SimpleDateFormat curDate = new SimpleDateFormat("dd.MM.yyyy '-' HH:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new DBHelper(this);
        data = new ArrayList<Map<String, String>>();
        String[] from = {ATTRIBUTE_NAME_TEXT};
        int[] to = {tvText};
        sAdapter = new SimpleAdapter(this, data, R.layout.item, from, to);
        sAdapter.notifyDataSetChanged();
        lvSimple = (ListView) findViewById(R.id.lvSimple);
        lvSimple.setAdapter(sAdapter);
        registerForContextMenu(lvSimple);
        loadData(savedInstanceState);

        lvSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TextView textView = (TextView) view.findViewById(tvText);
                String noteName = textView.getText().toString();
                Intent intent = new Intent(MainActivity.this, Lists.class);
                intent.putExtra("name", noteName);
                intent.putExtra("position", position);
                intent.putExtra("alltext", data.get(position).get(ATTRIBUTE_NAME_ALLTEXT));
                startActivityForResult(intent, 1);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m = new HashMap<String, String>();
                m.put(ATTRIBUTE_NAME_TEXT, curDate.format(new Date()));
                m.put(ATTRIBUTE_NAME_ALLTEXT, "Note>");
                data.add(m);
                sAdapter.notifyDataSetChanged();
                Snackbar.make(view, "Note added", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onStop() {
        saveData();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_save:
//                saveFile(FILENAME);
//                return true;
            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setClass(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return true;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Delete note");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            data.remove(acmi.position);
            sAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onPause() {
        saveData();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resintent) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                m = new HashMap<String, String>();
                m.put(ATTRIBUTE_NAME_TEXT, resintent.getStringExtra("name"));
                m.put(ATTRIBUTE_NAME_ALLTEXT, resintent.getStringExtra("alltext"));
                data.set(resintent.getExtras().getInt("id"), m);
                sAdapter.notifyDataSetChanged();
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    void saveData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        try {
            db.beginTransaction();
            int clearCount = db.delete("mytable", null, null);
            long rowID;

            for (int i = 0; i < data.size(); i++) {
                String alltext = data.get(i).get(ATTRIBUTE_NAME_ALLTEXT);
                String name = data.get(i).get(ATTRIBUTE_NAME_TEXT);
                cv.put("name", name);
                cv.put("alltext", alltext);
                rowID = db.insert("mytable", null, cv);
                cv.clear();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.execSQL("VACUUM");
        }
    }

    void loadData(Bundle savedInstanceState) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("mytable", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int alltextColIndex = c.getColumnIndex("alltext");
            do {
                m = new HashMap<String, String>();
                m.put(ATTRIBUTE_NAME_TEXT, c.getString(nameColIndex));
                m.put(ATTRIBUTE_NAME_ALLTEXT, c.getString(alltextColIndex));
                data.add(m);
            } while (c.moveToNext());
        } else {
            m.put(ATTRIBUTE_NAME_TEXT, ">      Welcome! ");
            m.put(ATTRIBUTE_NAME_ALLTEXT, "A long time ago in a galaxy far, far away...");
            data.add(m);
        }
        c.close();
        dbHelper.close();
    }
}