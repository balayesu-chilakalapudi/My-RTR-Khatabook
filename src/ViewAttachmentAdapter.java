package com.bchilakalapudi.rtrconstruction;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.bchilakalapudi.rtrconstruction.model.Attachment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ViewAttachmentAdapter implements ListAdapter {
    public ArrayList<Attachment> attlist;
    public Attachment att;
    public Context context;
    public TextView filename;
    public TextView filesize;
    public ImageView attpvw;
    public ListView atlvw;
    public ViewAttachmentAdapter adapter;

    public ViewAttachmentAdapter(Context context, ArrayList<Attachment> arrayList, ListView attlvw) {
        Log.d("ViewAttachmentAdapter",""+arrayList);
        this.attlist=arrayList;
        this.context=context;
        atlvw=attlvw;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return attlist.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        att = attlist.get(position);
        Log.d("att",""+att);
        Log.d("convertView",""+convertView);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.viewattachmentslvw, null);
            filename = convertView.findViewById(R.id.vwatt_filename);
            filesize=convertView.findViewById(R.id.vwatt_size);
            attpvw=convertView.findViewById(R.id.vwatt_preview);


            filename.setText(""+att.getFilename());
            filesize.setText(""+att.getSize());

            if(att.getDownloadUrl()!=null) {
                Picasso.with(context)
                        .load(att.getDownloadUrl())
                        .into(attpvw);
            }
            /*else{
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), att.getFilePath());
                    attpvw.setImageBitmap(bitmap);
                }catch(Exception ex){
                    Log.d("Exception",""+ex);
                }
            }*/
          convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("position",""+position);
                  att = attlist.get(position);
                  //  attlist.remove(position);
                 /*  Intent in=new Intent(context,OpenattachmentActivity.class);
                   in.putExtra("transactionId",att.getTransactionId());
                   in.putExtra("imageurl",att.getDownloadUrl());
                   context.startActivity(in);*/

                   context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(att.getDownloadUrl())));

                }
            });
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        int retsize=1;
        if(attlist.size()>1)
            retsize=attlist.size();
        return retsize;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}