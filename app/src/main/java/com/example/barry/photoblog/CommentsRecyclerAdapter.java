package com.example.barry.photoblog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/** Part 18: retrieve Comments
 * https://youtu.be/kNqik9Z9ui8
 * 1. extend the adapter to RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder>
 *      implement the methods
 *      create an internal ViewHolder class
 *      altEnter and create the super class for the viewHolder class
 * add the widgets
 * 2. declare a public List and context
 *      - Create an internal constructor CommentsRecyclerAdapter and select commentsList
 *      - add the list to the CommentsActivity
 * 3. get the message
 * 4. in onCreateViewHolder: def the View view
 *          - init the context and
 *          - return the view
 */
public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder>{

    public List<Comments> commentsList;
    public Context context;

    //2. constructor CommentsRecyclerAdapter.
    public CommentsRecyclerAdapter(List<Comments> commentsList) {
        this.commentsList = commentsList;
    }

    @NonNull
    @Override  // public CommentsRecyclerAdapter.ViewHolder onCreate()  - video name
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        String commentMessage = commentsList.get(position).getMessage();
        holder.setComment_message(commentMessage);

    }

    @Override
    public int getItemCount() {

        if (commentsList != null) {

            return commentsList.size();

        }
        else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView comment_message;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setComment_message(String message) {

            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);
        }
    }
}
