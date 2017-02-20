package com.lesson.vadim.attract.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lesson.vadim.attract.constructor.Marvel;
import com.lesson.vadim.attract.R;

import java.io.InputStream;
import java.util.ArrayList;

public class ImageScrollAdapter extends ArrayAdapter<Marvel>{
    ArrayList<Marvel> arrayList;
    int resources;
    Context context;
    LayoutInflater layoutInflater;

    public ImageScrollAdapter(Context context, int resource, ArrayList<Marvel> objects) {
        super(context, resource, objects);
        this.arrayList = objects;
        resources = resource;
        this.context = context;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(resources, null);
            holder = new ViewHolder();
            holder.image = (ImageView)convertView.findViewById(R.id.image);
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.time = (TextView)convertView.findViewById(R.id.time);
            holder.description = (TextView)convertView.findViewById(R.id.description);
            holder.itemId = (TextView)convertView.findViewById(R.id.itemId);

            convertView.setTag(holder);
        } else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.name.setText(arrayList.get(position).getName());
        holder.time.setText(arrayList.get(position).getTime());
        holder.description.setText(arrayList.get(position).getDescription());
        holder.itemId.setText(arrayList.get(position).getItemId());
        holder.image.setImageResource(R.mipmap.ic_dp);
        new DownloadImageTask(holder.image).execute(arrayList.get(position).getImage());
        return convertView;
    }

    class ViewHolder{
        public ImageView image;
        public TextView name;
        public TextView time;
        public TextView description;
        public TextView itemId;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }

    }
}

