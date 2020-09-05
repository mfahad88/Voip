package com.example.voip.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voip.R;


public class DialPadFragment extends Fragment implements View.OnClickListener {

    private View root;
    private TextView txt_dial_number;
    private LinearLayout linear_one;
    private LinearLayout linear_two;
    private LinearLayout linear_three;
    private LinearLayout linear_four;
    private LinearLayout linear_five;
    private LinearLayout linear_six;
    private LinearLayout linear_seven;
    private LinearLayout linear_eight;
    private LinearLayout linear_nine;
    private LinearLayout linear_zero;
    private LinearLayout linear_star;
    private LinearLayout linear_hash;
    private LinearLayout linear_call;
    private ImageView image_remove;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_dial_pad, container, false);
        initViews();
        setListener();
        return root;
    }

    private void setListener() {
        linear_hash.setOnClickListener(this);
        linear_star.setOnClickListener(this);
        linear_call.setOnClickListener(this);
        linear_zero.setOnClickListener(this);
        linear_one.setOnClickListener(this);
        linear_two.setOnClickListener(this);
        linear_three.setOnClickListener(this);
        linear_four.setOnClickListener(this);
        linear_five.setOnClickListener(this);
        linear_six.setOnClickListener(this);
        linear_seven.setOnClickListener(this);
        linear_eight.setOnClickListener(this);
        linear_nine.setOnClickListener(this);
        image_remove.setOnClickListener(this);
    }

    private void initViews() {
        txt_dial_number=root.findViewById(R.id.txt_dial_number);
        linear_call=root.findViewById(R.id.linear_call);
        linear_star=root.findViewById(R.id.linear_star);
        linear_hash=root.findViewById(R.id.linear_hash);
        linear_zero=root.findViewById(R.id.linear_zero);
        linear_one=root.findViewById(R.id.linear_one);
        linear_two=root.findViewById(R.id.linear_two);
        linear_three=root.findViewById(R.id.linear_three);
        linear_four=root.findViewById(R.id.linear_four);
        linear_five=root.findViewById(R.id.linear_five);
        linear_six=root.findViewById(R.id.linear_six);
        linear_seven=root.findViewById(R.id.linear_seven);
        linear_eight=root.findViewById(R.id.linear_eight);
        linear_nine=root.findViewById(R.id.linear_nine);
        image_remove=root.findViewById(R.id.image_remove);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.linear_call:
                break;

            case R.id.linear_star:
                txt_dial_number.append("*");
                break;

            case R.id.linear_hash:
                txt_dial_number.append("#");
                break;

            case R.id.linear_zero:
                txt_dial_number.append("0");
                break;

            case R.id.linear_one:
                txt_dial_number.append("1");
                break;

            case R.id.linear_two:
                txt_dial_number.append("2");
                break;

            case R.id.linear_three:
                txt_dial_number.append("3");
                break;

            case R.id.linear_four:
                txt_dial_number.append("4");
                break;

            case R.id.linear_five:
                txt_dial_number.append("5");
                break;

            case R.id.linear_six:
                txt_dial_number.append("6");
                break;

            case R.id.linear_seven:
                txt_dial_number.append("7");
                break;

            case R.id.linear_eight:
                txt_dial_number.append("8");
                break;

            case R.id.linear_nine:
                txt_dial_number.append("9");
                break;

            case R.id.image_remove:
                if(txt_dial_number.length()>0)
                    txt_dial_number.setText(txt_dial_number.getText().toString().substring(0,txt_dial_number.getText().length()-1));
                break;

        }
    }
}
