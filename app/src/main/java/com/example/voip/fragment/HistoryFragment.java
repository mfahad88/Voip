package com.example.voip.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voip.R;
import com.example.voip.activity.ScreenAV;

import org.abtollc.sdk.AbtoApplication;
import org.abtollc.sdk.AbtoPhone;
import org.abtollc.utils.CallLog;

import java.util.List;

public class HistoryFragment extends Fragment {
    private View root;
    private RecyclerView recycler_view;
    private AbtoPhone abtoPhone;
    private List<CallLog> logs;
    public static String START_VIDEO_CALL = "START_VIDEO_CALL";
    private String domain;
    int accExpire;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_history, container, false);
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
        logs=abtoPhone.getCallLog(20);
        recycler_view=root.findViewById(R.id.recycler_view);
        recycler_view.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
                return new HistoryViewHolder(layoutInflater.inflate(R.layout.list_history_row,parent,false));
            }


            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                final HistoryViewHolder viewHolder=(HistoryViewHolder)holder;

                viewHolder.txt_phone_no.setText(logs.get(position).getNumber().substring(logs.get(position).getNumber().indexOf(":")+1,logs.get(position).getNumber().indexOf("@")));
                if(logs.get(position).isIncoming()){
                    viewHolder.image_call_icon.setImageDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.ic_baseline_call_received));
                }else{
                    viewHolder.image_call_icon.setImageDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.ic_baseline_call_outgoing));
                }

                viewHolder.txt_date.setText(logs.get(position).getDate().toString());
                viewHolder.image_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startCall(false,viewHolder.txt_phone_no.getText().toString());
                    }
                });
            }

            @Override
            public int getItemCount() {
                return logs.size();
            }

            class HistoryViewHolder extends RecyclerView.ViewHolder{
                private TextView txt_phone_no;
                private ImageView image_call_icon;
                private TextView txt_date;
                private ImageView image_call;

                public HistoryViewHolder(@NonNull View itemView) {
                    super(itemView);
                    txt_phone_no=itemView.findViewById(R.id.txt_phone_no);
                    image_call_icon=itemView.findViewById(R.id.image_call_icon);
                    txt_date=itemView.findViewById(R.id.txt_date);
                    image_call=itemView.findViewById(R.id.image_call);
                }
            }
        });
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
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