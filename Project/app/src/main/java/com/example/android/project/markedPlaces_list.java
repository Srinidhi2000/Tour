package com.example.android.project;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.project.data.contract;
import com.google.android.gms.maps.Projection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class markedPlaces_list extends AppCompatActivity implements LoaderCallbacks<Cursor> {
   private static final int PLACES_LOADER=1;
   ListView list;
   TextView empty;
markedPlaces_adapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.markedplaces_activity);
        list=findViewById(R.id.list);
        empty=findViewById(R.id.emptystate);
        list.setEmptyView(empty);
        adapter=new markedPlaces_adapter(this,null);
        list.setAdapter(adapter);
       list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              adapter.getCursor().moveToPosition(position);
                int nameIndex=adapter.getCursor().getColumnIndex(contract.loginEntry.c8name);
                String name=adapter.getCursor().getString(nameIndex);
                Intent intent=new Intent(markedPlaces_list.this,diary.class);
                intent.putExtra("bookmark",true);
                intent.putExtra("name",name);
                Uri currenturi=ContentUris.withAppendedId(contract.loginEntry.CONTENT_URI,id);
                intent.setData(currenturi);
                startActivity(intent);
            }
        });
    getLoaderManager().initLoader(PLACES_LOADER,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={
                contract.loginEntry.rowid,
                contract.loginEntry.c1username,
                contract.loginEntry.c8name,
                contract.loginEntry.c9category,
                contract.loginEntry.c10address};
                return new CursorLoader(markedPlaces_list.this,contract.loginEntry.CONTENT_URI,projection,null,null,null);
         }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
adapter.swapCursor(null);
    }
}
