package com.example.threatiq;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PhishingContentFragment extends Fragment {

    private static final String ARG_CONTENT = "content";
    private SpannableStringBuilder content;

    // Factory method to create a new instance of this fragment with content
    public static PhishingContentFragment newInstance(SpannableStringBuilder content) {
        PhishingContentFragment fragment = new PhishingContentFragment();
        Bundle args = new Bundle();
        args.putCharSequence(ARG_CONTENT, content); // SpannableStringBuilder is a CharSequence
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Retrieve the content from the bundle
            content = new SpannableStringBuilder(getArguments().getCharSequence(ARG_CONTENT));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phishing_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView contentTextView = view.findViewById(R.id.fragment_content_textview);
        if (content != null) {
            contentTextView.setText(content);
        }
    }
}
