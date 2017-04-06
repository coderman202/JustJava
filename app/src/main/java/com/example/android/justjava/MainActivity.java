package com.example.android.justjava;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * This app displays an order form to order coffee.
 */
public class MainActivity extends AppCompatActivity {

    //Initialise the quantity, price and total variables.
    int quantity = 0;
    int pricePerCup = 5;
    double total = 0;
    double toppingsTotal = 0;

    String extrasText, summary;

    //Declare the views for initialisation in the onCreate method
    TextView totalCostView, quantityTextView;
    CheckBox whippedCream, chocolateSprinkles, caramelSyrup;
    EditText name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        totalCostView = (TextView) findViewById(R.id.total);
        quantityTextView = (TextView) findViewById(R.id.quantity);
        whippedCream = (CheckBox) findViewById(R.id.whipped_cream);
        chocolateSprinkles = (CheckBox) findViewById(R.id.chocolate_sprinkles);
        caramelSyrup = (CheckBox) findViewById(R.id.caramel_syrup);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);

        displayTotalCost();
        displayQuantity();
    }

    /*
    * This closes the keyboard once you click anywhere on the screen outside the EditText
    * This solution is thanks to: http://stackoverflow.com/users/840558/daniel
    * Found here: https://tinyurl.com/lctudx6
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event "+event.getRawX()+","+event.getRawY()+" "+x+","+y+" rect "+w.getLeft()+","+w.getTop()+","+w.getRight()+","+w.getBottom()+" coords "+scrcoords[0]+","+scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    /**
     * When order button is clicked, create the submit order button.
     */
    public void submitOrder(View view) {
        createOrderSummary();
    }

    /**
     * Creates and displays the summary of the order
     */
    public void createOrderSummary(){
        if(total == 0){
            Toast.makeText(this, "Buy something, you cheap bastard!", Toast.LENGTH_SHORT).show();
        }
        else{
            String[] address = new String[]{email.getText().toString()};

            composeEmail(address, getString(R.string.email_summary_subject, name.getText()), getString(R.string.email_summary_body, name.getText(), ""+quantity, extrasText, ""+total));
        }
    }

    /**
     * This method displays the given quantity value on quantity TextView.
     */
    private void displayQuantity() {
        quantityTextView.setText("" + quantity);
    }

    /**
     * This method displays the total price on the total cost TextView.
     */
    private void displayTotalCost() {
        calculateTotalCost();
        Locale locale = new Locale("en", "IE"); //Setting my locale to ensure the correct currency.
        totalCostView.setText(NumberFormat.getCurrencyInstance(locale).format(total));

    }
    /**
     * Calculates the price of the order.
     */
    private void calculateTotalCost() {
        getToppingsTotal();
        total = (quantity * pricePerCup) + toppingsTotal;
        Log.v("Toppings total: ", Double.toString(toppingsTotal));
        Log.v("Total: ", Double.toString(total));

    }

    private void getToppingsTotal(){
        toppingsTotal = 0;
        extrasText = getString(R.string.extras);

        if(whippedCream.isChecked()){
            toppingsTotal += quantity * Double.parseDouble(whippedCream.getTag().toString());
            extrasText += "\n" + getString(R.string.whipped_cream);
        }
        if(chocolateSprinkles.isChecked()){
            toppingsTotal += quantity * Double.parseDouble(chocolateSprinkles.getTag().toString());
            extrasText += "\n" + getString(R.string.chocolate_sprinkles);
        }
        if(caramelSyrup.isChecked()){
            toppingsTotal += quantity * Double.parseDouble(caramelSyrup.getTag().toString());
            extrasText += "\n" + getString(R.string.caramel_syrup);
        }
    }

    // Add to quantity when plus button is clicked
    public void increment(View view){
        if(quantity < 100){
            quantity++;
        }
        else {
            Toast.makeText(this, "You can't order more than 100 cups you lunatic!", Toast.LENGTH_SHORT).show();
        }
        displayQuantity();
        displayTotalCost();
    }

    // Subtract from quantity when minus button is clicked
    public void decrement(View view){
        if(quantity >= 1){
            quantity--;
        }
        else{
            Toast.makeText(this, "You can't order a negative number of cups you madman!", Toast.LENGTH_SHORT).show();
        }
        displayQuantity();
        displayTotalCost();
    }

    public void addExtras(View view){
        displayTotalCost();
    }

    public void composeEmail(String[] address, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}