package com.eladper.sudoku;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.os.LocaleListCompat.create;

public class BoardNoteAdapter extends RecyclerView.Adapter<BoardNoteAdapter.BoardNoteHolder> {

    public static final String EXTRA_ID =
            "com.eladper.sudoku.EXTRA_ID";
    public static final String EXTRA_BOARD_NUMBER =
            "com.eladper.sudoku.EXTRA_BOARD_NUMBER";


    private List<BoardNote> notes = new ArrayList<>();
    private onItemClickListener listener;
    private Context context;

    @NonNull
    @Override
    public BoardNoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.board_note_item,viewGroup,false);
        return new BoardNoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardNoteHolder boardNoteHolder, int i) {
        String boardImageDrawableRes;

        BoardNote currentBoardNote = notes.get(i);
        if(currentBoardNote.getStatus().equals("Not completed")) {
            boardNoteHolder.status.setText(context.getResources().getText(R.string.not_completed));
            boardNoteHolder.status.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        }
        else
            boardNoteHolder.status.setText(currentBoardNote.getStatus());
        boardNoteHolder.status.setAllCaps(true);
        boardNoteHolder.boardNumber.setText(String.valueOf(currentBoardNote.getBoardNumber()));
        boardNoteHolder.difficulty.setText(currentBoardNote.getDifficulty());
        boardNoteHolder.difficulty.setAllCaps(true);

        if (currentBoardNote.getStatus().equals("Completed"))
            boardNoteHolder.statusImage.setImageResource(R.drawable.ic_board_completed_status_24dp);
        if (currentBoardNote.getStatus().equals("Not completed"))
            boardNoteHolder.statusImage.setImageResource(0);
        if (currentBoardNote.getStatus().equals("Locked"))
            boardNoteHolder.statusImage.setImageResource(R.drawable.ic_board_locked_status);

        boardImageDrawableRes = currentBoardNote.getDifficulty()+"board"+currentBoardNote.getBoardNumber();
        Picasso.with(context)
                .load(context.getResources().getIdentifier("com.eladper.sudoku:drawable/" + boardImageDrawableRes, null, null))
                .fit()
                .into(boardNoteHolder.boardImage);
        //boardNoteHolder.boardImage.setImageResource(context.getResources().getIdentifier("com.eladper.sudoku:drawable/" + boardImageDrawableRes, null, null));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<BoardNote> notes){
        this.notes = notes;
        notifyDataSetChanged();
    }

    public BoardNote getNoteAt(int position){
        return notes.get(position);
    }

    class BoardNoteHolder extends RecyclerView.ViewHolder{

        private TextView difficulty;
        private TextView status;
        private TextView boardNumber;
        private ImageView boardImage;
        private ImageView statusImage;

        public BoardNoteHolder(@NonNull View itemView) {
            super(itemView);
            this.difficulty = itemView.findViewById(R.id.text_view_board_difficulty);
            this.status = itemView.findViewById(R.id.text_view_status);
            this.boardNumber = itemView.findViewById(R.id.text_view_board_number);
            this.boardImage = itemView.findViewById(R.id.image_view_board);
            this.statusImage = itemView.findViewById(R.id.image_view_status);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener!=null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(notes.get(position));
                }
            });
        }
    }

    public interface onItemClickListener{
        void onItemClick(BoardNote boardNote);
    }

    public void setOnClickListener(onItemClickListener listener){
        this.listener = listener;
    }
}
