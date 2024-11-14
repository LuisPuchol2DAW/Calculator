package com.example.calculator;

import com.example.calculator.databinding.ActivityMainBinding;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private static final String TAG = "MainActivity";
    private GestureDetector gestureDetector;
    private float startX, startY, endX, endY;

    private ActivityMainBinding binding;

    private Long volatileNum1 = 0L;
    private Long volatileNum2 = 0L;
    private Long memoryNum = 0L;
    private Character currentOperation = null;
    private Boolean isSecondOperand = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Activity has been created");
        super.onCreate(savedInstanceState);
        startStuff();
        startLayout();
        startGesture();

        startBinding();
        startCalculator();
    }

    private void startCalculator() {
        buttonsMemory();

        buttonNumbers();

        buttonOperation();

        buttonEquals();

        buttonsEraseBack();
    }

    private void buttonsMemory(){
        binding.buttonMS.setOnClickListener(view -> {
            memoryNum = Long.parseLong(binding.calculatorResult.getText().toString());
        });

        binding.buttonMSPlus.setOnClickListener(view -> {
            binding.calculatorResult.setText(String.valueOf(memoryNum + Long.parseLong(binding.calculatorResult.getText().toString())));
        });

        binding.buttonMSLess.setOnClickListener(view -> {
            binding.calculatorResult.setText(String.valueOf(memoryNum - Long.parseLong(binding.calculatorResult.getText().toString())));
        });

        binding.buttonMC.setOnClickListener(view -> {
            memoryNum = 0L;
        });

        binding.buttonMR.setOnClickListener(view -> {
            binding.calculatorResult.setText(String.valueOf(memoryNum));
        });
    }

    private void buttonsEraseBack() {
        binding.buttonEraseAll.setOnClickListener(view -> {
            volatileNum1 = 0L;
            volatileNum2 = 0L;
            currentOperation = null;
            isSecondOperand = false;
            updateDisplay("0");
        });
        binding.buttonBack.setOnClickListener(view -> {
            String currentText = binding.calculatorResult.getText().toString();
            if (!currentText.isEmpty()) {
                currentText = currentText.substring(0, currentText.length() - 1);
                updateDisplay(currentText.isEmpty() ? "0" : currentText);
            }
        });
    }

    private void buttonEquals() {
        binding.buttonEquals.setOnClickListener(view -> {
            if (currentOperation != null) {
                volatileNum2 = Long.parseLong(binding.calculatorResult.getText().toString());
                Long total = 0L;

                switch (currentOperation) {
                    case 'A': total = volatileNum1 + volatileNum2; break;
                    case 'S': total = volatileNum1 - volatileNum2; break;
                    case 'M': total = volatileNum1 * volatileNum2; break;
                    case 'D':
                        if (volatileNum2 != 0) {
                            total = volatileNum1 / volatileNum2;
                        } else {
                            updateDisplay("Error");
                            return;
                        }
                        break;
                }
                updateDisplay(String.valueOf(total));
                volatileNum1 = total;
                isSecondOperand = false;
            }
        });
    }

    private void buttonOperation() {
        binding.buttonAdd.setOnClickListener(view -> setOperation('A'));
        binding.buttonSubtract.setOnClickListener(view -> setOperation('S'));
        binding.buttonMultiply.setOnClickListener(view -> setOperation('M'));
        binding.buttonDivide.setOnClickListener(view -> setOperation('D'));
    }

    private void buttonNumbers() {
        binding.button1.setOnClickListener(view -> appendNumber("1"));
        binding.button2.setOnClickListener(view -> appendNumber("2"));
        binding.button3.setOnClickListener(view -> appendNumber("3"));
        binding.button4.setOnClickListener(view -> appendNumber("4"));
        binding.button5.setOnClickListener(view -> appendNumber("5"));
        binding.button6.setOnClickListener(view -> appendNumber("6"));
        binding.button7.setOnClickListener(view -> appendNumber("7"));
        binding.button8.setOnClickListener(view -> appendNumber("8"));
        binding.button9.setOnClickListener(view -> appendNumber("9"));
        binding.button0.setOnClickListener(view -> appendNumber("0"));
        binding.button00.setOnClickListener(view -> appendNumber("00"));
    }

    private void startBinding() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void updateDisplay(String value) {
        binding.calculatorResult.setText(value);
    }

    private void appendNumber(String num) {
        String currentNumber = binding.calculatorResult.getText().toString();
        if (currentNumber.equals("0") || isSecondOperand) {
            updateDisplay(num);
            isSecondOperand = false;
        } else {
            updateDisplay(currentNumber + num);
        }
    }

    private void setOperation(Character operation) {
        volatileNum1 = Long.parseLong(binding.calculatorResult.getText().toString());
        currentOperation = operation;
        isSecondOperand = true;
    }

    public void startLayout() {

    }

    public void startStuff() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void startGesture() {
        gestureDetector = new GestureDetector(this, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("firstOperand", volatileNum1);
        outState.putLong("secondOperand", volatileNum2);
        outState.putChar("currentOperation", currentOperation);
        outState.putBoolean("isSecondOperand", isSecondOperand);
        outState.putString("display", binding.calculatorResult.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        volatileNum1 = savedInstanceState.getLong("firstOperand");
        volatileNum2 = savedInstanceState.getLong("secondOperand");
        currentOperation = savedInstanceState.getChar("currentOperation");
        isSecondOperand = savedInstanceState.getBoolean("isSecondOperand");
        binding.calculatorResult.setText(savedInstanceState.getString("display"));
    }



    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //parte de abajo de la pantalla mayor valor Y
        //parte derecha de la pantalla mayor valor X
        Log.d(TAG, "onFling: Fling gesture detected with velocityX = " + velocityX + " and velocityY = " + velocityY);

        float movementX = startX - endX;
        float movementY = startY - endY;

        if (movementY > movementX && movementY > -movementX) {
            Log.d(TAG, "onFling: Fling gesture detected up");
        } else if (movementX > movementY && movementX > -movementY) {
            Log.d(TAG, "onFling: Fling gesture detected left");
        } else if (movementY > movementX && movementY < -movementX) {
            Log.d(TAG, "onFling: Fling gesture detected right");
        } else if (movementX > movementY && movementX < -movementY) {
            Log.d(TAG, "onFling: Fling gesture detected down");
        }

        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        startX = e.getX();
        startY = e.getY();
        Log.d(TAG, "onDown: User touched the screen");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(TAG, "onShowPress: User is pressing on the screen");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp: Single tap detected");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        endX = e2.getX();
        endY = e2.getY();
        Log.d(TAG, "onScroll: Scroll gesture detected with distanceX = " + distanceX + " and distanceY = " + distanceY);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(TAG, "onLongPress: Long press detected");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Activity is starting");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity has resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity is pausing");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Activity has stopped");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: Activity is restarting");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity is being destroyed");
    }
}