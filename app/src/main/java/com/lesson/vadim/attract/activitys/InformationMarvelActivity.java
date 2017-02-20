package com.lesson.vadim.attract.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;

import com.lesson.vadim.attract.constructor.Marvel;
import com.lesson.vadim.attract.R;
import com.lesson.vadim.attract.adapters.ImageScrollAdapter;
import com.lesson.vadim.attract.adapters.ListMarvelAdapter;
import com.lesson.vadim.attract.constructor.HorizontalView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class InformationMarvelActivity extends AppCompatActivity {
    TextView name, time, description, itemId;
    ImageView image;
    ArrayList<Marvel> arrayList;
    HorizontalView list;
    ListMarvelAdapter adapter;
    ImageScrollAdapter imageAdapter;
    ArrayList<Marvel> marvelList = new ArrayList<Marvel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        new MarvelAsyncTask().execute("http://others.php-cd.attractgroup.com/test.json");
        name = (TextView)findViewById(R.id.name);
        time = (TextView)findViewById(R.id.time);
        description = (TextView)findViewById(R.id.description);
        itemId = (TextView)findViewById(R.id.itemId);
        image = (ImageView)findViewById(R.id.image);

        Intent intent  = getIntent();
        String tvName = intent.getStringExtra("name");
        String tvTime = intent.getStringExtra("time");
        String tvDescription = intent.getStringExtra("description");
        String tvItemId = intent.getStringExtra("itemId");
        String tvImage = intent.getStringExtra("image");

        name.setText(tvName);
        time.setText(tvTime);
        description.setText(tvDescription);
        itemId.setText(tvItemId);

        image.setImageResource(R.mipmap.ic_dp);
        new DownloadImageTask(image).execute(tvImage);

        list = (HorizontalView) findViewById(R.id.gallery);
        marvelList = new ArrayList<Marvel>();
        imageAdapter = new ImageScrollAdapter(this, R.layout.image, marvelList);
        list.setAdapter(imageAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                name.setText(marvelList.get(position).getName());
                description.setText(marvelList.get(position).getDescription());
                time.setText(marvelList.get(position).getTime());
                itemId.setText(marvelList.get(position).getItemId());
                String strImage = marvelList.get(position).getImage();
                new DownloadImageTask(image).execute(strImage);
            }
        });

    }

    public class MarvelAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {


                HttpClient client  = new DefaultHttpClient();
                HttpGet httppost = new HttpGet(params[0]);
                HttpResponse response = client.execute(httppost);

                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    JSONArray jsonArray = new JSONArray(data);

                    for (int i=0; i<jsonArray.length(); i++){
                        Marvel marvel = new Marvel();

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        marvel.setName(jsonObject.getString("name"));
                        marvel.setImage(jsonObject.getString("image"));
                        marvel.setDescription(jsonObject.getString("description"));
                        marvel.setItemId(jsonObject.getString("itemId"));
                        String ackwardDate = jsonObject.getString("time");
                        Calendar calendar = Calendar.getInstance();
                        String ackwardRipOff = ackwardDate.replace("/Date(", "").replace(")/", "");
                        Long timeInMillis = Long.valueOf(ackwardRipOff);
                        calendar.setTimeInMillis(timeInMillis);
                        marvel.setTime(calendar.getTime().toGMTString());

                        marvelList.add(marvel);
                    }
                    return true;

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            imageAdapter.notifyDataSetChanged();
            if(result == false){
                Toast.makeText(InformationMarvelActivity.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView image;

        public DownloadImageTask(ImageView bmImage) {
            this.image = bmImage;
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
            image.setImageBitmap(result);
        }

    }
}

