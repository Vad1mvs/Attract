package com.lesson.vadim.attract.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.lesson.vadim.attract.constructor.Marvel;
import com.lesson.vadim.attract.activitys.InformationMarvelActivity;
import com.lesson.vadim.attract.R;
import com.lesson.vadim.attract.adapters.ListMarvelAdapter;

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
import java.util.ArrayList;
import java.util.Calendar;

public class FragmentMain extends Fragment{
    ListView list;
    ListMarvelAdapter adapter;
    ArrayList<Marvel> marvelList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        new MarvelAsyncTask().execute("http://others.php-cd.attractgroup.com/test.json");
        list = (ListView)view.findViewById(R.id.list);
        marvelList = new ArrayList<Marvel>();

        adapter = new ListMarvelAdapter(getActivity(), R.layout.item, marvelList);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), InformationMarvelActivity.class);
                intent.putExtra("name", marvelList.get(position).getName());
                intent.putExtra("time", marvelList.get(position).getTime());
                intent.putExtra("image", marvelList.get(position).getImage());
                intent.putExtra("description", marvelList.get(position).getDescription());
                intent.putExtra("itemId", marvelList.get(position).getItemId());
                startActivity(intent);

            }
        });
        return view;
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

                        String stringTime = jsonObject.getString("time");
                        Calendar calendar = Calendar.getInstance();
                        String date = stringTime.replace("/Date(", "").replace(")/", "");
                        Long timeInMillis = Long.valueOf(date);
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
            adapter.notifyDataSetChanged();
            if(result == false){
                Toast.makeText(getActivity(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();
            }
        }
    }
}

