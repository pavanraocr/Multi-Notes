package com.example.pavan.multi_notes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by pavan on 2/20/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesViewHolder>{

    private static final String TAG = "NoteAdapter";
    private List<Note> notesLst;
    private MainActivity mainAct;

    public NotesAdapter(List<Note> notesLst, MainActivity mainAct) {
        this.notesLst = notesLst;
        this.mainAct = mainAct;
    }

    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_listview_element, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new NotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotesViewHolder holder, int position) {
        Note noteItem = notesLst.get(position);

        holder.title.setText(noteItem.getTitle());
        holder.lastModified.setText(noteItem.getLastModifiedTimestamp());
        String dispStr = noteItem.getScribbleText();
        if(dispStr.length() > 79){
            dispStr = dispStr.substring(0,81);
            dispStr += "...";
        }

        holder.scribbleNote.setText(dispStr);
    }



    @Override
    public int getItemCount() {
        return notesLst.size();
    }
}
