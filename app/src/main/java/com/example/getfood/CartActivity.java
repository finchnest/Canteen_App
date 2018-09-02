package com.example.getfood;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CartActivity extends AppCompatActivity {

    ListView cartListView;
    CartDisplayAdapter cartDisplayAdapter;

    TextView totalPriceTV;
    Button orderButton;
//    alertdialog views
    Button alertPlus, alertMinus;
    TextView quantitySetTV;
    Boolean flag = false;
    public static int total;

//    checksum parameters
    private static String MID = "GetFoo88084336099945";
    private static String MercahntKey = "LkU0z2fR_!a5yR4U";
    private static String INDUSTRY_TYPE_ID = "Retail";
    private static String CHANNLE_ID = "WAP";
    private static String WEBSITE = "APP_STAGING";
    private static String CALLBACK_URL = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
    public static String checkSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartListView = findViewById(R.id.cartListView);

        totalPriceTV = findViewById(R.id.totalPriceTV);
        orderButton = findViewById(R.id.orderButton);

        setDisplayListView();

//        show dialog to adjust the quantity of items in the cart
        cartListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder2 = new AlertDialog.Builder(CartActivity.this);
                View quantityAlert = getLayoutInflater().inflate(R.layout.adjust_quantity_display, null);
                alertPlus = quantityAlert.findViewById(R.id.alertPlus);
                alertMinus = quantityAlert.findViewById(R.id.alertMinus);
                quantitySetTV = quantityAlert.findViewById(R.id.quantitySetTextView);
                quantitySetTV.setText(FoodMenuDisplayActivity.cartItemQuantity.get(position).toString());
                alertPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Integer.parseInt(quantitySetTV.getText().toString())<20){
                            quantitySetTV.setText(String.valueOf(Integer.valueOf(quantitySetTV.getText().toString())+1));
                        }
                    }
                });
                alertMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Integer.parseInt(quantitySetTV.getText().toString())>0){
                            quantitySetTV.setText(String.valueOf(Integer.valueOf(quantitySetTV.getText().toString())-1));
                        }
                    }
                });

                builder2.setTitle("Select Quantity");
                builder2.setMessage(FoodMenuDisplayActivity.cartItemName.get(position));
                builder2.setView(quantityAlert);

                builder2.setPositiveButton("Adjust Cart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int quant = Integer.valueOf(quantitySetTV.getText().toString());
                        if(quant!=0){
                            FoodMenuDisplayActivity.cartItemQuantity.set(position,quant);
                            cartDisplayAdapter.notifyDataSetChanged();
                            totalPriceTV.setText("Total: Rs. " +String.valueOf(calcTotal()));
                            Toast.makeText(getApplicationContext(),"Cart adjusted",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            AlertDialog.Builder builderConfirm = new AlertDialog.Builder(CartActivity.this);
                            builderConfirm.setTitle("Are you sure you want to remove item?");

                            builderConfirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FoodMenuDisplayActivity.cartItemQuantity.remove(position);
                                    FoodMenuDisplayActivity.cartItemPrice.remove(position);
                                    FoodMenuDisplayActivity.cartItemName.remove(position);
                                    if(FoodMenuDisplayActivity.cartItemName.isEmpty()){
                                        Toast.makeText(getBaseContext(),"Cart is Empty",Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    cartDisplayAdapter.notifyDataSetChanged();
                                    totalPriceTV.setText("Total: Rs. " +String.valueOf(calcTotal()));
                                    Toast.makeText(getApplicationContext(),"Cart adjusted",Toast.LENGTH_SHORT).show();
                                }
                            });

                            builderConfirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                            AlertDialog dialogConfirm = builderConfirm.show();
                        }
                    }
                });

                builder2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog2 = builder2.show();

            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartTransaction();
            }
        });

    }

    private void setDisplayListView() {
        cartDisplayAdapter = new CartDisplayAdapter(FoodMenuDisplayActivity.cartItemName, FoodMenuDisplayActivity.cartItemQuantity,
                FoodMenuDisplayActivity.cartItemPrice, getApplicationContext());
        cartListView.setAdapter(cartDisplayAdapter);
//        calculate total of all the items in cart and display it
        totalPriceTV.setText("Total: Rs. " +String.valueOf(calcTotal()));
    }

    private int calcTotal() {
        int i=0;
        total=0;
        for (Integer price : FoodMenuDisplayActivity.cartItemPrice) {
            total = total + price*FoodMenuDisplayActivity.cartItemQuantity.get(i++);
        }
        return total;
    }

    public void onStartTransaction() {
        PaytmPGService Service = PaytmPGService.getProductionService();
        Map<String, String> paramMap = new HashMap<String, String>();


        // these are mandatory parameters

        paramMap.put("CALLBACK_URL","https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");
        paramMap.put("CHANNEL_ID","WAP");
        paramMap.put("CHECKSUMHASH","7FOTUu30HAAsqZBHRgz5jl1OU7fvAMGK0VWuGiMAOmBKq1L9H07ozWvntxYuTUGddbDyMSXJiaNyLnRfTvNUgK87mq4Vd4gyv+nWr0qZhBc=");
        paramMap.put("CUST_ID","testing12345");
        paramMap.put("INDUSTRY_TYPE_ID","Retail");
        paramMap.put("MID","GetFoo88084336099945");
        paramMap.put("ORDER_ID","11111111");
        paramMap.put("TXN_AMOUNT",String.valueOf(calcTotal()));
        paramMap.put("WEBSITE","GetFood");

/*
        paramMap.put("MID" , "WorldP64425807474247");
        paramMap.put("ORDER_ID" , "210lkldfka2a27");
        paramMap.put("CUST_ID" , "mkjNYC1227");
        paramMap.put("INDUSTRY_TYPE_ID" , "Retail");
        paramMap.put("CHANNEL_ID" , "WAP");
        paramMap.put("TXN_AMOUNT" , "1");
        paramMap.put("WEBSITE" , "worldpressplg");
        paramMap.put("CALLBACK_URL" , "https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp");*/


        PaytmOrder Order = new PaytmOrder(paramMap);

		/*PaytmMerchant Merchant = new PaytmMerchant(
				"https://pguat.paytm.com/paytmchecksum/paytmCheckSumGenerator.jsp",
				"https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp");*/

        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                        Toast.makeText(getApplicationContext(),"UI Error Occured",Toast.LENGTH_SHORT).show();
                    }

					/*@Override
					public void onTransactionSuccess(Bundle inResponse) {
						// After successful transaction this method gets called.
						// // Response bundle contains the merchant response
						// parameters.
						Log.d("LOG", "Payment Transaction is successful " + inResponse);
						Toast.makeText(getApplicationContext(), "Payment Transaction is successful ", Toast.LENGTH_LONG).show();
					}
					@Override
					public void onTransactionFailure(String inErrorMessage,
							Bundle inResponse) {
						// This method gets called if transaction failed. //
						// Here in this case transaction is completed, but with
						// a failure. // Error Message describes the reason for
						// failure. // Response bundle contains the merchant
						// response parameters.
						Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
						Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
					}*/

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.d("LOG", "Payment Transaction is successful " + inResponse);
                        Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                        Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                        Toast.makeText(getApplicationContext(), "Operation failed, try again later", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        Toast.makeText(getApplicationContext(),"Back pressed. Transaction cancelled",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                });
    }

//    private String genereateCheckSum(){
//        TreeMap<String,String> paramMap = new TreeMap<String,String>();
//        paramMap.put("MID" , MID);
//        paramMap.put("ORDER_ID" , "ORDER00011");
//        paramMap.put("CUST_ID" , "CUST00011");
//        paramMap.put("INDUSTRY_TYPE_ID" , INDUSTRY_TYPE_ID);
//        paramMap.put("CHANNEL_ID" , CHANNLE_ID);
//        paramMap.put("TXN_AMOUNT" , String.valueOf(CartActivity.total));
//        paramMap.put("WEBSITE" , WEBSITE);
//        paramMap.put("CALLBACK_URL" , CALLBACK_URL);
//
//        try{
//            checkSum =  CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(MercahntKey, paramMap);
//            paramMap.put("CHECKSUMHASH" , checkSum);
//
//            System.out.println("Paytm Payload: "+ paramMap);
//            return checkSum;
//
//        }catch(Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return null;
//        }
//    }
}
