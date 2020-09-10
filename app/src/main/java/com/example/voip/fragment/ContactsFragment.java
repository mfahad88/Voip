package com.example.voip.fragment;

import android.content.ContentResolver;
import android.content.Intent;
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
import android.text.TextUtils;
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
import com.example.voip.activity.ScreenAV;
import com.example.voip.models.Contacts;

import org.abtollc.sdk.AbtoApplication;
import org.abtollc.sdk.AbtoPhone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ContactsFragment extends Fragment {
    private View root;
    private ProgressBar progress_bar;
    private RecyclerView recycler_view;
    private AbtoPhone abtoPhone;
    public static String START_VIDEO_CALL = "START_VIDEO_CALL";
    private String domain;
    int accExpire;
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
        abtoPhone = ((AbtoApplication) getActivity().getApplication()).getAbtoPhone();
        long accId = abtoPhone.getCurrentAccountId();
        accExpire = abtoPhone.getConfig().getAccountExpire(accId);
        String contact = abtoPhone.getConfig().getAccount(accId).acc_id;
        contact = contact.replace("<", "");
        contact = contact.replace(">", "");

        if (accExpire == 0)  {
            domain = "";
        }
        else  {
            domain = abtoPhone.getConfig().getAccountDomain(accId);
        }


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
//                Bitmap bp= BitmapFactory.decodeResource(getContext().getResources(),
//                        R.drawable.contact);

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



                                contacts.add(new Contacts(name,phoneNo,image_uri));
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
                                String phoneNo = contacts.get(position).getPhone();
                                if(contacts.get(position).getPhone().startsWith("+")){
                                    phoneNo=contacts.get(position).getPhone().replace("+","");
                                }else if(contacts.get(position).getPhone().startsWith("00")){
                                    phoneNo=contacts.get(position).getPhone().substring(2,contacts.get(position).getPhone().length());
                                }
                                startCall(false,phoneNo);
                            }
                        });
                    }

                    @Override
                    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
                        super.onViewAttachedToWindow(holder);
                        ContactViewHolder viewHolder=(ContactViewHolder)holder;
                        if(contacts.get(viewHolder.getAdapterPosition()).getImage()!=null) {
                            try {
                                Bitmap bp=
                                        MediaStore.Images.Media
                                                .getBitmap(root.getContext().getContentResolver(),
                                                        Uri.parse(contacts.get(viewHolder.getAdapterPosition()).getImage()));
                                Glide.with(viewHolder.itemView.getContext()).load(bp).circleCrop()
                                        .into(viewHolder.image_contact);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }else{
                            Glide.with(viewHolder.itemView.getContext()).load(R.drawable.contact).circleCrop()
                                    .into(viewHolder.image_contact);
                        }
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
                recycler_view.setLayoutManager(new LinearLayoutManager(recycler_view.getContext()));
                recycler_view.addItemDecoration(new DividerItemDecoration(recycler_view.getContext(),RecyclerView.HORIZONTAL));
            }
        }.execute();
    }

    public void startCall(boolean bVideo,String sipNumber)   {

        //Check empty

        if(sipNumber.isEmpty())  return;

        //Verify direct call mode
        if(TextUtils.isEmpty(domain) && !sipNumber.contains("@") ) {
            Toast.makeText(getContext(), "Specify remote side address as 'number@domain:port'", Toast.LENGTH_SHORT).show();
            return;
        }

        //Build address to dial
        StringBuffer buildString = new StringBuffer();
        buildString.append(sipNumber);

        //Append domain (if required)
        if(!sipNumber.contains("@")) {
            buildString.append("@");
            buildString.append(domain);
        }
/*
        //Append headers
        try {
            buildString.append("?");
            buildString.append("header1=");
            buildString.append(URLEncoder.encode("qq<q>q", "UTF-8"));//value of 'header1'

            buildString.append("&");

            buildString.append("header2=");
            buildString.append(URLEncoder.encode("a@b", "UTF-8"));//value of 'header2'
            //=======================
            buildString.append("?");
            buildString.append("X-AccountId=");
            buildString.append(URLEncoder.encode("2223344", "UTF-8"));//value of 'header1'

            buildString.append("&");

            buildString.append("X-Geolocation=");
            buildString.append(URLEncoder.encode("<geo:43.6665599,-79.3791219;timestamp=20200317094000>", "UTF-8"));//value of 'header2'

        }
        catch (UnsupportedEncodingException e)
        {
        }*/

        Intent intent = new Intent(getContext(), ScreenAV.class);
        intent.putExtra(AbtoPhone.IS_INCOMING, false);//!
        intent.putExtra(AbtoPhone.REMOTE_CONTACT, buildString.toString());
        intent.putExtra(START_VIDEO_CALL, bVideo);
        startActivity(intent);
    }
}