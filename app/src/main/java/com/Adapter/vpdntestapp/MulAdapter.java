package com.Adapter.vpdntestapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.example.vpdntestapp.R;
import java.util.HashMap;
import java.util.List;

/**
 * @author panqi
 */
public class MulAdapter extends BaseAdapter {

  private Context context;
  private static List<HashMap<String, String>> list;
  private static HashMap<Integer, Boolean> isSelected;
  private LayoutInflater layoutInflater = null;

  public MulAdapter(Context context, List<HashMap<String, String>> list) {
    this.context = context;
    this.list = list;
    layoutInflater = LayoutInflater.from(context);
    isSelected = new HashMap<Integer, Boolean>();
    for (int i = 0; i < list.size(); i++) {
      isSelected.put(i, false);
    }
    initData();
  }

  private void initData() {
    for (int i = 0; i < list.size(); i++) {
      getIsSelected();
    }
  }

  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public Object getItem(int position) {
    return list.get(position);
  }

  @Override
  public long getItemId(int position) {
    return Integer.parseInt(list.get(position).get("id"));
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder = null;
    if (convertView == null) {//当第一次加载ListView控件时 convertView为空
      convertView = layoutInflater
        .inflate(R.layout.item, null);//所以当ListView控件没有滑动时都会执行这条语句
      holder = new ViewHolder();
      holder.ipView = convertView.findViewById(R.id.item_ip);
      holder.idView = convertView.findViewById(R.id.item_id);
      holder.checkBox = convertView.findViewById(R.id.item_cb);
      convertView.setTag(holder);//为view设置标签
    } else {//取出holder
      holder = (ViewHolder) convertView.getTag();//the Object stored in this view as a tag
    }
//设置list的textview显示
    holder.ipView.setTextColor(Color.BLACK);
    holder.ipView.setText(list.get(position).get("ipAddress"));
    holder.idView.setText(list.get(position).get("id"));
// 根据isSelected来设置checkbox的选中状况
    if (!isSelected.isEmpty()) {
      holder.checkBox.setChecked(getIsSelected().get(position));
    }
    return convertView;

  }

  public static class ViewHolder {

    public TextView ipView;
    public TextView idView;
    public CheckBox checkBox;
  }

  public static HashMap<Integer, Boolean> getIsSelected() {
    return isSelected;
  }

  public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
    MulAdapter.isSelected = isSelected;
  }
}
