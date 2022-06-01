package com.example.android.project;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.android.project.data.contract;
import com.example.android.project.login.MainActivity;
import com.example.android.project.specificvenue.venueDetailsActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class diary extends AppCompatActivity {
    EditText user_rating,to_time,from_time,user_info;
    Switch visit;
    ProgressBar loading;
    ArrayList<String> userlist_name;
    ArrayList<String> userlist_venue;
    int row;
    int added=0;
    String isvisited,Rating;
    LinearLayout time_layout;
    boolean bookmark;
    private Uri uri=null;
    String user_id,venue_name;
    Button del;
    public static  final String PLACEID1="placeId1";
    public static  final String PLACEID2="placeId2";
    private static final int PLACE_LOADER_ID=1;
    private static final int MARK_LOADER_ID=2;
    int cnt=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.diary_activity);
    user_rating=findViewById(R.id.user_rating);
    user_info=findViewById(R.id.user_details);
    from_time=findViewById(R.id.from_time);
    to_time=findViewById(R.id.to_time);
    visit=findViewById(R.id.visit);
    loading=findViewById(R.id.loading);
    userlist_name=new ArrayList<>();
    userlist_venue=new ArrayList<>();
    del=findViewById(R.id.deleteplace);
    time_layout=findViewById(R.id.time_layout);
    time_layout.setVisibility(View.GONE);
loadData();
        Intent intent=getIntent();
        bookmark=intent.getBooleanExtra("bookmark",false);
        user_id=intent.getStringExtra("UserID");
        venue_name=intent.getStringExtra("name");
        if(!bookmark)
        { loading.setVisibility(View.GONE);
            del.setVisibility(View.GONE);
        }
        if(bookmark)
        { setTitle("Marked Place");
            uri=intent.getData();
            if(uri!=null)
            {  user_info.setEnabled(true);user_info.setInputType(InputType.TYPE_CLASS_TEXT);
                from_time.setEnabled(true);from_time.setInputType(InputType.TYPE_CLASS_TEXT);
                to_time.setEnabled(true);to_time.setInputType(InputType.TYPE_CLASS_TEXT);
                user_rating.setEnabled(true);user_rating.setInputType(InputType.TYPE_CLASS_TEXT);
                getLoaderManager().initLoader(PLACE_LOADER_ID,null,mark);
                user_info.setEnabled(false);user_info.setInputType(InputType.TYPE_NULL);
                from_time.setEnabled(false);from_time.setInputType(InputType.TYPE_NULL);
                to_time.setEnabled(false);to_time.setInputType(InputType.TYPE_NULL);
                user_rating.setEnabled(false);user_rating.setInputType(InputType.TYPE_NULL);
            }

        }

        visit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
            {isvisited="Visited";
            time_layout.setVisibility(View.VISIBLE);
            }
            else
            {time_layout.setVisibility(View.GONE);
                isvisited="Not yet visited";
            }

        }
    });
    user_rating.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(actionId==EditorInfo.IME_ACTION_DONE)
            {
                if(Integer.parseInt(user_rating.getText().toString())<=5&&Integer.parseInt(user_rating.getText().toString())>0)
                Rating=user_rating.getText().toString();
                else
                {
                 user_rating.setError("Rate the place out of 5");
                }
            }
        return false;
        }
    });
    }
    public void deleteDiary(View view)
    {

    int rowsdeleted=getContentResolver().delete(uri,null,null);
        if(rowsdeleted!=0)
        {
            for(int i=0;i<userlist_venue.size();i++)
            {
                if(userlist_name.get(i).equals(MainActivity.USERNAME)&&userlist_venue.get(i).equals(venue_name))
                {
                    userlist_venue.remove(i);
                    userlist_name.remove(i);
                    saveData();
                }
            }

        }
  finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_diary,menu);
        if(!bookmark)
        {MenuItem item1=menu.findItem(R.id.edit);
            item1.setVisible(false);
        this.invalidateOptionsMenu();}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
       if(id==R.id.save)
        {  // getLoaderManager().initLoader(MARK_LOADER_ID,null,diary_info);
            diary_infos();
            return true;
        }
        if(id==R.id.edit)
        { user_info.setEnabled(true);user_info.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
          from_time.setEnabled(true);from_time.setInputType(InputType.TYPE_CLASS_DATETIME);
          to_time.setEnabled(true);to_time.setInputType(InputType.TYPE_CLASS_DATETIME);
          user_rating.setEnabled(true);user_rating.setInputType(InputType.TYPE_CLASS_NUMBER);
          return true;
        }
        return super.onOptionsItemSelected(item);
    }
private void diary_infos()
{              if (!getTitle().equals("Marked Place")) {
    if (userlist_name.size() != 0) {
        for (int i = 0; i < userlist_name.size(); i++) {
            if (userlist_name.get(i).equals(MainActivity.USERNAME) && userlist_venue.get(i).equals(venue_name)) {
                added++;
            }}}
    if (added > 0)
    {Toast.makeText(diary.this, "Already marked the place", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(diary.this,venueDetailsActivity.class);
        intent.putExtra("ID",user_id);
        startActivity(intent);
    }
}
    if (added == 0) {
        if(TextUtils.isEmpty(user_rating.getText())&&TextUtils.isEmpty(user_info.getText())&&TextUtils.isEmpty(from_time.getText())&&TextUtils.isEmpty(to_time.getText())&&isvisited==null)
        {
            //     finish();
            Intent intent=new Intent(diary.this,venueDetailsActivity.class);
            intent.putExtra("ID",user_id);
            startActivity(intent);

        }
        else {
            ContentValues values = new ContentValues();
            values.put(contract.loginEntry.c3rating,user_rating.getText().toString());
            values.put(contract.loginEntry.c4visited, isvisited);
            values.put(contract.loginEntry.c5from_time, from_time.getText().toString());
            values.put(contract.loginEntry.c6to_time, to_time.getText().toString());
            values.put(contract.loginEntry.c7info, user_info.getText().toString());
            values.put(contract.loginEntry.c8name,venueDetailsActivity.name);
            values.put(contract.loginEntry.c9category,venueDetailsActivity.category_text);
            values.put(contract.loginEntry.c10address,venueDetailsActivity.address_text);
            values.put(contract.loginEntry.c11id,user_id);
            if (uri == null)
            {
                Uri newuri = getContentResolver().insert(contract.loginEntry.CONTENT_URI, values);
                Toast.makeText(diary.this, "Marked the place", Toast.LENGTH_SHORT).show();
                userlist_name.add(MainActivity.USERNAME);
                userlist_venue.add(venue_name);
                             saveData();
                //                finish();
                if(cnt==0) {
                    Intent intent = new Intent(diary.this, venueDetailsActivity.class);
                    intent.putExtra("ID", user_id);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(diary.this, markedPlaces_list.class);
                    startActivity(intent);

                }
            }
            else{
                int rowsdeleted=getContentResolver().delete(uri,null,null);
                uri=null;
                cnt=1;
                diary_infos();
                }


        }
    }


}

public void saveData()
{
    SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(diary.this);
    SharedPreferences.Editor editor=sharedPreferences.edit();
    Gson gson=new Gson();
    String json1=gson.toJson(userlist_name);
    String json2=gson.toJson(userlist_venue);
    editor.putString(PLACEID1,json1);
    editor.putString(PLACEID2,json2);
    editor.apply();
}

    public void loadData()
    { SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(diary.this);
        Gson gson=new Gson();
        String json1=sharedPreferences.getString(PLACEID1,null);
        String json2=sharedPreferences.getString(PLACEID2,null);
        Type type=new TypeToken<ArrayList<String>>(){}.getType();
        userlist_name=gson.fromJson(json1,type);
        userlist_venue=gson.fromJson(json2,type);
        if(userlist_name==null){
            userlist_name=new ArrayList<>();
        }
        if(userlist_venue==null){
            userlist_venue=new ArrayList<>();
        }
    }

private LoaderCallbacks<Cursor> mark=new LoaderCallbacks<Cursor>() {
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={
                   contract.loginEntry.rowid,
                    contract.loginEntry.c3rating,
                    contract.loginEntry.c4visited,
                    contract.loginEntry.c5from_time,
                    contract.loginEntry.c6to_time,
                    contract.loginEntry.c7info};
        return new CursorLoader(getApplicationContext(),uri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
if(cursor==null||cursor.getCount()<1)
{
    return;
}
if(cursor.moveToFirst())
{
    int c3=cursor.getColumnIndex(contract.loginEntry.c3rating);
    int c4=cursor.getColumnIndex(contract.loginEntry.c4visited);
    int c5=cursor.getColumnIndex(contract.loginEntry.c5from_time);
    int c6=cursor.getColumnIndex(contract.loginEntry.c6to_time);
    int c7=cursor.getColumnIndex(contract.loginEntry.c7info);
String s3=cursor.getString(c3);
String s4=cursor.getString(c4);
String s5=cursor.getString(c5);
String s6=cursor.getString(c6);
String s7=cursor.getString(c7);
if(s3!=null)
user_rating.setText(s3);
    if(s7!=null)
user_info.setText(s7);
if(s4!=null &&s4.equals("Visited"))
visit.setChecked(true);
if(s5!=null)
    from_time.setText(s5);
if(s6!=null)
    to_time.setText(s6);
loading.setVisibility(View.GONE);
}

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
};

}
