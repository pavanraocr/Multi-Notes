package com.example.pavan.multi_notes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by pavan on 2/23/17.
 */

public class JsonReaderAndWriter {

    private final static String TAG = "jsonReaderAndWriter";
    private List<Note> listOfNotes = new ArrayList<>();

    /* Getter and Setters */
    public List<Note> getListOfNotes() {
        return listOfNotes;
    }

    public void setListOfNotes(List<Note> listOfNotes) {
        this.listOfNotes = listOfNotes;
    }

    /**
     * Saves the session in the file which is denoted by the file output stream
     * @return boolean value denoting true if the task is successful false otherwise
     */
    public boolean SaveNotes(Context c){
        FileOutputStream fs;

        try {
            fs = c.openFileOutput(c.getString(R.string.jsonFileName), c.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG,"There was something wrong in saving the json file");
            return false;
        }

        if(fs == null){
            Log.d(TAG, "The fileOutputStream sent into the function is not set\n");
            return false;
        }

        try {
            JsonWriter jsonWrtr = new JsonWriter(new OutputStreamWriter(fs, "UTF-8"));
            jsonWrtr.setIndent("  ");
            for(Note n: listOfNotes){
                writeNote(c, jsonWrtr, n);
            }
            jsonWrtr.close();

            Log.d(TAG, "File saved\n");
            return true;

        } catch(Exception e){
            e.getStackTrace();
        }

        return false; //the code should not reach here
    }

    private void writeNote(Context c, JsonWriter writer, Note note) throws IOException {
        writer.beginObject();
        writer.name(c.getString(R.string.idJSONKey)).value(note.getId());
        writer.name(c.getString(R.string.lastModifiedJSONKey)).value(note.getLastModifiedTimestamp());
        writer.name(c.getString(R.string.scribbleJSONKey)).value(note.getScribbleText());
        writer.name(c.getString(R.string.titleJSONKey)).value(note.getTitle());
        writer.endObject();
    }

    /**
     * Loads the data from the json file which is denoted by the file input stream
     * @return false if the file descriptor is not null true otherwise
     */
    public boolean loadNotes(Context c){
        InputStream fs;

        try {
            fs = c.openFileInput(c.getString(R.string.jsonFileName));
        } catch (FileNotFoundException e) {
            //This is the first time the app has opened since the last time the data of the app was cleared
            Log.d(TAG, "No JSON file for loading");
            listOfNotes = new ArrayList<Note>();
            return true;
        }
        catch (Exception e){
            e.getStackTrace();
            return false;
        }

        Log.d(TAG, "loading the file from the json file");

        if(fs == null){
            Log.d(TAG, "The inputStream sent into the function is not set\n" );
            return false;
        }

        try{
            JsonReader rdr = new JsonReader(new InputStreamReader(fs, "UTF-8"));

            listOfNotes = new ArrayList<Note>();

            try{
                readAllNotes(c, rdr);
            }finally {
                rdr.close();
            }
        }
        catch(Exception e){
            e.getStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Reads all the notes in the json file
     * @param c - Context of the application
     * @param rdr - reader object from which the JSON data is gathered from
     * @throws IOException - If the rdr fails to read the fails
     */
    private void readAllNotes(Context c, JsonReader rdr) throws IOException {
        rdr.beginArray();

        while (rdr.hasNext()){
            listOfNotes.add(parseNote(c, rdr));
        }

        rdr.endArray();
    }

    /**
     * Reads each element of a note and creates a new object of note class
     * @param rdr - Json Reader
     * @param c - Context of the application
     * @return  the new object with updated note data
     * @throws IOException
     */
    private Note parseNote(Context c, JsonReader rdr) throws IOException {
        Note newNote = new Note();
        int id = 0;

        rdr.beginObject();
        while (rdr.hasNext()){
            String name = rdr.nextName();

            if(name.equals(R.string.idJSONKey)){
                newNote.setId(id = Math.max(rdr.nextInt(), id));
            }
            else if(name.equals(R.string.titleJSONKey)){
                newNote.setTitle(rdr.nextString());
            }
            else if(name.equals(R.string.lastModifiedJSONKey)){
                newNote.setLastModifiedTimestamp(rdr.nextString());
            }
            else if(name.equals(R.string.scribbleJSONKey)){
                newNote.setScribbleText(rdr.nextString());
            }
            else{
                //This key is not recognized in the present implementation and should not have
                //  been placed in this json object
                Log.d(TAG, "This key is not recognized in the present implementation " + name);
                rdr.skipValue();
            }
        }
        rdr.endObject();

        return newNote;
    }
}
