package com.malta_mqf.malta_mobile.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.malta_mqf.malta_mobile.CancelFragment;
import com.malta_mqf.malta_mobile.HistoryFragment;
import com.malta_mqf.malta_mobile.UpdateFragment;

public class CustomPagerAdapter extends FragmentStateAdapter {
    private String selectedDate;

    public CustomPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public CustomPagerAdapter(@NonNull FragmentActivity fragmentActivity, String selectedDate) {
        super(fragmentActivity);
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HistoryFragment();
            case 1:
                return new UpdateFragment();
            case 2:
                return new CancelFragment();
            default:
                return null;
        }
    }


    @Override
    public int getItemCount() {
        return 3; // Number of tabs
    }
}

