package com.botty.navinyan;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MakePoint extends Activity implements View.OnClickListener{

	int et = 0;
	WebView wb;
	int flg = 0;
	String latlng;
	ArrayList<Integer> sidlist = new ArrayList<Integer>();
	ArrayList<Integer> nidlist = new ArrayList<Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_makepoint);
		findViewById(R.id.makepoint_btn1).setOnClickListener(this);
		findViewById(R.id.makepoint_btn2).setOnClickListener(this);
		et = getIntent().getIntExtra("et", -1);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		ListView lv;
		ArrayList<String> lblist;
		DBHelper dbh;
		android.database.sqlite.SQLiteDatabase db;
		Cursor cr;
		boolean eof;
		ViewAdapter va;
		
		
		switch(arg0.getId()){
		case R.id.makepoint_btn1: //【ジャンル】ボタン
			setContentView(R.layout.activity_genre_l);
			findViewById(R.id.genre_l_conv).setOnClickListener(this);
			findViewById(R.id.genre_l_rest).setOnClickListener(this);
			findViewById(R.id.genre_l_leis).setOnClickListener(this);
			findViewById(R.id.genre_l_amus).setOnClickListener(this);
			findViewById(R.id.genre_l_buil).setOnClickListener(this);
			/*
			DBHelper dbh = new DBHelper(this);
			android.database.sqlite.SQLiteDatabase db = dbh.getReadableDatabase();
			Cursor cr = db.rawQuery("SELECT t_id,t_name FROM typeDB",null);
			db.close();
			dbh.close();
			*/
			
			//setContentView(R.layout.activity_makeroute);
			Intent vIntent1 = new Intent();
			vIntent1.putExtra("ReturnTid", et);
			setResult(RESULT_OK,vIntent1);
			break;
		case R.id.makepoint_btn2: //【地図】ボタン
			flg = 1;
			setContentView(R.layout.activity_searchplace);
			findViewById(R.id.searchplace_btn).setOnClickListener(this);
			wb = (WebView)findViewById(R.id.searchplace_wv);
			wb.getSettings().setJavaScriptEnabled(true);
			wb.loadUrl("file:///android_asset/searchplace.html");
			
			wb.setWebViewClient(new WebViewClient(){
				public boolean shouldOverrideUrlLoading(WebView wv, String str){
					String url = str;
					if(! url.startsWith("navinyan://")){  //アプリに対するコールバックかの確認
						return (true);
					}
					
					if(url.startsWith("navinyan://getplace/")){  //該当するコールバックに対する処理
						System.out.println(url.substring(20));
						Intent vIntent2 = new Intent();
						vIntent2.putExtra("ReturnTid", et);
						vIntent2.putExtra("PlaceName", ((EditText)findViewById(R.id.searchplace_et)).getText());
						vIntent2.putExtra("LatLng", url.substring(20));
						setResult(RESULT_OK,vIntent2);
						finish();
					}
					return (true);
				}
			});
			break;
			
		case R.id.searchplace_btn: //【地図】検索ボタン
			System.out.println("Pushed search button.");
			wb.loadUrl("javascript:setPlace('" + ((EditText)findViewById(R.id.searchplace_et)).getText() + "');");
			break;
			
			
		case R.id.genre_l_conv:
			setContentView(R.layout.activity_listview);
			lv = (ListView)findViewById(R.id.listView);
			lblist = new ArrayList<String>();
			
			dbh = new DBHelper(this);
			db = dbh.getReadableDatabase();
			cr = db.rawQuery("SELECT t_id,t_name FROM typeDB",null);
			eof = cr.moveToFirst();
			while(eof){
				sidlist.add(cr.getInt(0));
				lblist.add(cr.getString(1));
				eof = cr.moveToNext();
			}
			db.close();
			dbh.close();
			
			va = new ViewAdapter(this, 0, lblist, 1);
			lv.setAdapter(va);
			lv.setDivider(null);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
	            public void onItemClick(AdapterView<?> parent, View view,
	                    int position, long id) {
					setContentView(R.layout.activity_listview);
					ListView lv = (ListView)findViewById(R.id.listView);
					ArrayList<String> lblist = new ArrayList<String>();
					
					DBHelper dbh = new DBHelper(getApplicationContext());
					android.database.sqlite.SQLiteDatabase db = dbh.getReadableDatabase();
					Cursor cr = db.rawQuery("select m.name, m.n_id from stDB s left join mapsDB m on s.n_id = m.n_id where s.s_id = '" + sidlist.get(position) + "'",null);
					boolean eof = cr.moveToFirst();
					while(eof){
						lblist.add(cr.getString(0));
						nidlist.add(cr.getInt(1));
						eof = cr.moveToNext();
					}
					db.close();
					dbh.close();
					
					ViewAdapter va = new ViewAdapter(getApplicationContext(), 0, lblist, 1);
					lv.setAdapter(va);
					lv.setDivider(null);
					lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
			            public void onItemClick(AdapterView<?> parent, View view,
			                    int position, long id) {
							setContentView(R.layout.activity_detail);
							flg = 2;
							DBHelper dbh = new DBHelper(getApplicationContext());
							android.database.sqlite.SQLiteDatabase db = dbh.getReadableDatabase();
							Cursor cr = db.rawQuery("select name,address,tel,time,parking,lat,log from MapsDB where n_id = '" + nidlist.get(position) + "'",null);
							cr.moveToFirst();
							((TextView)findViewById(R.id.detail_name)).setText(cr.getString(0));
							((TextView)findViewById(R.id.detail_address)).setText("住所：" + cr.getString(1));
							((TextView)findViewById(R.id.detail_tel)).setText("Tel:0" + cr.getInt(2));
							((TextView)findViewById(R.id.detail_time)).setText("営業時間：" + cr.getString(3));
							((TextView)findViewById(R.id.detail_parking)).setText("駐車場：" + cr.getString(4));
							latlng = String.valueOf(cr.getDouble(5)) + "," + String.valueOf(cr.getDouble(6));
							
							db.close();
							dbh.close();
						}
					});
	            }

			});
			break;
		case R.id.genre_l_rest:
			setContentView(R.layout.activity_listview);
			lv = (ListView)findViewById(R.id.listView);
			lblist = new ArrayList<String>();
			
			dbh = new DBHelper(this);
			db = dbh.getReadableDatabase();
			cr = db.rawQuery("select s_id, s_name from smalltypeDB where t_id = '2'",null);
			eof = cr.moveToFirst();
			while(eof){
				sidlist.add(cr.getInt(0));
				lblist.add(cr.getString(1));
				eof = cr.moveToNext();
			}
			db.close();
			dbh.close();
			
			va = new ViewAdapter(this, 0, lblist, 2);
			lv.setAdapter(va);
			lv.setDivider(null);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
	            public void onItemClick(AdapterView<?> parent, View view,
	                    int position, long id) {
					setContentView(R.layout.activity_listview);
					ListView lv = (ListView)findViewById(R.id.listView);
					ArrayList<String> lblist = new ArrayList<String>();
					
					DBHelper dbh = new DBHelper(getApplicationContext());
					android.database.sqlite.SQLiteDatabase db = dbh.getReadableDatabase();
					Cursor cr = db.rawQuery("select m.name, m.n_id from stDB s left join mapsDB m on s.n_id = m.n_id where s.s_id = '" + sidlist.get(position) + "'",null);
					boolean eof = cr.moveToFirst();
					while(eof){
						lblist.add(cr.getString(0));
						nidlist.add(cr.getInt(1));
						eof = cr.moveToNext();
					}
					db.close();
					dbh.close();
					
					ViewAdapter va = new ViewAdapter(getApplicationContext(), 0, lblist, 2);
					lv.setAdapter(va);
					lv.setDivider(null);
					lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
			            public void onItemClick(AdapterView<?> parent, View view,
			                    int position, long id) {
							setContentView(R.layout.activity_detail);
							flg = 2;
							DBHelper dbh = new DBHelper(getApplicationContext());
							android.database.sqlite.SQLiteDatabase db = dbh.getReadableDatabase();
							Cursor cr = db.rawQuery("select name,address,tel,time,parking,lat,log from MapsDB where n_id = '" + nidlist.get(position) + "'",null);
							cr.moveToFirst();
							((TextView)findViewById(R.id.detail_name)).setText(cr.getString(0));
							((TextView)findViewById(R.id.detail_address)).setText("住所：" + cr.getString(1));
							((TextView)findViewById(R.id.detail_tel)).setText("Tel:0" + cr.getInt(2));
							((TextView)findViewById(R.id.detail_time)).setText("営業時間：" + cr.getString(3));
							((TextView)findViewById(R.id.detail_parking)).setText("駐車場：" + cr.getString(4));
							latlng = String.valueOf(cr.getDouble(5)) + "," + String.valueOf(cr.getDouble(6));
							
							db.close();
							dbh.close();
						}
					});
	            }

			});
			break;
		case R.id.genre_l_leis:
			setContentView(R.layout.activity_listview);
			lv = (ListView)findViewById(R.id.listView);
			lblist = new ArrayList<String>();
			
			dbh = new DBHelper(this);
			db = dbh.getReadableDatabase();
			cr = db.rawQuery("select s_id, s_name from smalltypeDB where t_id = '3'",null);
			eof = cr.moveToFirst();
			while(eof){
				sidlist.add(cr.getInt(0));
				lblist.add(cr.getString(1));
				eof = cr.moveToNext();
			}
			db.close();
			dbh.close();
			
			va = new ViewAdapter(this, 0, lblist, 3);
			lv.setAdapter(va);
			lv.setDivider(null);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
	            public void onItemClick(AdapterView<?> parent, View view,
	                    int position, long id) {
					setContentView(R.layout.activity_listview);
					ListView lv = (ListView)findViewById(R.id.listView);
					ArrayList<String> lblist = new ArrayList<String>();
					
					DBHelper dbh = new DBHelper(getApplicationContext());
					android.database.sqlite.SQLiteDatabase db = dbh.getReadableDatabase();
					Cursor cr = db.rawQuery("select m.name, m.n_id from stDB s left join mapsDB m on s.n_id = m.n_id where s.s_id = '" + sidlist.get(position) + "'",null);
					boolean eof = cr.moveToFirst();
					while(eof){
						lblist.add(cr.getString(0));
						nidlist.add(cr.getInt(1));
						eof = cr.moveToNext();
					}
					db.close();
					dbh.close();
					
					ViewAdapter va = new ViewAdapter(getApplicationContext(), 0, lblist, 3);
					lv.setAdapter(va);
					lv.setDivider(null);
					lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
			            public void onItemClick(AdapterView<?> parent, View view,
			                    int position, long id) {
							setContentView(R.layout.activity_detail);
							flg = 2;
							DBHelper dbh = new DBHelper(getApplicationContext());
							android.database.sqlite.SQLiteDatabase db = dbh.getReadableDatabase();
							Cursor cr = db.rawQuery("select name,address,tel,time,parking,lat,log from MapsDB where n_id = '" + nidlist.get(position) + "'",null);
							cr.moveToFirst();
							((TextView)findViewById(R.id.detail_name)).setText(cr.getString(0));
							((TextView)findViewById(R.id.detail_address)).setText("住所：" + cr.getString(1));
							((TextView)findViewById(R.id.detail_tel)).setText("Tel:0" + cr.getInt(2));
							((TextView)findViewById(R.id.detail_time)).setText("営業時間：" + cr.getString(3));
							((TextView)findViewById(R.id.detail_parking)).setText("駐車場：" + cr.getString(4));
							latlng = String.valueOf(cr.getDouble(5)) + "," + String.valueOf(cr.getDouble(6));
							
							db.close();
							dbh.close();
						}
					});
	            }

			});
			break;
		case R.id.genre_l_amus:
			setContentView(R.layout.activity_listview);
			lv = (ListView)findViewById(R.id.listView);
			lblist = new ArrayList<String>();
			
			dbh = new DBHelper(this);
			db = dbh.getReadableDatabase();
			cr = db.rawQuery("select s_id, s_name from smalltypeDB where t_id = '4'",null);
			eof = cr.moveToFirst();
			while(eof){
				sidlist.add(cr.getInt(0));
				lblist.add(cr.getString(1));
				eof = cr.moveToNext();
			}
			db.close();
			dbh.close();
			
			va = new ViewAdapter(this, 0, lblist, 4);
			lv.setAdapter(va);
			lv.setDivider(null);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
	            public void onItemClick(AdapterView<?> parent, View view,
	                    int position, long id) {
					setContentView(R.layout.activity_listview);
					ListView lv = (ListView)findViewById(R.id.listView);
					ArrayList<String> lblist = new ArrayList<String>();
					
					DBHelper dbh = new DBHelper(getApplicationContext());
					android.database.sqlite.SQLiteDatabase db = dbh.getReadableDatabase();
					Cursor cr = db.rawQuery("select m.name, m.n_id from stDB s left join mapsDB m on s.n_id = m.n_id where s.s_id = '" + sidlist.get(position) + "'",null);
					boolean eof = cr.moveToFirst();
					while(eof){
						lblist.add(cr.getString(0));
						nidlist.add(cr.getInt(1));
						eof = cr.moveToNext();
					}
					db.close();
					dbh.close();
					
					ViewAdapter va = new ViewAdapter(getApplicationContext(), 0, lblist, 4);
					lv.setAdapter(va);
					lv.setDivider(null);
					lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
			            public void onItemClick(AdapterView<?> parent, View view,
			                    int position, long id) {
							setContentView(R.layout.activity_detail);
							flg = 2;
							DBHelper dbh = new DBHelper(getApplicationContext());
							android.database.sqlite.SQLiteDatabase db = dbh.getReadableDatabase();
							Cursor cr = db.rawQuery("select name,address,tel,time,parking,lat,log from MapsDB where n_id = '" + nidlist.get(position) + "'",null);
							cr.moveToFirst();
							((TextView)findViewById(R.id.detail_name)).setText(cr.getString(0));
							((TextView)findViewById(R.id.detail_address)).setText("住所：" + cr.getString(1));
							((TextView)findViewById(R.id.detail_tel)).setText("Tel:0" + cr.getInt(2));
							((TextView)findViewById(R.id.detail_time)).setText("営業時間：" + cr.getString(3));
							((TextView)findViewById(R.id.detail_parking)).setText("駐車場：" + cr.getString(4));
							latlng = String.valueOf(cr.getDouble(5)) + "," + String.valueOf(cr.getDouble(6));
							
							db.close();
							dbh.close();
						}
					});
	            }

			});
			break;
		case R.id.genre_l_buil:
			setContentView(R.layout.activity_listview);
			lv = (ListView)findViewById(R.id.listView);
			lblist = new ArrayList<String>();
			
			dbh = new DBHelper(this);
			db = dbh.getReadableDatabase();
			cr = db.rawQuery("select s_id, s_name from smalltypeDB where t_id = '5'",null);
			eof = cr.moveToFirst();
			while(eof){
				sidlist.add(cr.getInt(0));
				lblist.add(cr.getString(1));
				eof = cr.moveToNext();
			}
			db.close();
			dbh.close();
			
			va = new ViewAdapter(this, 0, lblist, 5);
			lv.setAdapter(va);
			lv.setDivider(null);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
	            public void onItemClick(AdapterView<?> parent, View view,
	                    int position, long id) {
					setContentView(R.layout.activity_listview);
					ListView lv = (ListView)findViewById(R.id.listView);
					ArrayList<String> lblist = new ArrayList<String>();
					
					DBHelper dbh = new DBHelper(getApplicationContext());
					android.database.sqlite.SQLiteDatabase db = dbh.getReadableDatabase();
					Cursor cr = db.rawQuery("select m.name, m.n_id from stDB s left join mapsDB m on s.n_id = m.n_id where s.s_id = '" + sidlist.get(position) + "'",null);
					boolean eof = cr.moveToFirst();
					while(eof){
						lblist.add(cr.getString(0));
						nidlist.add(cr.getInt(1));
						eof = cr.moveToNext();
					}
					db.close();
					dbh.close();
					
					ViewAdapter va = new ViewAdapter(getApplicationContext(), 0, lblist, 5);
					lv.setAdapter(va);
					lv.setDivider(null);
					lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
			            public void onItemClick(AdapterView<?> parent, View view,
			                    int position, long id) {
							setContentView(R.layout.activity_detail);
							flg = 2;
							DBHelper dbh = new DBHelper(getApplicationContext());
							android.database.sqlite.SQLiteDatabase db = dbh.getReadableDatabase();
							Cursor cr = db.rawQuery("select name,address,tel,time,parking,lat,log from MapsDB where n_id = '" + nidlist.get(position) + "'",null);
							cr.moveToFirst();
							((TextView)findViewById(R.id.detail_name)).setText(cr.getString(0));
							((TextView)findViewById(R.id.detail_address)).setText("住所：" + cr.getString(1));
							((TextView)findViewById(R.id.detail_tel)).setText("Tel:0" + cr.getInt(2));
							((TextView)findViewById(R.id.detail_time)).setText("営業時間：" + cr.getString(3));
							((TextView)findViewById(R.id.detail_parking)).setText("駐車場：" + cr.getString(4));
							latlng = String.valueOf(cr.getDouble(5)) + "," + String.valueOf(cr.getDouble(6));
							
							db.close();
							dbh.close();
						}
					});
	            }

			});
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) { //メニューを作成
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.searchplace_menu,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) { //【地図】決定ボタンを押した時の処理
		// TODO Auto-generated method stub
		int itemid = item.getItemId();
		if(itemid == R.id.searchplace_menu_ok){
			if(flg == 1){
				if(!((EditText)findViewById(R.id.searchplace_et)).getText().toString().equals("")){
					wb.loadUrl("javascript:getPlace();");
				}else{
					AlertDialog.Builder adb = new AlertDialog.Builder(this);
					adb.setTitle("地点名がありません");
					adb.setMessage("地点名を入力してください");
					adb.setPositiveButton("OK", null);
					adb.create().show();
				}
			}else if(flg == 2){
				Intent vIntent2 = new Intent();
				vIntent2.putExtra("ReturnTid", et);
				vIntent2.putExtra("PlaceName", ((TextView)findViewById(R.id.detail_name)).getText());
				vIntent2.putExtra("LatLng", latlng);
				setResult(RESULT_OK,vIntent2);
				finish();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) { //戻るボタンで戻った時の処理
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent vIntent3 = new Intent();
			vIntent3.putExtra("ReturnTid", et);
			setResult(RESULT_CANCELED,vIntent3);
			finish();
			return true;
		}
		return false;
	}
	
}
