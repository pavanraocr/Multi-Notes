package com.example.pavan.multi_notes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by pavan on 2/20/17.
 */

public class NotesViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView lastModified;
    public TextView scribbleNote;

    public NotesViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.tv_title);
        lastModified = (TextView) itemView.findViewById(R.id.tv_lastModifiedTimestamp);
        scribbleNote = (TextView) itemView.findViewById(R.id.tv_scribble);
    }
}
