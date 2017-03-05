package com.example.pavan.multi_notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

public class NoteView extends AppCompatActivity {

    private static final String TAG = "noteView";
    private boolean saveStatus;
    private String oriTitle;
    private String oriScribbleText;
    private int id;

    //instance of the view elements
    private EditText titleEdit;
    private EditText scribbleEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);

        titleEdit = (EditText) findViewById(R.id.title);
        scribbleEdit = (EditText) findViewById(R.id.scribbleAreaET);

        Bundle bun = getIntent().getExtras();

        saveStatus = false;

        if (bun != null) {
            oriTitle = bun.getString(getString(R.string.titleJSONKey));
            oriScribbleText = bun.getString(getString(R.string.scribbleJSONKey));
            id = bun.getInt(getString(R.string.idJSONKey));

            titleEdit.setText(oriTitle);
            scribbleEdit.setText(oriScribbleText);
        } else {
            oriTitle = "";
            oriScribbleText = "";
        }

        scribbleEdit.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                Intent data = new Intent();
                checkIfDataHasChanged();
                data = updateIntentForSave(data);
                setResult(RESULT_OK, data);
                finish();
                break;
            default:
                Log.d(TAG,"Unknown selection");
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Updates the intent with the data that is required to be sent back to the main screen
     * @param data
     * @return
     */
    private Intent updateIntentForSave(Intent data){

        data.putExtra(this.getString(R.string.saveStatus), String.valueOf(saveStatus));

        if(saveStatus){
            data.putExtra(getString(R.string.titleJSONKey), titleEdit.getText().toString());
            data.putExtra(getString(R.string.scribbleJSONKey), scribbleEdit.getText().toString());
            data.putExtra(getString(R.string.lastModifiedJSONKey), DateFormat.getDateTimeInstance().format(new Date()));
            data.putExtra(getString(R.string.idJSONKey), id);
            Toast.makeText(this,getString(R.string.saveToastMsg),Toast.LENGTH_SHORT).show();
        }

        return data;
    }

    @Override
    public void onBackPressed() {
        //have the dialog popup here
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(getString(R.string.yesDialogBtnLbl), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkIfDataHasChanged();
                Intent data = new Intent();
                data = updateIntentForSave(data);
                setResult(RESULT_OK, data);
                NoteView.super.onBackPressed();
            }
        });

        builder.setNegativeButton(getString(R.string.noDialogBtnLbl), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //The user has cancelled the action
                saveStatus = false;
                Intent data = new Intent();
                data = updateIntentForSave(data);
                setResult(RESULT_OK, data);
                NoteView.super.onBackPressed();
            }
        });

        String titleStr = getString(R.string.NotSavedDialogTitle) + "\n"
                + getString(R.string.SaveNoteDialog) + " \""
                + titleEdit.getText().toString() + "\" ?";
        builder.setTitle(titleStr);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This checks if the user has changed the data from the time he has opened the note
     */
    private void checkIfDataHasChanged() {
        if((!oriTitle.equals(titleEdit.getText().toString()) ||
                !oriScribbleText.equals(scribbleEdit.getText().toString())) &&
                    !titleEdit.getText().toString().isEmpty())
            saveStatus = true;
        else{
            saveStatus = false;
            if(titleEdit.getText().toString().isEmpty()){
                Toast.makeText(this, R.string.NoTitleToast, Toast.LENGTH_SHORT).show();
            }
        }


        return;
    }
}
