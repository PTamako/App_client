package com.example.lenovo.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lenovo.test.model.Comment;


import java.text.SimpleDateFormat;
import java.util.List;

public class AdapterComment extends BaseAdapter {

    Context context;
    List<Comment> data;

    public AdapterComment(Context c, List<Comment> data){
        this.context = c;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        // 重用convertView
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.comment, null);
            holder.comment_name = (TextView) convertView.findViewById(R.id.comment_name);
            holder.comment_text = (TextView) convertView.findViewById(R.id.comment_text);
            holder.comment_grade = (TextView) convertView.findViewById(R.id.comment_grade);
            holder.comment_time = (TextView) convertView.findViewById(R.id.comment_time);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        // 适配数据
        SimpleDateFormat simpledata = new SimpleDateFormat("yyyy-MM-dd");
        holder.comment_name.setText(data.get(i).getUserName()+ "：");
        holder.comment_text.setText(data.get(i).getReview());
        holder.comment_grade.setText("评分："+String.valueOf(data.get(i).getGrade())+"分");//将int类型的数字转变为字符串
        holder.comment_time.setText(simpledata.format(data.get(i).getCurrentDate()).toString());//将时间格式化，变成yyyy-mm-dd

        return convertView;
    }

    /**
     * 添加一条评论,刷新列表
     * @param comment
     */
    public void addComment(Comment comment){
        data.add(comment);
        notifyDataSetChanged();
    }

    /**
     * 静态类，便于GC回收
     */
    public static class ViewHolder{
        TextView comment_name;
        TextView comment_text;
        TextView comment_grade;
        TextView comment_time;

    }
}