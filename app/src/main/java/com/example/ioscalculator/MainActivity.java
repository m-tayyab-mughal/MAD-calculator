package com.example.ioscalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    // Main UI and operation variables
    private TextView display;
    private String currentInput = "";
    private String operator = "";
    private double firstOperand = 0;
    private boolean isNewOperation = true;

    private DecimalFormat formatter = new DecimalFormat("#.########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        display = findViewById(R.id.display);
        setupNumberButtons();
        setupOperatorButtons();
        setupSpecialButtons();
        setupScientificButtons();
    }

    private void setupNumberButtons() {
        int[] numberButtonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        };

        View.OnClickListener numberListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;

                if (isNewOperation) {
                    display.setText("");
                    isNewOperation = false;
                }

                String buttonText = button.getText().toString();
                if (buttonText.equals(".") && currentInput.contains(".")) {
                    return;
                }
                currentInput += buttonText;
                display.setText(currentInput);
            }
        };

        // Attach the listener to all number buttons
        for (int id : numberButtonIds) {
            Button button = findViewById(id);
            if (button != null) {
                button.setOnClickListener(numberListener);
            }
        }
    }

    private void setupOperatorButtons() {
        int[] operatorButtonIds = {
                R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide, R.id.btnEqual
        };

        View.OnClickListener operatorListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String buttonText = button.getText().toString();

                try {
                    if (!currentInput.isEmpty()) {
                        if (operator.isEmpty()) {
                            firstOperand = Double.parseDouble(currentInput);
                        } else {
                            double secondOperand = Double.parseDouble(currentInput);
                            firstOperand = performOperation(firstOperand, secondOperand, operator);
                            display.setText(formatter.format(firstOperand));
                        }
                    }

                    if (buttonText.equals("=")) {
                        operator = "";
                    } else {
                        operator = buttonText;
                    }

                    currentInput = "";
                    isNewOperation = true;

                } catch (Exception e) {
                    display.setText("Error");
                    resetCalculator();
                }
            }
        };

        for (int id : operatorButtonIds) {
            Button button = findViewById(id);
            if (button != null) {
                button.setOnClickListener(operatorListener);
            }
        }
    }


    private void setupSpecialButtons() {
        // Clear button - reset everything
        Button btnClear = findViewById(R.id.btnClear);
        if (btnClear != null) {
            btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetCalculator();
                    display.setText("0");
                }
            });
        }

        Button btnPlusMinus = findViewById(R.id.btnPm);
        if (btnPlusMinus != null) {
            btnPlusMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!currentInput.isEmpty()) {
                        try {
                            // Negate the current value
                            double value = Double.parseDouble(currentInput);
                            value = -value;
                            currentInput = formatter.format(value);
                            display.setText(currentInput);
                        } catch (Exception e) {
                            display.setText("Error");
                            currentInput = "";
                        }
                    }
                }
            });
        }

        Button btnPercent = findViewById(R.id.btnPer);
        if (btnPercent != null) {
            btnPercent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!currentInput.isEmpty()) {
                        try {
                            double value = Double.parseDouble(currentInput);
                            value = value / 100;  // Convert to percentage
                            currentInput = formatter.format(value);
                            display.setText(currentInput);
                        } catch (Exception e) {
                            display.setText("Error");
                            currentInput = "";
                        }
                    }
                }
            });
        }
    }
    //scientific buttons
    private void setupScientificButtons() {
        // Square (x²) button
        setupScientificButton(R.id.btnsqr, new ScientificOperation() {
            @Override
            public double perform(double value) {
                return value * value;  // Square the number
            }
        });

        Button btnPower = findViewById(R.id.btnpower);
        if (btnPower != null) {
            btnPower.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!currentInput.isEmpty()) {
                        try {
                            firstOperand = Double.parseDouble(currentInput);
                            operator = "^";  // Power operator
                            currentInput = "";
                            isNewOperation = true;
                        } catch (Exception e) {
                            display.setText("Error");
                            currentInput = "";
                        }
                    }
                }
            });
        }

        setupScientificButton(R.id.btnsqrt, new ScientificOperation() {
            @Override
            public double perform(double value) throws ArithmeticException {
                if (value < 0) {
                    throw new ArithmeticException("Cannot take square root of negative number");
                }
                return Math.sqrt(value);
            }
        });

        setupScientificButton(R.id.qubert, new ScientificOperation() {
            @Override
            public double perform(double value) {
                return Math.cbrt(value);  // Cube root
            }
        });

        // Factorial button (x!)
        setupScientificButton(R.id.btnfect, new ScientificOperation() {
            @Override
            public double perform(double value) throws ArithmeticException {
                if (value < 0 || value != Math.floor(value)) {
                    throw new ArithmeticException("Factorial only works with positive integers");
                }

                int n = (int) value;
                long factorial = 1;
                for (int i = 1; i <= n; i++) {
                    factorial *= i;
                    if (factorial < 0) { // Overflow check
                        throw new ArithmeticException("Factorial too large");
                    }
                }
                return factorial;
            }
        });

        setupScientificButton(R.id.btnlog, new ScientificOperation() {
            @Override
            public double perform(double value) throws ArithmeticException {
                if (value <= 0) {
                    throw new ArithmeticException("Cannot take log of zero or negative number");
                }
                return Math.log10(value);
            }
        });

        setupScientificButton(R.id.btnsin, new ScientificOperation() {
            @Override
            public double perform(double value) {
                // Convert degrees to radians for Math.sin
                return Math.sin(Math.toRadians(value));
            }
        });

        setupScientificButton(R.id.btncos, new ScientificOperation() {
            @Override
            public double perform(double value) {
                return Math.cos(Math.toRadians(value));
            }
        });

        setupScientificButton(R.id.btntan, new ScientificOperation() {
            @Override
            public double perform(double value) throws ArithmeticException {
                if (value % 180 == 90) {
                    throw new ArithmeticException("Tangent undefined at this angle");
                }
                return Math.tan(Math.toRadians(value));
            }
        });
        Button btnPi = findViewById(R.id.btnpi);
        if (btnPi != null) {
            btnPi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentInput = formatter.format(Math.PI);
                    display.setText(currentInput);
                    isNewOperation = false;
                }
            });
        }
    }

    private interface ScientificOperation {
        double perform(double value) throws ArithmeticException;
    }


    private void setupScientificButton(int buttonId, final ScientificOperation operation) {
        Button button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!currentInput.isEmpty()) {
                        try {
                            double value = Double.parseDouble(currentInput);
                            value = operation.perform(value);
                            currentInput = formatter.format(value);
                            display.setText(currentInput);
                        } catch (ArithmeticException e) {
                            display.setText("Error");
                            currentInput = "";
                        } catch (Exception e) {
                            display.setText("Error");
                            currentInput = "";
                        }
                    }
                }
            });
        }
    }
    private void resetCalculator() {
        currentInput = "";
        operator = "";
        firstOperand = 0;
        isNewOperation = true;
    }
    private double performOperation(double operand1, double operand2, String operator) {
        switch (operator) {
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "×":
                return operand1 * operand2;
            case "÷":
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return operand1 / operand2;
            case "^":
                return Math.pow(operand1, operand2);
            default:
                return operand2;
        }
    }
}