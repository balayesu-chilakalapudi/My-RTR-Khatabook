package com.bchilakalapudi.rtrconstruction;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bchilakalapudi.rtrconstruction.controller.FirebaseDatabaseHandler;
import com.bchilakalapudi.rtrconstruction.model.Attachment;
import com.bchilakalapudi.rtrconstruction.shared.SharedData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AttachmentAdapter implements ListAdapter {
    public ArrayList<AttachmentWrapper> attlist;
    public AttachmentWrapper att;
    public Context context;
    public TextView filename;
    public TextView filesize;
    public ImageView attpvw;
    public ListView atlvw;
    public AttachmentAdapter adapter;
    public FirebaseDatabaseHandler dbhandler;

    public AttachmentAdapter(Context context, ArrayList<AttachmentWrapper> arrayList,ListView attlvw) {
        Log.d("AttachmentAdapter",""+arrayList);
        this.attlist=arrayList;
        this.context=context;
        atlvw=attlvw;
        dbhandler= SharedData.getDbhandler();
        if(dbhandler==null){
            dbhandler=new FirebaseDatabaseHandler();
        }
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
            convertView = layoutInflater.inflate(R.layout.editattachmentslvw, null);
            filename = convertView.findViewById(R.id.att_filename);
            filesize=convertView.findViewById(R.id.att_size);
            attpvw=convertView.findViewById(R.id.att_preview);


            filename.setText(""+att.getFilename());
            filesize.setText(""+att.getSize());

            if(att.getDownloadUrl()!=null) {
                Picasso.with(context)
                        .load(att.getDownloadUrl())
                        .into(attpvw);
            }else{
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), att.getFilePath());
                    attpvw.setImageBitmap(bitmap);
                }catch(Exception ex){
                    Log.d("Exception",""+ex);
                }
            }
          convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("position",""+position);
                    att = attlist.get(position);
                    Log.d("att",""+att);
                    if(att.getId()!=null) {
                        dbhandler.removeAttachment(att.getId());
                        dbhandler.deleteStorageFile(att.getId());
                    }
                    attlist.remove(position);
                    adapter=new AttachmentAdapter(context,attlist,atlvw);
                    atlvw.setAdapter(adapter);
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