/*********************************
 * HW #2
 * FileName: MainActivity.java
 *********************************
 * Team Members:
 * Richa Kandlikar
 * Sai Phaninder Reddy Jonnala
 * *******************************
 */
package com.mad.hw2;

import java.text.DecimalFormat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.app.Activity;

public class MainActivity extends Activity {

	Button btn_exit;
	RadioGroup rg;
	EditText et;
	TextView tvCustomPercentageDisplay, tvSavedAmount, tvPayAmount;
	SeekBar sb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//get text views
		tvCustomPercentageDisplay=(TextView)findViewById(R.id.tvCustomPercentageDisplay);
		tvSavedAmount=(TextView)findViewById(R.id.tvSavedAmount);
		tvPayAmount=(TextView)findViewById(R.id.tvPayAmount);
		
		//Application Exit button handler
		btn_exit = (Button)findViewById(R.id.btnExit);
		btn_exit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		//EditText list price change listener
		et=(EditText)findViewById(R.id.etOriginalPrice);
		et.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s){
				handleChange();
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
		
		//Discount% radio change listener
		rg=(RadioGroup)findViewById(R.id.rgDiscountSelector);
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				handleChange();
			}
		});
		
		//Discount% seekbar change listner
		sb=(SeekBar)findViewById(R.id.sbCustomAmount);
		String temp=getString(R.string.initCustomPercentage);
		sb.setProgress( Integer.parseInt(temp.replace("%", "")) );
		sb.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				tvCustomPercentageDisplay.setText(progress+"%");
				
				int checkedId=rg.getCheckedRadioButtonId();
				RadioButton checkedButton = (RadioButton)rg.findViewById(checkedId);
				String checkBoxText = checkedButton.getText().toString();
				
				if(checkBoxText.equalsIgnoreCase("Custom")){
					handleChange();
				}
			}
		});
		sb.setEnabled(false);
		
	}
	
	/**
	 * handleChange is called whenever there is a change in input values including the percentage
	 * selection and custom progress bar changes.
	 */
	public void handleChange(){
		int checkedId=rg.getCheckedRadioButtonId();
		float price = getPriceFromInput();
		float discount=0.0f;
		
		if(checkedId!=-1){
			RadioButton checkedButton = (RadioButton)rg.findViewById(checkedId);
			String checkBoxText = checkedButton.getText().toString().replace("%", "");
			
			if(checkBoxText.equalsIgnoreCase("Custom")){
				sb.setEnabled(true);
				discount = sb.getProgress();
			}else{
				sb.setEnabled(false);
				discount = Float.valueOf(checkBoxText);
			}
		}
		recalculateDiscountValues(price,discount);
	}
	
	/**
	 * recalculates the discount
	 * @param price
	 * @param discount
	 */
	public void recalculateDiscountValues(float price, float discount){
		float saved, discountPrice, savedRounded;
		if(discount>100.0f){
			discount=100.0f;
		}
		if(discount<0.0f){
			discount=0.0f;
		}
		
		saved= (( price / 100.0f ) * discount) ;
		DecimalFormat df=new DecimalFormat("0.00");
		savedRounded= Float.parseFloat(df.format(saved));
		Log.d("DISC",saved+" : "+savedRounded);
		discountPrice = price - savedRounded;
		
		setValues(saved,discountPrice);
	}

	/**
	 * sets the values of saved amount and to pay amount
	 * @param saved
	 * @param discountPrice
	 */
	public void setValues(float saved, float discountPrice){
		tvSavedAmount.setText(formatMoney(saved));
		tvPayAmount.setText(formatMoney(discountPrice));
	}
	
	/**
	 * formats the float value to resemble $0.00
	 * @param float value
	 * @return string "$0.00"
	 */
	public String formatMoney(float value){
		return String.format("$ %.2f", value);
	}
	
	/**
	 * gets the price from the EditText input field
	 * @return float
	 */
	public float getPriceFromInput(){
		String priceText = et.getText().toString();
		if(priceText.isEmpty() ){
			et.setError("Enter List Price");
			return 0.0f;
		}
		else{
			float input = Float.valueOf(priceText);
			DecimalFormat df=new DecimalFormat("0.00");
			/* 
			// causing performance drop.
			String[] temp = Float.toString(input).split("\\.");
			if(temp.length==2){
				int decimalCount= temp[1].length();
				if(decimalCount>2){
					et.setError("Can have only 2 decimal places .00");
				}
				else{
					et.setError(null);
				}
			}//*/
			return Float.parseFloat(df.format(input));
		}
	}
	
}
