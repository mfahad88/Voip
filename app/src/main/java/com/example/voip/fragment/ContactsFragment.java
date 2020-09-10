package com.example.voip.fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.voip.R;
import com.example.voip.models.Contacts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ContactsFragment extends Fragment {
    private View root;
    private ProgressBar progress_bar;
    private RecyclerView recycler_view;
    private List<Contacts> list;
    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getContactList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_contacts, container, false);
        initViews();

        recycler_view.setLayoutManager(new LinearLayoutManager(recycler_view.getContext()));
        recycler_view.addItemDecoration(new DividerItemDecoration(recycler_view.getContext(),RecyclerView.HORIZONTAL));


        /*recycler_view.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());

                return new ContactViewHolder(layoutInflater.inflate(R.layout.list_contact_row,parent,false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ContactViewHolder viewHolder=(ContactViewHolder)holder;
                viewHolder.txt_name.setText(list.get(position).getName());
                viewHolder.txt_number.setText(list.get(position).getPhone());
            }

            @Override
            public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                ContactViewHolder viewHolder=(ContactViewHolder)holder;
                Glide.with(viewHolder.itemView.getContext()).load(list.get(viewHolder.getAdapterPosition()).getImage()).circleCrop()
                        .into(viewHolder.image_contact);
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
            class ContactViewHolder extends RecyclerView.ViewHolder{
                private ImageView image_contact;
                private TextView txt_name;
                private TextView txt_number;
                public ContactViewHolder(@NonNull View itemView) {
                    super(itemView);
                    image_contact=itemView.findViewById(R.id.image_contact);
                    txt_name=itemView.findViewById(R.id.txt_name);
                    txt_number=itemView.findViewById(R.id.txt_number);
                }
            }
        });*/

        return root;
    }

    private void initViews() {
        progress_bar=root.findViewById(R.id.progress_bar);
        recycler_view=root.findViewById(R.id.recycler_view);
    }

    private void getContactList() {
        final List<Contacts> contacts=new ArrayList<>();

        new AsyncTask<Void,Void,List<Contacts>>(){

            @Override
            protected List<Contacts> doInBackground(Void... voids) {
                Bitmap bp= BitmapFactory.decodeResource(getContext().getResources(),
                        R.drawable.contact);
//                Bitmap bp = null;
                ContentResolver cr = getContext().getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

                if ((cur != null ? cur.getCount() : 0) > 0) {
                    while (cur != null && cur.moveToNext()) {
                        String id = cur.getString(
                                cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME));

                        if (cur.getInt(cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                            Cursor pCur = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id},  null);
                            while (pCur.moveToNext()) {
                                String phoneNo = pCur.getString(pCur.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                                String image_uri = pCur.getString(pCur.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                                if(image_uri !=null){
                                    try {
                                        bp= MediaStore.Images.Media
                                                .getBitmap(root.getContext().getContentResolver(),
                                                        Uri.parse(image_uri));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }

                                contacts.add(new Contacts(name,phoneNo,bp));
                            }
                            pCur.close();
                        }
                    }
                }
                if(cur!=null){
                    cur.close();
                }
                return contacts;
            }

            @Override
            protected void onPostExecute(final List<Contacts> contacts) {
                super.onPostExecute(contacts);

                recycler_view.setAdapter(new RecyclerView.Adapter() {
                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());

                        return new ContactViewHolder(layoutInflater.inflate(R.layout.list_contact_row,parent,false));
                    }

                    @Override
                    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
                        ContactViewHolder viewHolder=(ContactViewHolder)holder;
                        viewHolder.txt_name.setText(contacts.get(position).getName());
                        viewHolder.txt_number.setText(contacts.get(position).getPhone());
                        viewHolder.image_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getContext(), ""+contacts.get(position).getPhone(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
                        super.onViewAttachedToWindow(holder);
                        ContactViewHolder viewHolder=(ContactViewHolder)holder;
                        Glide.with(viewHolder.itemView.getContext()).load(contacts.get(viewHolder.getAdapterPosition()).getImage()).circleCrop()
                                .into(viewHolder.image_contact);
                    }

                    @Override
                    public int getItemCount() {
                        return contacts.size();
                    }
                    class ContactViewHolder extends RecyclerView.ViewHolder{
                        private ImageView image_contact,image_call;
                        private TextView txt_name;
                        private TextView txt_number;
                        public ContactViewHolder(@NonNull View itemView) {
                            super(itemView);
                            image_contact=itemView.findViewById(R.id.image_contact);
                            image_call=itemView.findViewById(R.id.image_call);
                            txt_name=itemView.findViewById(R.id.txt_name);
                            txt_number=itemView.findViewById(R.id.txt_number);
                        }
                    }
                });
                progress_bar.setVisibility(View.GONE);
                recycler_view.setVisibility(View.VISIBLE);
            }
        }.execute();
    }
}