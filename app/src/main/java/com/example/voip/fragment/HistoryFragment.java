package com.example.voip.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.voip.R;

import org.abtollc.sdk.AbtoApplication;
import org.abtollc.sdk.AbtoPhone;
import org.abtollc.utils.CallLog;

import java.util.List;

public class HistoryFragment extends Fragment {
    private View root;
    private RecyclerView recycler_view;
    private AbtoPhone abtoPhone;
    private List<CallLog> logs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_history, container, false);
        abtoPhone = ((AbtoApplication) getActivity().getApplication()).getAbtoPhone();
        long accId = abtoPhone.getCurrentAccountId();

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
                HistoryViewHolder viewHolder=(HistoryViewHolder)holder;
                viewHolder.txt_phone_no.setText(logs.get(position).getNumber());
                if(logs.get(position).isIncoming()){
                    viewHolder.image_call_icon.setImageDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.ic_baseline_call_received));
                }else{
                    viewHolder.image_call_icon.setImageDrawable(holder.itemView.getContext().getResources().getDrawable(R.drawable.ic_baseline_call_outgoing));
                }

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
}