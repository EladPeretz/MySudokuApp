package com.eladper.sudoku;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class CellGroupFragment extends Fragment {
    private int groupId;
    private OnFragmentInteractionListener mListener;
    private View view;

    public CellGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_cell_group, container, false);

        //Set textview click listeners
        int textViews[] = new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4,
                R.id.textView5, R.id.textView6, R.id.textView7, R.id.textView8, R.id.textView9};
        for (int textView1 : textViews) {
            TextView textView = view.findViewById(textView1);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onFragmentInteraction(groupId, Integer.parseInt(view.getTag().toString()), view);
                }
            });
        }
        return view;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setValue(int position, int value) {
        int textViews[] =new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4,
                R.id.textView5, R.id.textView6, R.id.textView7, R.id.textView8, R.id.textView9};
        TextView currentView = view.findViewById(textViews[position]);
        currentView.setText(String.valueOf(value));
        currentView.setTextColor(Color.BLACK);
        currentView.setTypeface(null, Typeface.BOLD);
    }

    public boolean checkGroupCorrect() {
        ArrayList<Integer> numbers = new ArrayList<>();
        int textViews[] = new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4,
                R.id.textView5, R.id.textView6, R.id.textView7, R.id.textView8, R.id.textView9};
        for (int textView1 : textViews) {
            TextView textView = view.findViewById(textView1);
            int number = Integer.parseInt(textView.getText().toString());
            if (numbers.contains(number)) {
                return false;
            } else {
                numbers.add(number);
            }
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    //returns the view of a certain cell
    public void setValueOfSaved(int position, int value) {
        int textViews[] = new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4,
                R.id.textView5, R.id.textView6, R.id.textView7, R.id.textView8, R.id.textView9};
        TextView currentView = view.findViewById(textViews[position]);
        currentView.setText(String.valueOf(value));
        currentView.setTextColor(getResources().getColor(R.color.RestoredNumbersColor));
    }

    //
    //   FROM HERE, 2 METHODS HANDLING THE GUIDELINES APPEARANCE TO WHEN A CELL IS TAPPED.
    //
    //

    public void showGuidelines(int groupId, int cellId, boolean vertical, boolean horizontal, boolean block, boolean identical){
        Drawable.ConstantState constantState=getResources().getDrawable(R.drawable.table_border_cell_unsure).getConstantState();
        //First, whitening all the grid.
        this.hidePreviousGuidelines();
        //cube guidelines
        int textViews[]=new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4,
                R.id.textView5, R.id.textView6, R.id.textView7, R.id.textView8, R.id.textView9};
        if(block) {
            for (int i = 0; i < 9; i++) {
                if (view.findViewById(textViews[i]).getBackground().getConstantState() != constantState)
                    view.findViewById(textViews[i]).setBackgroundResource(R.drawable.table_border_cell_associate);
            }
        }
        //
        //row and column guidelines
        int[] cellGroupFragments=new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3,R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        if(vertical) {
            for (int i = 0; i < 9; i++) {
                if ((groupId - 1) % 3 == i % 3) {
                    for (int j = 0; j < 9; j++) {
                        if (j % 3 == cellId % 3) {
                            if (getFragmentManager().findFragmentById(cellGroupFragments[i]).getView().findViewById(textViews[j]).getBackground().getConstantState() != constantState)
                                getFragmentManager().findFragmentById(cellGroupFragments[i]).getView().findViewById(textViews[j]).setBackgroundResource(R.drawable.table_border_cell_associate);
                        }
                    }
                }
            }
        }
        if(horizontal) {
            for (int i = 0; i < 9; i++) {
                if ((groupId - 1) / 3 == i / 3) {
                    for (int j = 0; j < 9; j++) {
                        if (j / 3 == cellId / 3) {
                            if (getFragmentManager().findFragmentById(cellGroupFragments[i]).getView().findViewById(textViews[j]).getBackground().getConstantState() != constantState)
                                getFragmentManager().findFragmentById(cellGroupFragments[i]).getView().findViewById(textViews[j]).setBackgroundResource(R.drawable.table_border_cell_associate);
                        }
                    }
                }
            }
        }
        //
        //Same number guidelines
        TextView currentView,tempView;
        currentView=view.findViewById(textViews[cellId]);
        if(identical) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    tempView = getFragmentManager().findFragmentById(cellGroupFragments[i]).getView().findViewById(textViews[j]);
                    if (tempView.getText().toString().equals(currentView.getText().toString()) && !currentView.getText().toString().equals("")) {
                        if (getFragmentManager().findFragmentById(cellGroupFragments[i]).getView().findViewById(textViews[j]).getBackground().getConstantState() != constantState)
                            getFragmentManager().findFragmentById(cellGroupFragments[i]).getView().findViewById(textViews[j]).setBackgroundResource(R.drawable.table_border_cell_associate);
                    }
                }
            }
        }
        //Highlight tapped cell
        if(view.findViewById(textViews[cellId]).getBackground().getConstantState()!=constantState)
        view.findViewById(textViews[cellId]).setBackgroundResource(R.drawable.table_border_cell_selected);
    }

    public void hidePreviousGuidelines(){
        Drawable.ConstantState constantState=getResources().getDrawable(R.drawable.table_border_cell_unsure).getConstantState();
        int textViews[]=new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4,
                R.id.textView5, R.id.textView6, R.id.textView7, R.id.textView8, R.id.textView9};
        int[] cellGroupFragments=new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3,R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                if(getFragmentManager().findFragmentById(cellGroupFragments[i]).getView().findViewById(textViews[j]).getBackground().getConstantState()!=constantState)
                getFragmentManager().findFragmentById(cellGroupFragments[i]).getView().findViewById(textViews[j]).setBackgroundResource(R.drawable.table_border_cell);
            }
        }

    }

    //
    //
    //
    //

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int groupId, int cellId, View view);
    }
}
