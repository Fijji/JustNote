package com.justnote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import static com.justnote.MainActivity.LOG_TAG;

public class Lists extends AppCompatActivity implements View.OnTouchListener {
    private EditText mEditText;
    private EditText noteName;
    private int pos;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists);

        mEditText = (EditText) findViewById(R.id.editText);
        noteName = (EditText) findViewById(R.id.noteName);

        mEditText.setOnTouchListener(this);
        noteName.setOnTouchListener(this);

        Log.d(LOG_TAG, "on Create Lists activity ");

        Intent intent = getIntent();
        pos = intent.getExtras().getInt("position");
        noteName.setText(intent.getStringExtra("name"));
        mEditText.setText(intent.getStringExtra("alltext"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "on SaveInstanceStatey Lists activity ");
        SharedPreferences userDetails = getSharedPreferences("instanceSaved", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.clear();
        edit.putString("name", noteName.getText().toString());
        edit.putString("alltext", mEditText.getText().toString());
        edit.putInt("position", pos);
        edit.commit();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "on Destroy Lists activity ");

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "on Resume Lists activity ");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Intent i = new Intent();
        i.putExtra("id", pos);
        i.putExtra("name", noteName.getText().toString());
        i.putExtra("alltext", mEditText.getText().toString());
        setResult(RESULT_OK, i);
        Log.d(LOG_TAG, "on Pause Lists activity ");
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "on BackPressed Lists activity ");
        super.onBackPressed();
    }

    @Override
    public void finish() {
        Intent i = new Intent();
        i.putExtra("name", noteName.getText().toString());
        i.putExtra("alltext", mEditText.getText().toString());
        i.putExtra("id", pos);
        setResult(RESULT_OK, i);
        super.finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}