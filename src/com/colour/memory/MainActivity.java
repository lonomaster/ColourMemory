package com.colour.memory;
import com.colour.memory.model.*;
import com.colour.memory.sqlite.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static int ROW_COUNT = -1;
	private static int COL_COUNT = -1;
	private Context context;
	private Drawable backImage;
	private int [] [] cards;
	private List<Drawable> images;
	private Card firstCard;
	private Card seconedCard;
	private ButtonListener buttonListener;
	public int x = 4;
	public int y = 4;
	public int count = 8;

	public int HighScore; 

	private SqliteClass dbInstance;	

	private static Object lock = new Object();

	int turns;
	private TableLayout mainTable;
	private UpdateCardsHandler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dbInstance = new SqliteClass(this);
		handler = new UpdateCardsHandler();
		loadImages();
		setContentView(R.layout.main);


		backImage =  getResources().getDrawable(R.drawable.card_bg);



		((Button)findViewById(R.id.highScore)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, TableScore.class);
				Bundle b = new Bundle(); 
				b.putString("name", "vacio");
				b.putString("score", "1");
				intent.putExtras(b);
				startActivity(intent); 

			}

		});




		buttonListener = new ButtonListener();

		mainTable = (TableLayout)findViewById(R.id.TableLayout);


		context  = mainTable.getContext();

		Thread t = new Thread() {
			public void run() {
				Looper.prepare(); 
				SQLiteDatabase db = dbInstance.getWritableDatabase();
				if(db != null){		
					Cursor c = db.rawQuery("SELECT * FROM people ORDER BY score ASC", null);
					if(c.moveToFirst()){
						do {
							HighScore = c.getInt(2);

						} while (c.moveToNext());
					}
					c.close();

					db.close();
				}
				Looper.loop(); 
			}
		};
		t.start();  



		newGame(x,y);


	}


	private void newGame(int c, int r) {
		ROW_COUNT = r;
		COL_COUNT = c;

		cards = new int [COL_COUNT] [ROW_COUNT];


		TableRow tr = ((TableRow)findViewById(R.id.TableRow));
		tr.removeAllViews();

		mainTable = new TableLayout(context);
		tr.addView(mainTable);

		for (int y = 0; y < ROW_COUNT; y++) {
			mainTable.addView(createRow(y));
		}

		firstCard=null;
		loadCards();

		turns=0;
		((TextView)findViewById(R.id.score)).setText("Score: "+turns);
		((Button)findViewById(R.id.highScore)).setText("High Score: "+HighScore);


	}

	private void loadImages() {
		images = new ArrayList<Drawable>();

		images.add(getResources().getDrawable(R.drawable.colour1));
		images.add(getResources().getDrawable(R.drawable.colour2));
		images.add(getResources().getDrawable(R.drawable.colour3));
		images.add(getResources().getDrawable(R.drawable.colour4));
		images.add(getResources().getDrawable(R.drawable.colour5));
		images.add(getResources().getDrawable(R.drawable.colour6));
		images.add(getResources().getDrawable(R.drawable.colour7));
		images.add(getResources().getDrawable(R.drawable.colour8));

	}

	private void loadCards(){
		try{
			int size = ROW_COUNT*COL_COUNT;

			Log.i("loadCards()","size=" + size);

			ArrayList<Integer> list = new ArrayList<Integer>();

			for(int i=0;i<size;i++){
				list.add(Integer.valueOf(i));
			}


			Random r = new Random();

			for(int i=size-1;i>=0;i--){
				int t=0;

				if(i>0){
					t = r.nextInt(i);
				}

				t=list.remove(t).intValue();
				cards[i%COL_COUNT][i/COL_COUNT]=t%(size/2);

				Log.i("loadCards()", "card["+(i%COL_COUNT)+
						"]["+(i/COL_COUNT)+"]=" + cards[i%COL_COUNT][i/COL_COUNT]);
			}
		}
		catch (Exception e) {
			Log.e("loadCards()", e+"");
		}

	}

	private TableRow createRow(int y){
		TableRow row = new TableRow(context);
		row.setHorizontalGravity(Gravity.CENTER);

		for (int x = 0; x < COL_COUNT; x++) {
			row.addView(createImageButton(x,y));
		}
		return row;
	}

	@SuppressWarnings("deprecation")
	private View createImageButton(int x, int y){
		Button button = new Button(context);
		button.setBackgroundDrawable(backImage);
		button.setId(100*x+y);
		button.setOnClickListener(buttonListener);
		return button;
	}

	class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			synchronized (lock) {
				if(firstCard!=null && seconedCard != null){
					return;
				}
				int id = v.getId();
				int x = id/100;
				int y = id%100;
				turnCard((Button)v,x,y);
			}

		}

		@SuppressWarnings("deprecation")
		private void turnCard(Button button,int x, int y) {
			button.setBackgroundDrawable(images.get(cards[x][y]));

			if(firstCard==null){
				firstCard = new Card(button,x,y);
			}
			else{ 

				if(firstCard.x == x && firstCard.y == y){
					return;
				}

				seconedCard = new Card(button,x,y);



				TimerTask tt = new TimerTask() {

					@Override
					public void run() {
						try{
							synchronized (lock) {
								handler.sendEmptyMessage(0);
							}
						}
						catch (Exception e) {
							Log.e("E1", e.getMessage());
						}
					}
				};

				Timer t = new Timer(false);
				t.schedule(tt, 1300);
			}


		}

	}

	@SuppressLint("HandlerLeak")
	class UpdateCardsHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			synchronized (lock) {
				checkCards();
			}
		}
		@SuppressWarnings("deprecation")
		public void checkCards(){
			if(cards[seconedCard.x][seconedCard.y] == cards[firstCard.x][firstCard.y]){
				firstCard.button.setVisibility(View.INVISIBLE);
				seconedCard.button.setVisibility(View.INVISIBLE);
				turns = turns + 2;
				count--;
				((TextView)findViewById(R.id.score)).setText("Score: "+turns);
				if(count==0) {
					Intent intent = new Intent(MainActivity.this, Score.class);
					Bundle b = new Bundle(); 
					b.putString("score", String.valueOf(turns));
					intent.putExtras(b);
					startActivity(intent); 
				}

			}
			else {
				seconedCard.button.setBackgroundDrawable(backImage);
				firstCard.button.setBackgroundDrawable(backImage);
				turns--;
				((TextView)findViewById(R.id.score)).setText("Score: "+turns);
			}

			firstCard=null;
			seconedCard=null;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {

		case R.id.new_game:
			count = 8;
			turns = 0;

			Thread t = new Thread() {
				public void run() {
					Looper.prepare(); 
					SQLiteDatabase db = dbInstance.getWritableDatabase();
					if(db != null){		
						Cursor c = db.rawQuery("SELECT * FROM people ORDER BY score ASC", null);
						if(c.moveToFirst()){
							do {
								HighScore = c.getInt(2);

							} while (c.moveToNext());
						}
						c.close();

						db.close();
					}

					Looper.loop(); 
				}
			};
			t.start();  
			newGame(x,y);

			return true;


		default:
			return super.onOptionsItemSelected(item);
		}
	}




}