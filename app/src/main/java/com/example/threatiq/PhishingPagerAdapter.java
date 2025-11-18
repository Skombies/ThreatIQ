package com.example.threatiq;

import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class PhishingPagerAdapter extends FragmentStateAdapter {

    private final List<SpannableStringBuilder> pages;

    public PhishingPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<SpannableStringBuilder> pages) {
        super(fragmentActivity);
        this.pages = pages;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return PhishingContentFragment.newInstance(pages.get(position));
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }
}

