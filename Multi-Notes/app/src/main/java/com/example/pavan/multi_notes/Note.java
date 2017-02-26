package com.example.pavan.multi_notes;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by pavan on 2/20/17.
 */

public class Note {

    private final static String TAG = "Note";


    private String title;
    private String lastModifiedTimestamp;
    private String scribbleText;
    private static int id = 0;

    public Note() {
        title = "";
        lastModifiedTimestamp = "";
        scribbleText = "";
    }

    /**
     * constructor which creates an object with the passed data or with the empty string in the scribble
     * area and current time in the last modified time
     * @param title - title of the note
     * @param lastModifiedTime String that has the last modified date and time or ""
     * @param scribbleTxt String that contains the text from the scribble area or ""
     */
    public Note(String title, String lastModifiedTime, String scribbleTxt){
        if(title.isEmpty())
            this.title = "";
        else
            this.title = title;

        if(lastModifiedTime.isEmpty())
            lastModifiedTimestamp = DateFormat.getDateTimeInstance().format(new Date());
        else
            lastModifiedTimestamp = lastModifiedTime;

        if(scribbleTxt.isEmpty())
            scribbleText = "";
        else
            scribbleText = scribbleTxt;

        id++;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public String getScribbleText() {
        return scribbleText;
    }

    public void setLastModifiedTimestamp(String lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }

    public void setScribbleText(String scribbleText) {
        this.scribbleText = scribbleText;
    }

    public static int getId() {
        return id;
    }
    public static void setId(int id) {
        Note.id = id;
    }
}
