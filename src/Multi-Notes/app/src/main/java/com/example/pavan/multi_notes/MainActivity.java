package com.example.pavan.multi_notes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";
    private List<Note> notesList = new ArrayList<>();
    private RecyclerView rv_notes;
    private NotesAdapter notesAdapterObj;
    private JsonReaderAndWriter readWriteObj;
    private boolean isAsyncTaskRunning;
    private boolean isWriteToFilePending;
    private boolean isIntentToEditNoteView;

    /*------- Codes that relate to intent -----*/
    public final int itemClickReqCode = 1;
    private final int newNoteReqCode = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv_notes = (RecyclerView) findViewById(R.id.recyclerView);
        notesAdapterObj = new NotesAdapter(notesList, this);
        readWriteObj = new JsonReaderAndWriter();

        rv_notes.setAdapter(notesAdapterObj);
        rv_notes.setLayoutManager(new LinearLayoutManager(this));
        rv_notes.setHasFixedSize(false);

        isAsyncTaskRunning = false;
        isWriteToFilePending = false;
        isIntentToEditNoteView = false;

        loadNotes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isIntentToEditNoteView = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "the activity has gone into pause");
        if(isWriteToFilePending && saveNotes() && !isIntentToEditNoteView)
            Toast.makeText(this, getString(R.string.saveToastMsg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.menu_addNote:
                //Toast.makeText(this, "add note selected", Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, NoteView.class);
                intent.putExtra(getString(R.string.titleJSONKey), "");
                intent.putExtra(getString(R.string.lastModifiedJSONKey), DateFormat.getDateTimeInstance().format(new Date()));
                intent.putExtra(getString(R.string.scribbleJSONKey), "");
                isIntentToEditNoteView = true;
                startActivityForResult(intent, newNoteReqCode);
                break;
            case R.id.menu_appInfo:
                //Toast.makeText(this, "app info selected", Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, Infomation.class);
                isIntentToEditNoteView = true;
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "selection unknown", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        int pos = rv_notes.getChildLayoutPosition(v);
        Note n = notesList.get(pos);

        Intent intent = new Intent(MainActivity.this, NoteView.class);
        intent.putExtra(getString(R.string.titleJSONKey), n.getTitle());
        intent.putExtra(getString(R.string.lastModifiedJSONKey), n.getLastModifiedTimestamp());
        intent.putExtra(getString(R.string.scribbleJSONKey), n.getScribbleText());
        intent.putExtra(getString(R.string.idJSONKey), pos);
        isIntentToEditNoteView = true;

        startActivityForResult(intent, itemClickReqCode);
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = rv_notes.getChildLayoutPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton(getString(R.string.confirmDeleteLbl), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notesList.remove(pos);
                //notesAdapterObj.notifyDataSetChanged();
                refreshRecyclerView();
                isWriteToFilePending = true;
            }
        });

        builder.setNegativeButton(getString(R.string.cancelDialogLbl), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cancel is pressed by the user do nothing
            }
        });
        builder.setTitle("Confirm delete Note?");
        AlertDialog dialog = builder.create();

        dialog.show();

        return false;
    }

    /**
     * This extracts the extra data from the result returned from the view
     * @param data - Intent that contains the extra data
     * @return Note - Object of Note class that contains the data sent from a different intent
     */
    private Note retrieveIntentResultData(Intent data){
        Note tempNote = new Note();
        tempNote.setTitle(data.getStringExtra(getString(R.string.titleJSONKey)));
        tempNote.setScribbleText(data.getStringExtra(getString(R.string.scribbleJSONKey)));
        tempNote.setLastModifiedTimestamp(data.getStringExtra(getString(R.string.lastModifiedJSONKey)));

        return tempNote;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Intent has come with results");
        int id = data.getIntExtra(getString(R.string.idJSONKey), -1);

        if(requestCode == itemClickReqCode){
            if(resultCode == RESULT_OK){
                String status = data.getStringExtra(getString(R.string.saveStatus));

                if(status.equals("true")){
                    isWriteToFilePending = true;
                    Note tempNote = retrieveIntentResultData(data);

                    if(id < notesList.size() && id >= 0){
                        Note note = notesList.get(id);
                        note.setTitle(tempNote.getTitle());
                        note.setScribbleText(tempNote.getScribbleText());
                        note.setLastModifiedTimestamp(tempNote.getLastModifiedTimestamp());
                        notesList.remove(id);
                        notesList.add(0, note);
                    }

                    //notesAdapterObj.notifyDataSetChanged();
                    refreshRecyclerView();
                }
            }
        }
        else if(requestCode == newNoteReqCode){
            if(resultCode == RESULT_OK){
                String status = data.getStringExtra(getString(R.string.saveStatus));

                if(status.equals("true")){
                    isWriteToFilePending = true;
                    Note tempNote = retrieveIntentResultData(data);
                    notesList.add(0, tempNote);
                    //notesAdapterObj.notifyDataSetChanged();
                    refreshRecyclerView();
                }
            }
        }
        else{
            Log.d(TAG, "This is unrecognized result code" + requestCode);
        }

        isIntentToEditNoteView = false;
    }

    class ReadAsync extends AsyncTask<Context, Void, Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            notesList.clear();
            readWriteObj.setListOfNotes(notesList);
        }

        @Override
        protected Boolean doInBackground(Context... params) {
            Log.d(TAG, "async task started with notelist size = " + String.valueOf(notesList.size()));
            isAsyncTaskRunning = true;
            boolean loadStatus = readWriteObj.loadNotes(params[0]);
            isAsyncTaskRunning = false;
            Log.d(TAG, "async task ended and returning status " + String.valueOf(loadStatus));

            return loadStatus;
        }

        @Override
        protected void onPostExecute(Boolean loadStatus) {
            super.onPostExecute(loadStatus);

            if(loadStatus){
                //notesList.addAll(readWriteObj.getListOfNotes());
                Log.d(TAG, "onPostexe notelist size = " + String.valueOf(notesList.size()));
                //notesAdapterObj.notifyDataSetChanged();
                refreshRecyclerView();
            }
            else{
                Log.d(TAG, "Load error");
            }
        }
    }

    private void refreshRecyclerView(){
        notesAdapterObj.notifyDataSetChanged();
        rv_notes.forceLayout();
    }

    /**
     * This method invokes an async task that loads the notes from a JSON file if it exists
     * @return True if the task was started and false if there already exists an async task
     */
    private boolean loadNotes() {
        if(isAsyncTaskRunning){
            Log.d(TAG, "There is an async task already running");
            return false;
        }

        new ReadAsync().execute(getApplication());
        return true;
    }

    /**
     * This saves the current list of all the notes into a JSON file
     * @return
     */
    private boolean saveNotes(){
        Log.d(TAG, "saving Note with notelist size = " + String.valueOf(notesList.size()));
        readWriteObj.setListOfNotes(notesList);
        boolean status =  readWriteObj.SaveNotes(getApplicationContext());
        if(status)
            isWriteToFilePending = false;

        Log.d(TAG, "After saving Note with notelist size = " + String.valueOf(notesList.size()));
        return status;
    }
}