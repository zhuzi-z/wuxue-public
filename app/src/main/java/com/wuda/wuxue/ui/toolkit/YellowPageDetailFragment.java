package com.wuda.wuxue.ui.toolkit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Contact;


public class YellowPageDetailFragment extends ToolFragment {

    private Contact contact;
    private TextView titleTextView;
    private TextView phoneNumberTextView;
    private Button callButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (tool != null && tool instanceof Contact) {
            contact = (Contact) tool;
        } else {
            requireActivity().onBackPressed();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yellow_page_detail, container, false);

        titleTextView = view.findViewById(R.id.yellowPage_title_textView);
        phoneNumberTextView = view.findViewById(R.id.yellowPage_phoneNumber_textView);
        callButton = view.findViewById(R.id.yellowPage_call_button);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contact.getPhoneNumber()));
                startActivity(intent);
            }
        });

        showContent();

        return view;
    }

    private void showContent() {
        titleTextView.setText(contact.getName());
        phoneNumberTextView.setText(contact.getPhoneNumber());
        closeProgressBar();
    }
}