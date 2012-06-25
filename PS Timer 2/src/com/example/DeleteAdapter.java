package com.example;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DeleteAdapter extends ArrayAdapter<Model> {

	private final List<Model> list;
	private final Activity context;
	private int layoutResourceId;

	public DeleteAdapter(Activity context, int layoutResourceId,
			List<Model> list) {
		super(context, layoutResourceId, list);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.list = list;
	}

	static class ViewHolder {
		protected TextView text;
		protected CheckBox checkbox;
		public TextView crono;
		public TextView time;
	}

	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {

		View view = convertView;
		final ViewHolder viewHolder;

		if (view == null) {

			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(layoutResourceId, parent, false);
			viewHolder = new ViewHolder();

			viewHolder.text = (TextView) view.findViewById(R.id.TimerName);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.time = (TextView) view.findViewById(R.id.TimerTime);
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));

		} else {
			viewHolder = (ViewHolder) view.getTag();
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}

		
		viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				
				Model element = (Model) viewHolder.checkbox.getTag();
				element.setSelected(buttonView.isChecked());

				//Enabling Delete button
				if (buttonView.isChecked()) {
					Button myButton = (Button) context.findViewById(R.id.delete);
					myButton.setEnabled(true);
				}

				// if we are unchecking the button
				else {
					
					//uncheck checkAll checkbox if it was checked
					CheckBox checkbox = (CheckBox) context.findViewById(R.id.checkAll);
					if (checkbox.isChecked()) checkbox.setChecked(false);

					//checking if this button is the only one unchecked
					int checkedTimers = 0;
					ListView listView = (ListView) context.findViewById(R.id.list);

					for (int i = 0; i < listView.getChildCount(); i++) {

						RelativeLayout itemLayout = (RelativeLayout) listView.getChildAt(i);
						CheckBox cb = (CheckBox) itemLayout.findViewById(R.id.check);
						if (cb.isChecked())	checkedTimers++;
					}

					//if this checkbox was the only one checked then disable button
					if (checkedTimers == 0) {
						Button myButton = (Button) context.findViewById(R.id.delete);
						myButton.setEnabled(false);
					}

				}

			}
		});

		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		String formatted = format.format(list.get(position).getTime()); 
		
		viewHolder.time.setText(formatted);
		viewHolder.text.setText(list.get(position).getName());

		return view;
	}

}
