package com.example.vent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class TicTacActivity extends AppCompatActivity implements View.OnClickListener{

    private Button[][] buttons = new Button[3][3];
    private Button buttonReset;
    private TextView tvPlayer1, tvPlayer2;

    private static final String TAG = "TicTacActivity";

    private boolean player1Turn = true;
    private int roundCount, player1Points, player2Points;

    // Makes tic tac toe game
    // random move is made my the computer in response to the user's move
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac);

        tvPlayer1 = findViewById(R.id.text_view_p1);
        tvPlayer2 = findViewById(R.id.text_view_p2);

        // saving all the button variables in a 2d list
        for(int i =0;i<3;i++){
            for(int j =0;j<3;j++){
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }

        buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBoard();
            }
        });
    }

    //called every time a button is clicked
    @Override
    public void onClick(View v) {
        //doesnt make a move since button already has X or O
        if(!((Button)v).getText().toString().equals("")){
            return;
        }

        ((Button)v).setText("X");

        String[][] board = getBoard();
        roundCount++;

        //check for win/draw
        //player 1 wins
        if(checkForWin(board)){
           player1wins();

        }
        //draw since board is filled but no player wins
        else if(!boardEmpty(board)){
            draw();

        } else{

            //computer makes move
            board = getBoard();

            // move made is random
           int[] move = randomMove(board);
           buttons[move[0]][move[1]].setText("O");
           board = getBoard();

           //if move made a winning board, computer wins
           if(checkForWin(board)){
               player2Wins();
           }

           //board is filled but no winner, draw
           else if(!boardEmpty(board)){
               draw();
           }
        }

    }

    private void printBoard(String[][] b){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                Log.println(Log.ASSERT, "ticTacBoard: ", b[i][j] + " row: " + i + " col: " + j);
            }
        }
    }

    private boolean checkForWin(String[][] board){

        //checks each row
        for(int i=0;i<3;i++){
            if(board[i][0].equals(board[i][1])
                && board[i][0].equals(board[i][2])
                && !board[i][0].equals("")){
                return true;
            }
        }

        //checks each column
        for(int i=0;i<3;i++){
            if(board[0][i].equals(board[1][i])
                    && board[0][i].equals(board[2][i])
                    && !board[0][i].equals("")){
                return true;
            }
        }

        //checks both the diagonals
        for(int i=0;i<2;i++){
            if(board[1][1].equals(board[2*i][0])
                    && board[1][1].equals(board[2*(1-i)][2])
                    && !board[1][1].equals("")){
                return true;
            }
        }

        return false;

    }

    private void player1wins(){
        player1Points++;
        Toast.makeText(this, "You Win!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void player2Wins(){
        player2Points++;
        Toast.makeText(this, "Venty Wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void draw(){
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    private void updatePointsText(){
        tvPlayer1.setText("You: "+ player1Points);
        tvPlayer2.setText("Venty: "+ player2Points);
    }

    private void resetBoard(){
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                buttons[i][j].setText("");
            }
        }
        roundCount = 0;
        player1Turn = true;
    }

    private String[][] getBoard(){
        String[][] board = new String[3][3];
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                board[i][j] = buttons[i][j].getText().toString();
            }
        }
        return board;
    }

    private boolean boardEmpty(String[][] board){
        boolean empty = false;
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(!board[i][j].equals("")){
                    empty = true;
                    break;
                }
            }
        }
        return empty;
    }

    private int[] randomMove(String[][] board){
        Random random = new Random();
        int row = random.nextInt(3);
        int col = random.nextInt(3);
        while(!board[row][col].equals("")){
            row = random.nextInt(3);
            col = random.nextInt(3);
        }
        return new int[]{row,col};
    }

}
