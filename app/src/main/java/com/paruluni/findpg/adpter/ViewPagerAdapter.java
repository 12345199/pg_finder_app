package com.paruluni.findpg.adpter;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by UddishVerma on 03/09/16.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragment = new ArrayList<>();
    ArrayList title = new ArrayList<>();

    public void addfragment (Fragment fragment, String title) {
        this.fragment.add(fragment);
        this.title.add(title);
    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragment.get(position);
    }

    @Override
    public int getCount() {
        return fragment.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (CharSequence) title.get(position);
    }
}
