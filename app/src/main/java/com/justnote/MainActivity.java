package com.justnote;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {

    static final String LOG_TAG = "myLogs";
    private static final int CM_DELETE_ID = 1;
    DBHelper dbHelper;

    // имена атрибутов для Map
    final String ATTRIBUTE_NAME_TEXT = "text";
    final String ATTRIBUTE_NAME_ALLTEXT = "alltext";

    ListView lvSimple;
    SimpleAdapter sAdapter;
    ArrayList<Map<String, String>> data;
    private static Map<String, String> m = new HashMap<String, String>();
    SimpleDateFormat curDate = new SimpleDateFormat("dd.MM.yyyy '-' HH:mm:ss");
    String s = "check";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(LOG_TAG, "on Create main method ");
// создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);
// упаковываем данные в понятную для адаптера структуру
        data = new ArrayList<Map<String, String>>();
        if(savedInstanceState != null && !savedInstanceState.isEmpty()){
            loadData(savedInstanceState); }
 // массив имен атрибутов, из которых будут читаться данные
        String[] from = {ATTRIBUTE_NAME_TEXT};
// массив ID View-компонентов, в которые будут вставлять данные
        int[] to = {tvText};
// создаем адаптер
        sAdapter = new SimpleAdapter(this, data, R.layout.item, from, to);
        sAdapter.notifyDataSetChanged();

// определяем список и присваиваем ему адаптер
        lvSimple = (ListView) findViewById(R.id.lvSimple);
        lvSimple.setAdapter(sAdapter);
        registerForContextMenu(lvSimple);


//обрабатывает нажатие на пункт списка -урок 44
        lvSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TextView textView = (TextView) view.findViewById(tvText);
                String noteName = textView.getText().toString();
                Log.d(LOG_TAG, "itemClick: position = " + position + ", id = "
                        + id + ", name -" + noteName);
                Intent intent = new Intent(MainActivity.this, Lists.class);
                intent.putExtra("name", noteName);
                intent.putExtra("position", position);
                intent.putExtra("alltext", data.get(position).get(ATTRIBUTE_NAME_ALLTEXT));
                startActivityForResult(intent, 1);
            }
        });


//        lvSimple.setOnScrollListener(new AbsListView.OnScrollListener() {
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                Log.d(LOG_TAG, "scrollState = " + scrollState);
//            }
//
//
//            public void onScroll(AbsListView view, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//                Log.d(LOG_TAG, "scroll: firstVisibleItem = " + firstVisibleItem
//                        + ", visibleItemCount" + visibleItemCount
//                        + ", totalItemCount" + totalItemCount);
//            }
//        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m = new HashMap<String, String>();
                m.put(ATTRIBUTE_NAME_TEXT, curDate.format(new Date()));
                m.put(ATTRIBUTE_NAME_ALLTEXT, s);
                // добавляем его в коллекцию
                data.add(m);

                // уведомляем, что данные изменились
                sAdapter.notifyDataSetChanged();
                Snackbar.make(view, "Note added", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        Log.d(LOG_TAG, "on RestoreInstanceStatey MainActivity");
//        super.onRestoreInstanceState(savedInstanceState);
//        // Восстановите состояние UI из переменной savedInstanceState.
//        // Этот объект типа Bundle также был передан в метод onCreate.
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        Log.d(LOG_TAG, "on SaveInstanceStatey MainActivity");
//        super.onSaveInstanceState(outState);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //menu button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
//            case R.id.action_open:
//                openFile(FILENAME);
//                return true;
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
            // получаем инфу о пункте списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
// удаляем Map из коллекции, используя позицию пункта в списке
            data.remove(acmi.position);
// уведомляем, что данные изменились
            sAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "on Destroy Main activity ");
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "on Resume Main activity ");
        super.onResume();
    }

    @Override
    protected void onPause() {
        saveData();
        Log.d(LOG_TAG, "on Pause Main activity ");
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
                Log.d(LOG_TAG, "RESULT_OK = ");

            } else if (resultCode == RESULT_CANCELED) {
                Log.d(LOG_TAG, "RESULT_CANCELED ");

            }
        }
    }
    void saveData(){
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // создаем объект для данных
        ContentValues cv = new ContentValues();
        try {
        db.beginTransaction();
        // удаляем все записи
        int clearCount = db.delete("mytable", null, null);
        Log.d(LOG_TAG, "deleted rows count = " + clearCount);

            long rowID;

        for( int i = 0; i < data.size(); i++) {

            String alltext = data.get(i).get(ATTRIBUTE_NAME_ALLTEXT);
            String name = data.get(i).get(ATTRIBUTE_NAME_TEXT);

            Log.d(LOG_TAG, "--- Insert in mytable: ---");
            // подготовим данные для вставки в виде пар: наименование столбца - значение
            cv.put("name", name);
            cv.put("alltext", alltext);
            // вставляем запись и получаем ее ID
            rowID = db.insert("mytable", null, cv);
            cv.clear();
            Log.d(LOG_TAG, "row inserted, ID = " + rowID);

        }
        db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.execSQL("VACUUM");
        }

    }

    void loadData(Bundle savedInstanceState) {
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("mytable", null, null, null, null, null, null);
        Log.d(LOG_TAG, "row inserted, cursor = " + c.getCount());
        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {
            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int alltextColIndex = c.getColumnIndex("alltext");
            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG_TAG,
                        "ID = " + c.getInt(idColIndex) +
                                ", name = " + c.getString(nameColIndex) +
                                ", alltext = " + c.getString(alltextColIndex));
                m = new HashMap<String, String>();
                m.put(ATTRIBUTE_NAME_TEXT, c.getString(nameColIndex));
                m.put(ATTRIBUTE_NAME_ALLTEXT, c.getString(alltextColIndex));
                data.add(m);
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        } else {
            Log.d(LOG_TAG, "0 rows");
            m.put(ATTRIBUTE_NAME_TEXT, ">      Welcome! ");
            m.put(ATTRIBUTE_NAME_ALLTEXT, s);
            data.add(m);
        }
        c.close();
        // закрываем подключение к БД
        dbHelper.close();
            SharedPreferences userDetails = getSharedPreferences("instanceSaved", MODE_PRIVATE);
            if (userDetails!=null) {
                m = new HashMap<String, String>();
                m.put(ATTRIBUTE_NAME_TEXT, userDetails.getString("name", ""));
                m.put(ATTRIBUTE_NAME_ALLTEXT, userDetails.getString("alltext", ""));
                data.set(userDetails.getInt("position", 1), m);
            }
    }
}
