package com.colour.memory;
import com.colour.memory.adapter.*;
import com.colour.memory.model.*;
import com.colour.memory.sqlite.*;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;



public class TableScore  extends Activity {
	public Button  btnName;
	public EditText txtUsername;
	public SQLiteDatabase db;
	public int score;
	public String name; 

	private AsyncTask<Void, Void, List<People>> task;
	private SqliteClass dbInstance;
	private ListView listView;

	private List<People> peoples;

	@Override
	protected void onCreate(Bundle savedInstanceState) {


		super.onCreate(savedInstanceState);
		setContentView(R.layout.table_score);
		final Bundle bundle = this.getIntent().getExtras();

		name = bundle.getString("name");
		score = Integer.valueOf(bundle.getString("score"));
		dbInstance = new SqliteClass(this);
		peoples = new ArrayList<People>();
		listView = (ListView) findViewById(R.id.rankList);
		task = new AsyncTask<Void, Void, List<People>>(){

			@Override
			protected void onPreExecute(){
			}

			@Override
			protected List<People> doInBackground(Void... params) {
				return saveSqlite(name, score);
			}

			@Override
			protected void onPostExecute(List<People> result) {

				AdapterPeople adapter = new AdapterPeople(TableScore.this,result);
				listView.setAdapter(adapter);
			}

		};

		task.execute();


	}



	private List<People> saveSqlite(String name, int score){
		SQLiteDatabase db = dbInstance.getWritableDatabase();
		if(db != null){
			if(!name.equals("vacio")){
				db.beginTransaction();
				try{
					db.execSQL("INSERT INTO people (name, score) " +
							"VALUES ('" + name + "', '" + 
							score + "')");
				} finally {
					db.setTransactionSuccessful();
				}
				db.endTransaction();
			}

			Cursor c = db.rawQuery("SELECT * FROM people ORDER BY score DESC", null);
			if(c.moveToFirst()){
				do {
					People model = new People();
					model.setName(c.getString(1));
					model.setScore(c.getInt(2));

					peoples.add(model);
				} while (c.moveToNext());
			}
			c.close();

			db.close();
		}

		return peoples;
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MainActivity.class));
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.table_score);
		final Bundle bundle = this.getIntent().getExtras();

		name = "vacio";
		score = Integer.valueOf(bundle.getString("score"));
		dbInstance = new SqliteClass(this);
		peoples = new ArrayList<People>();
		listView = (ListView) findViewById(R.id.rankList);
		task = new AsyncTask<Void, Void, List<People>>(){

			@Override
			protected void onPreExecute(){
			}

			@Override
			protected List<People> doInBackground(Void... params) {
				return saveSqlite(name, score);
			}

			@Override
			protected void onPostExecute(List<People> result) {

				AdapterPeople adapter = new AdapterPeople(TableScore.this,result);
				listView.setAdapter(adapter);
			}

		};

		task.execute();
	}

}

