package com.colour.memory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class Score  extends Activity {
	public Button  btnName;
	public EditText txtUsername;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score);
		TextView scoreFinal = (TextView) findViewById(R.id.scoreFinal);
		final Bundle bundle = this.getIntent().getExtras();
		scoreFinal.setText(bundle.getString("score"));

		btnName = (Button)this.findViewById(R.id.Send);
		txtUsername = (EditText)this.findViewById(R.id.Name);
		btnName.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if( txtUsername.getText().toString().length() == 0 )
					txtUsername.setError( "Name is required!" );
				else {

					Intent intent = new Intent(Score.this, TableScore.class);
					Bundle b = new Bundle(); 
					b.putString("name", txtUsername.getText().toString());
					b.putString("score", bundle.getString("score"));
					intent.putExtras(b);
					startActivity(intent); 
				}
			}
		});

	}




}

