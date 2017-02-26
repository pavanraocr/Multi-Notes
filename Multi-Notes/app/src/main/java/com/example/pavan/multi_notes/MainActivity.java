package com.example.pavan.multi_notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
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
    private List<Note> notesList = new ArrayList<> ();
    private RecyclerView rv_notes;
    private NotesAdapter notesAdapterObj;

    /*------- Codes that relate to intent -----*/
    public final int itemClickReqCode = 1;
    private final int newNoteReqCode = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv_notes = (RecyclerView) findViewById(R.id.recyclerView);
        notesAdapterObj = new NotesAdapter(notesList, this);

        rv_notes.setAdapter(notesAdapterObj);
        rv_notes.setLayoutManager(new LinearLayoutManager(this));

        // Make some data - not always needed - used to fill list
        for (int i = 0; i < 20; i++) {
            notesList.add(new Note("Title "+String.valueOf(i), "Bla", "Text " +
                    "this is a very very very very very very very very very " +
                    "very very very very very very very very very very very very " +
                    "very very very very very very very very very " +
                    "very very very  long message"+String.valueOf(i)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_addNote:
                Toast.makeText(this, "add note selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, NoteView.class);
                intent.putExtra(getString(R.string.titleJSONKey), "");
                intent.putExtra(getString(R.string.lastModifiedJSONKey), DateFormat.getDateTimeInstance().format(new Date()));
                intent.putExtra(getString(R.string.scribbleJSONKey), "");

                startActivityForResult(intent, newNoteReqCode);
                break;
            case R.id.menu_appInfo:
                Toast.makeText(this, "app info selected", Toast.LENGTH_SHORT).show();
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
        //intent.putExtra(getString(R.string.idJSONKey), n.getId());
        intent.putExtra(getString(R.string.idJSONKey), pos);

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
                notesAdapterObj.notifyDataSetChanged();
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
        //super.onActivityResult(requestCode, resultCode, data);
        int id = data.getIntExtra(getString(R.string.idJSONKey), -1);

        if(requestCode == itemClickReqCode){
            if(resultCode == RESULT_OK){
                String status = data.getStringExtra(getString(R.string.saveStatus));

                if(status.equals("true")){
                    Note tempNote = retrieveIntentResultData(data);

                    /*int i;
                    for(i = 0; i < notesList.size(); i++){
                        Note note = notesList.get(i);

                        if(note.getId() == id){
                            note.setTitle(tempNote.getTitle());
                            note.setScribbleText(tempNote.getScribbleText());
                            note.setLastModifiedTimestamp(tempNote.getLastModifiedTimestamp());
                            break;
                        }
                    }*/

                    if(id < notesList.size() && id >= 0){
                        Note note = notesList.get(id);
                        note.setTitle(tempNote.getTitle());
                        note.setScribbleText(tempNote.getScribbleText());
                        note.setLastModifiedTimestamp(tempNote.getLastModifiedTimestamp());
                        notesList.remove(id);
                        notesList.add(0, note);
                    }

                    notesAdapterObj.notifyDataSetChanged();
                }
            }
        }
        else if(requestCode == newNoteReqCode){
            if(resultCode == RESULT_OK){
                String status = data.getStringExtra(getString(R.string.saveStatus));

                if(status.equals("true")){
                    Note tempNote = retrieveIntentResultData(data);
                    notesList.add(0, tempNote);
                    notesAdapterObj.notifyDataSetChanged();
                }
            }
        }
        else{
            Log.d(TAG, "This is unrecognized result code" + requestCode);
        }
    }
}