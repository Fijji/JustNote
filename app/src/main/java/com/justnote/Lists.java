package com.justnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
/**
 * Lists class - note editing activity
 * @author Kyryl Potapenko
 * @since 2017-01-03
 */
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

        Intent intent = getIntent();
        pos = intent.getExtras().getInt("position");
        noteName.setText(intent.getStringExtra("name"));
        mEditText.setText(intent.getStringExtra("alltext"));
    }

    @Override
    protected void onPause() {
        Intent i = new Intent();
        i.putExtra("id", pos);
        i.putExtra("name", noteName.getText().toString());
        i.putExtra("alltext", mEditText.getText().toString());
        setResult(RESULT_OK, i);
        super.onPause();
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