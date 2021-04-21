package com.example.vent;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecreationFragment extends Fragment {

    private View rootView;
    private ImageButton ticTacBtn;

    public RecreationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_recreation, container, false);


        ticTacBtn = rootView.findViewById(R.id.tic_tac_toe_btn);

        //send user to TicTacToe activity when image button clicked
        ticTacBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ticTacIntent = new Intent(getContext(), TicTacActivity.class);
                startActivity(ticTacIntent);
            }
        });

        return rootView;
    }

}
