package com.colour.memory.adapter;
import com.colour.memory.*;
import com.colour.memory.model.*;

import java.util.List;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class AdapterPeople extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private List<People> peoples;


	public AdapterPeople(Activity activity, List<People> peoples) {
		this.activity = activity;
		this.peoples = peoples;
	}

	@Override
	public int getCount() {
		return peoples.size();
	}

	@Override
	public Object getItem(int location) {
		return peoples.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.people_item, null);

		
		TextView Name = (TextView) convertView.findViewById(R.id.nameText);
		TextView Score = (TextView) convertView.findViewById(R.id.scoreText);
		TextView Rank = (TextView) convertView.findViewById(R.id.rankText);


		People p = peoples.get(position);

		Score.setText(String.valueOf(p.getScore()));
		Name.setText(p.getName());
		Rank.setText(String.valueOf(position + 1));


		return convertView;
	}

	
	public void destroy() {
		peoples.clear();
	}

}