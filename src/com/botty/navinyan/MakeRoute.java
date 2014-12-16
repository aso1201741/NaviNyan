package com.botty.navinyan;

import java.util.Calendar;

import com.botty.navinyan.R.id;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.CompoundButton;

public class MakeRoute extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
	
	WebView wb;
	TimePickerDialog tpd;
	TimePickerDialog.OnTimeSetListener tsl1,tsl2;
	
	int[] layoutlist = new int[11];
	int[] pluslist = new int[10];
	int[] pbtnlist = new int[10];
	int[] dbtnlist = new int[11];
	int[] tbtnlist = new int[11];
	int[] etlist = new int[11];
	
	int numpos = 0;
	int numtime = 0;
	NumberPicker np;
	
	String[] latlnglist = new String[11];
	int[] dislist = new int[10];
	int[] durlist = new int[10];
	
	int c = 2; //表示されている地点の数を格納
	int countadd = 0;
	int countdel = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_makeroute);
		
		wb = (WebView)findViewById(R.id.makeroute_wv);
		wb.getSettings().setJavaScriptEnabled(true);
		wb.loadUrl("file:///android_asset/calctime.html");
		wb.setWebViewClient(new WebViewClient(){
			public boolean shouldOverrideUrlLoading(WebView wv, String str){
				String url = str;
				if(! url.startsWith("navinyan://")){  //アプリに対するコールバックかの確認
					return (true);
				}
				
				if(url.startsWith("navinyan://alert/")){
					Toast.makeText(getApplicationContext(), "Alert!!" + url.substring(17), Toast.LENGTH_SHORT).show();
				}
				
				if(url.startsWith("navinyan://calctime/")){  //該当するコールバックに対する処理
					String dis = url.substring(24);
					int d = dis.indexOf("dur:");
					String dur = dis.substring(d + 4);
					dis = dis.substring(0,d);
					
					System.out.println(dis);
					System.out.println(dur);
					
					int i = 0;
					int disres = 0;
					int durres = 0;
					while(i < 9){
						int dist1 = dis.indexOf("s[" + String.valueOf(i) + "]:");
						int dist2 = dis.indexOf("s[" + String.valueOf(i + 1) + "]:");
						if(dist1 == -1 && dist2 == -1){
							break;
						}else if(dist1 != -1 && dist2 == -1){
							dist2 = dis.indexOf(",,,,,");
							disres += Integer.parseInt(dis.substring(dist1 + 5,dist2));
							((TextView)findViewById(dislist[i + 1])).setText(String.valueOf((Double.parseDouble(dis.substring(dist1 + 5,dist2))/1000)) + "km");
						}else{
							disres += Integer.parseInt(dis.substring(dist1 + 5,dist2));
							((TextView)findViewById(dislist[i + 1])).setText(String.valueOf((Double.parseDouble(dis.substring(dist1 + 5,dist2))/1000)) + "km");
						
						}
						i++;
					}
					i = 0;
					while(i < 9){
						int dure1 = dur.indexOf("r[" + String.valueOf(i) + "]:");
						int dure2 = dur.indexOf("r[" + String.valueOf(i + 1) + "]:");
						if(dure1 == -1 && dure2 == -1){
							break;
						}else if(dure1 != -1 && dure2 == -1){
							dure2 = dur.indexOf(",,,,,");
							durres += Integer.parseInt(dur.substring(dure1 + 5,dure2));
							((TextView)findViewById(durlist[i + 1])).setText(String.valueOf(Integer.parseInt(dur.substring(dure1 + 5, dure2))/3600) + "時間" + String.valueOf(Integer.parseInt(dur.substring(dure1 + 5, dure2)) % 3600 / 60) + "分");
						}else{
							durres += Integer.parseInt(dur.substring(dure1 + 5,dure2));
							((TextView)findViewById(durlist[i + 1])).setText(String.valueOf(Integer.parseInt(dur.substring(dure1 + 5, dure2))/3600) + "時間" + String.valueOf(Integer.parseInt(dur.substring(dure1 + 5, dure2)) % 3600 / 60) + "分");
						
						}
						i++;
					}
					TextView tv = (TextView)findViewById(R.id.makeroute_result);
					durres += calcTime() * 60; //分単位のcalcTime()を秒単位に直してdurresに追加
					tv.setText("運転距離：" + String.valueOf(disres / 1000) + "km" + " 所要時間：" + String.valueOf(durres/3600) + "時間" + String.valueOf(durres % 3600 / 60) + "分");
					String dptime1;
					if(((Switch)findViewById(R.id.makeroute_switch1)).isChecked()){//到着時間指定のときの処理
						String arr = ((Button)findViewById(R.id.makeroute_timeset_btn2)).getText().toString();
						int arrive = Integer.parseInt(arr.substring(0,2)) * 60 + Integer.parseInt(arr.substring(3,5));
						int arriveh = 0;
						int arrivem = 0;
						if(arrive - durres / 60 < 0){//出発時間が0時を超えて前日に到達する場合
							arriveh = (1440 + arrive - durres / 60) / 60;
							arrivem = (1440 + arrive - durres / 60) % 60;
						}else{//それ以外
							arriveh = (arrive - durres / 60) / 60;
							arrivem = (arrive - durres / 60) % 60;
						}
						
						if(arriveh < 10){
							dptime1 = "0" + String.valueOf(arriveh);
						}else{
							dptime1 = String.valueOf(arriveh);
						}
						if(arrivem < 10){
							dptime1 = dptime1 + ":0" + String.valueOf(arrivem);
						}else{
							dptime1 = dptime1 + ":" + String.valueOf(arrivem);
						}
						((Button)findViewById(R.id.makeroute_timeset_btn1)).setText(dptime1);
					}else{ //出発時間指定のときの処理
						String dep = ((Button)findViewById(R.id.makeroute_timeset_btn1)).getText().toString();
						int depart = Integer.parseInt(dep.substring(0,2)) * 60 + Integer.parseInt(dep.substring(3,5));
						int departh = 0;
						int departm = 0;
						if(depart + durres / 60 >= 1440){//出発時間が0時を超えて前日に到達する場合
							departh = (depart + durres / 60 - 1440) / 60;
							departm = (depart + durres / 60 - 1440) % 60;
						}else{//それ以外
							departh = (depart + durres / 60) / 60;
							departm = (depart + durres / 60) % 60;
						}
						
						if(departh < 10){
							dptime1 = "0" + String.valueOf(departh);
						}else{
							dptime1 = String.valueOf(departh);
						}
						if(departm < 10){
							dptime1 = dptime1 + ":0" + String.valueOf(departm);
						}else{
							dptime1 = dptime1 + ":" + String.valueOf(departm);
						}
						((Button)findViewById(R.id.makeroute_timeset_btn2)).setText(dptime1);
					}
					/*
					TextView tv1 = (TextView)findViewById(R.id.calctime_time);
					TextView tv2 = (TextView)findViewById(R.id.calctime_trip);
					tv1.setText(dur);
					if(Double.parseDouble(dur) >= 60){
						int hour = (int)(Double.parseDouble(dur) / 60);
						int minute = (int)(Double.parseDouble(dur) % 60);
						tv1.setText(String.valueOf(hour) + "時間" + String.valueOf(minute) + "分");
					}else{
						int minute = (int)(Double.parseDouble(dur));
						tv1.setText(String.valueOf(minute) + "分");
					}
					
					tv2.setText(dis + "km");
					*/
				}
				return (true);
			}
		});
		
		//出発・到着時間指定ボタン
		findViewById(R.id.makeroute_timeset_btn1).setOnClickListener(this);
		findViewById(R.id.makeroute_timeset_btn2).setOnClickListener(this);
		String h;
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if(hour < 10){
			h = "0" + String.valueOf(hour); 
		}else{
			h = String.valueOf(hour);
		}
		((Button)findViewById(R.id.makeroute_timeset_btn1)).setText(h + ":00");
		((Button)findViewById(R.id.makeroute_timeset_btn2)).setText(h + ":00");
		((Button)findViewById(R.id.makeroute_timeset_btn2)).setClickable(false);
		
		((Switch)findViewById(R.id.makeroute_switch1)).setOnCheckedChangeListener(this);
		((Switch)findViewById(R.id.makeroute_switch2)).setOnCheckedChangeListener(this);
		tsl1 = new TimePickerDialog.OnTimeSetListener(){
			public void onTimeSet(TimePicker view, int hourOfDay, int minute){
				String h,m;
				if(hourOfDay < 10){
					h = "0" + String.valueOf(hourOfDay);
				}else{
					h = String.valueOf(hourOfDay);
				}
				if(minute < 10){
					m = "0" + String.valueOf(minute);
				}else{
					m = String.valueOf(minute);
				}
				((Button)findViewById(R.id.makeroute_timeset_btn1)).setText(h + ":" + m);
			}
		};
		tsl2 = new TimePickerDialog.OnTimeSetListener(){
			public void onTimeSet(TimePicker view, int hourOfDay, int minute){
				String h,m;
				if(hourOfDay < 10){
					h = "0" + String.valueOf(hourOfDay);
				}else{
					h = String.valueOf(hourOfDay);
				}
				if(minute < 10){
					m = "0" + String.valueOf(minute);
				}else{
					m = String.valueOf(minute);
				}
				((Button)findViewById(R.id.makeroute_timeset_btn2)).setText(h + ":" + m);
			}
		};
		
		layoutlist[1] = R.id.makeroute_layout1;
		layoutlist[2] = R.id.makeroute_layout2;
		layoutlist[3] = R.id.makeroute_layout3;
		layoutlist[4] = R.id.makeroute_layout4;
		layoutlist[5] = R.id.makeroute_layout5;
		layoutlist[6] = R.id.makeroute_layout6;
		layoutlist[7] = R.id.makeroute_layout7;
		layoutlist[8] = R.id.makeroute_layout8;
		layoutlist[9] = R.id.makeroute_layout9;
		layoutlist[10] = R.id.makeroute_layout10;
		
		pluslist[1] = R.id.makeroute_p_lay1;
		pluslist[2] = R.id.makeroute_p_lay2;
		pluslist[3] = R.id.makeroute_p_lay3;
		pluslist[4] = R.id.makeroute_p_lay4;
		pluslist[5] = R.id.makeroute_p_lay5;
		pluslist[6] = R.id.makeroute_p_lay6;
		pluslist[7] = R.id.makeroute_p_lay7;
		pluslist[8] = R.id.makeroute_p_lay8;
		pluslist[9] = R.id.makeroute_p_lay9;
		
		pbtnlist[1] = R.id.makeroute_p_btn1;
		pbtnlist[2] = R.id.makeroute_p_btn2;
		pbtnlist[3] = R.id.makeroute_p_btn3;
		pbtnlist[4] = R.id.makeroute_p_btn4;
		pbtnlist[5] = R.id.makeroute_p_btn5;
		pbtnlist[6] = R.id.makeroute_p_btn6;
		pbtnlist[7] = R.id.makeroute_p_btn7;
		pbtnlist[8] = R.id.makeroute_p_btn8;
		
		dbtnlist[1] = R.id.makeroute_delbtn1;
		dbtnlist[2] = R.id.makeroute_delbtn2;
		dbtnlist[3] = R.id.makeroute_delbtn3;
		dbtnlist[4] = R.id.makeroute_delbtn4;
		dbtnlist[5] = R.id.makeroute_delbtn5;
		dbtnlist[6] = R.id.makeroute_delbtn6;
		dbtnlist[7] = R.id.makeroute_delbtn7;
		dbtnlist[8] = R.id.makeroute_delbtn8;
		dbtnlist[9] = R.id.makeroute_delbtn9;
		dbtnlist[10] = R.id.makeroute_delbtn10;
		
		etlist[1] = R.id.makeroute_et1;
		etlist[2] = R.id.makeroute_et2;
		etlist[3] = R.id.makeroute_et3;
		etlist[4] = R.id.makeroute_et4;
		etlist[5] = R.id.makeroute_et5;
		etlist[6] = R.id.makeroute_et6;
		etlist[7] = R.id.makeroute_et7;
		etlist[8] = R.id.makeroute_et8;
		etlist[9] = R.id.makeroute_et9;
		etlist[10] = R.id.makeroute_et10;
		
		tbtnlist[1] = R.id.makeroute_time1;
		tbtnlist[2] = R.id.makeroute_time2;
		tbtnlist[3] = R.id.makeroute_time3;
		tbtnlist[4] = R.id.makeroute_time4;
		tbtnlist[5] = R.id.makeroute_time5;
		tbtnlist[6] = R.id.makeroute_time6;
		tbtnlist[7] = R.id.makeroute_time7;
		tbtnlist[8] = R.id.makeroute_time8;
		tbtnlist[9] = R.id.makeroute_time9;
		tbtnlist[10] = R.id.makeroute_time10;
		
		dislist[1] = R.id.makeroute_p_dist1;
		dislist[2] = R.id.makeroute_p_dist2;
		dislist[3] = R.id.makeroute_p_dist3;
		dislist[4] = R.id.makeroute_p_dist4;
		dislist[5] = R.id.makeroute_p_dist5;
		dislist[6] = R.id.makeroute_p_dist6;
		dislist[7] = R.id.makeroute_p_dist7;
		dislist[8] = R.id.makeroute_p_dist8;
		dislist[9] = R.id.makeroute_p_dist9;
		
		durlist[1] = R.id.makeroute_p_time1;
		durlist[2] = R.id.makeroute_p_time2;
		durlist[3] = R.id.makeroute_p_time3;
		durlist[4] = R.id.makeroute_p_time4;
		durlist[5] = R.id.makeroute_p_time5;
		durlist[6] = R.id.makeroute_p_time6;
		durlist[7] = R.id.makeroute_p_time7;
		durlist[8] = R.id.makeroute_p_time8;
		durlist[9] = R.id.makeroute_p_time9;
		
		
		
		
		for(int i = 1; i <= 8; i++){
			findViewById(pbtnlist[i]).setOnClickListener(this);
		}
		
		for(int i = 1; i <= 10; i++){
			findViewById(dbtnlist[i]).setOnClickListener(this);
		}
		
		for(int i = 1; i <= 10; i++){
			findViewById(etlist[i]).setOnClickListener(this);
		}
		
		for(int i = 1; i <= 10; i++){
			findViewById(tbtnlist[i]).setOnClickListener(this);
			((TextView)findViewById(tbtnlist[i])).setText("20分");
		}
		
		initialize();
	}
	
	private void initialize(){
		for(int i = 3; i <= 10; i++){
			findViewById(layoutlist[i]).setVisibility(View.GONE);
		}
		for(int i = 2; i <= 9; i++){
			findViewById(pluslist[i]).setVisibility(View.GONE);
		}
	}

	private int calcTime(){
		int countTime = 0;
		for(int i = 1; i <= c; i++){
			String s = ((TextView)findViewById(tbtnlist[i])).getText().toString();
			countTime += Integer.parseInt(s.substring(0, s.length() - 1));
		}
		return countTime;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		//PlusボタンのClick時処理
		case R.id.makeroute_p_btn1:
		case R.id.makeroute_p_btn2:
		case R.id.makeroute_p_btn3:
		case R.id.makeroute_p_btn4:
		case R.id.makeroute_p_btn5:
		case R.id.makeroute_p_btn6:
		case R.id.makeroute_p_btn7:
		case R.id.makeroute_p_btn8:
			countadd = 1;
			while (countadd <= 8){ //countに何番目のPlusボタンが押されたか格納
				if(v.getId() == pbtnlist[countadd]){
					break;
				}
				countadd++;
			}
			
			findViewById(layoutlist[c + 1]).setVisibility(View.VISIBLE);
			findViewById(pluslist[c]).setVisibility(View.VISIBLE);
			
			for(int i = c; i > countadd; i--){
				((Button)findViewById(etlist[i + 1])).setText(((Button)findViewById(etlist[i])).getText());
				latlnglist[i + 1] = latlnglist[i];
			}
			((Button)findViewById(etlist[countadd + 1])).setText("");
			latlnglist[countadd + 1] = "";
			
			if(c == 9){
				for(int i = 1; i <= 8; i++){
					findViewById(pbtnlist[i]).setVisibility(View.INVISIBLE);
				}
			}
			c++;
			
			break;

		case R.id.makeroute_delbtn1:
		case R.id.makeroute_delbtn2:
		case R.id.makeroute_delbtn3:
		case R.id.makeroute_delbtn4:
		case R.id.makeroute_delbtn5:
		case R.id.makeroute_delbtn6:
		case R.id.makeroute_delbtn7:
		case R.id.makeroute_delbtn8:
		case R.id.makeroute_delbtn9:
		case R.id.makeroute_delbtn10:
			System.out.println("DelButton Pushed.");
			countdel = 1;
			while(countdel <= 10){ //何番目の削除ボタンが押されたか格納
				if(v.getId() == dbtnlist[countdel]){
					break;
				}
				countdel++;
			}
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			if(countdel == 1 || countdel == c){
				adb.setTitle("クリア");
				adb.setMessage("項目の内容をクリアします");
				adb.setPositiveButton("クリア",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						((Button)findViewById(etlist[countdel])).setText("");
						latlnglist[countdel] = "";
					}
				});
				adb.setNegativeButton("キャンセル",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
				});
			}else{
				adb.setTitle("削除");
				adb.setMessage("経由地を削除します");
				adb.setPositiveButton("削除",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						((Button)findViewById(etlist[countdel])).setText("");
						latlnglist[countdel] = "";
						for(int i = countdel; i < c; i++){
							((Button)findViewById(etlist[i])).setText(((Button)findViewById(etlist[i + 1])).getText());
							latlnglist[i] = latlnglist[i + 1];
						}
						((Button)findViewById(etlist[c])).setText("");
						latlnglist[c] = "";
						findViewById(layoutlist[c]).setVisibility(View.GONE);
						findViewById(pluslist[c - 1]).setVisibility(View.GONE);
						
						if(c == 10){
							for(int i = 1; i <= 8; i++){
								findViewById(pbtnlist[i]).setVisibility(View.VISIBLE);
							}
						}
						c--;
					}
				});
				adb.setNegativeButton("キャンセル",new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
				});
			}
			adb.create().show();
			break;
		case R.id.makeroute_et1:
		case R.id.makeroute_et2:
		case R.id.makeroute_et3:
		case R.id.makeroute_et4:
		case R.id.makeroute_et5:
		case R.id.makeroute_et6:
		case R.id.makeroute_et7:
		case R.id.makeroute_et8:
		case R.id.makeroute_et9:
		case R.id.makeroute_et10:
			int countet = 1;
			while(countet <= 10){
				if(v.getId() == etlist[countet]){
					break;
				}
				countet++;
			}
			Intent vIntent = new Intent(this, MakePoint.class);
			vIntent.putExtra("et", countet);
			startActivityForResult(vIntent,1);
			break;
			
		case R.id.makeroute_time1:
		case R.id.makeroute_time2:
		case R.id.makeroute_time3:
		case R.id.makeroute_time4:
		case R.id.makeroute_time5:
		case R.id.makeroute_time6:
		case R.id.makeroute_time7:
		case R.id.makeroute_time8:
		case R.id.makeroute_time9:
		case R.id.makeroute_time10:
			int i = 1;
			while(i <= 10){
				if(v.getId() == tbtnlist[i]){
					break;
				}
				i++;
			}
			numpos = i;
			showDialog();
			break;
		case R.id.makeroute_timeset_btn1:
			String hm1 = ((Button)findViewById(R.id.makeroute_timeset_btn1)).getText().toString();
			tpd = new TimePickerDialog(this, tsl1, Integer.parseInt(hm1.substring(0,2)), Integer.parseInt(hm1.substring(3,5)),true);
			tpd.show();
			break;
		case R.id.makeroute_timeset_btn2:
			String hm2 = ((Button)findViewById(R.id.makeroute_timeset_btn2)).getText().toString();
			tpd = new TimePickerDialog(this, tsl2, Integer.parseInt(hm2.substring(0,2)), Integer.parseInt(hm2.substring(3,5)),true);
			tpd.show();
			break;
		}
		
		
	}
	private void showDialog() {
        class MainFragmentDialog extends DialogFragment {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog, null, false);

                np = (NumberPicker) view.findViewById(R.id.numberPicker);
                
                np.setMaxValue(120);
                np.setMinValue(1);
                

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Number Picker");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // OKクリック時の処理
                    	((Button)findViewById(tbtnlist[numpos])).setText(np.getValue() + "分");
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.setView(view);
                return builder.create();
            }
        }

        // Dialogの表示
        MainFragmentDialog dialog = new MainFragmentDialog();
        dialog.show(getFragmentManager(), "span_setting_dialog");
    }

	
	
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.makeroute_switch1:
			if(((Switch)findViewById(R.id.makeroute_switch1)).isChecked()){
				((Switch)findViewById(R.id.makeroute_switch2)).setChecked(true);
				((Button)findViewById(R.id.makeroute_timeset_btn1)).setClickable(false);
				((Button)findViewById(R.id.makeroute_timeset_btn2)).setClickable(true);				
			}else{
				((Switch)findViewById(R.id.makeroute_switch2)).setChecked(false);
				((Button)findViewById(R.id.makeroute_timeset_btn1)).setClickable(true);
				((Button)findViewById(R.id.makeroute_timeset_btn2)).setClickable(false);
			}
			break;
		case R.id.makeroute_switch2:
			if(((Switch)findViewById(R.id.makeroute_switch2)).isChecked()){
				((Switch)findViewById(R.id.makeroute_switch1)).setChecked(true);
				((Button)findViewById(R.id.makeroute_timeset_btn1)).setClickable(false);
				((Button)findViewById(R.id.makeroute_timeset_btn2)).setClickable(true);	
			}else{
				((Switch)findViewById(R.id.makeroute_switch1)).setChecked(false);
				((Button)findViewById(R.id.makeroute_timeset_btn1)).setClickable(true);
				((Button)findViewById(R.id.makeroute_timeset_btn2)).setClickable(false);
			}
			break;
		}
	}

	@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			Bundle extras = data.getExtras();
			if(extras != null){
				if(resultCode == RESULT_OK){
					System.out.println(extras.getString("LatLng"));
					((Button)findViewById(etlist[extras.getInt("ReturnTid")])).setText(extras.getCharSequence("PlaceName"));//extras.getCharSequence("PlaceName")
					latlnglist[extras.getInt("ReturnTid")] = extras.getString("LatLng");
				}
			}
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.makeroute_menu,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int itemid = item.getItemId();
		if(itemid == R.id.makeroute_menu_refresh){
			if(!((Button)findViewById(etlist[1])).getText().toString().equals("")&&!((Button)findViewById(etlist[c])).getText().toString().equals("")){
				//経路処理
				String str = "javascript:dispRoute('1','1','" + c + "','" +
				latlnglist[1] + "','" +
				latlnglist[2] + "','" +
				latlnglist[3] + "','" +
				latlnglist[4] + "','" +
				latlnglist[5] + "','" +
				latlnglist[6] + "','" +
				latlnglist[7] + "','" +
				latlnglist[8] + "','" +
				latlnglist[9] + "','" +
				latlnglist[10] + "');";
				System.out.println(str);
				wb.loadUrl(str);
				
				
			}else if(((Button)findViewById(etlist[1])).getText().toString().equals("")&&!((Button)findViewById(etlist[c])).getText().toString().equals("")){
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setTitle("出発地がありません");
				adb.setMessage("出発地を指定してください");
				adb.setPositiveButton("OK", null);
				adb.create().show();
			}else if(!((Button)findViewById(etlist[1])).getText().toString().equals("")&&((Button)findViewById(etlist[c])).getText().toString().equals("")){
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setTitle("目的地がありません");
				adb.setMessage("目的地を指定してください");
				adb.setPositiveButton("OK", null);
				adb.create().show();
			}else{
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setTitle("出発地と目的地がありません");
				adb.setMessage("出発地と目的地を指定してください");
				adb.setPositiveButton("OK", null);
				adb.create().show();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
}
