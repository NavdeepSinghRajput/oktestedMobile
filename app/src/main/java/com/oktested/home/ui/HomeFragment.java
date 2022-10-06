package com.oktested.home.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oktested.R;
import com.oktested.dashboard.ui.DashboardActivity;
import com.oktested.utils.DataHolder;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Context context;
    private TabLayout tabLayout;
    private CommunityFragment communityFragment = new CommunityFragment();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_home, container, false);

        ViewPager viewPager = root_view.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        tabLayout = root_view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
        setupTabIcons();
        return root_view;
    }

    private void setupTabIcons() {
        if (DataHolder.getInstance().getUserDataresponse.is_quiz_enable) {
            TextView tabOne = (TextView) LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
            tabOne.setText("Quizzes");
            tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_vector_new, 0, 0);
            tabLayout.getTabAt(0).setCustomView(tabOne);

            TextView tabTwo = (TextView) LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
            tabTwo.setText("Videos");
            tabLayout.getTabAt(1).setCustomView(tabTwo);

            TextView tabThree = (TextView) LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
            tabThree.setText("Community");
            tabLayout.getTabAt(2).setCustomView(tabThree);
        } else {
            TextView tabTwo = (TextView) LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
            tabTwo.setText("Videos");
            tabLayout.getTabAt(0).setCustomView(tabTwo);

            TextView tabThree = (TextView) LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
            tabThree.setText("Community");
            tabLayout.getTabAt(1).setCustomView(tabThree);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        if (DataHolder.getInstance().getUserDataresponse.is_quiz_enable) {
            adapter.addFragment(new QuizFragment(), "Quizzes");
        }
        adapter.addFragment(new VideosFragment(), "Videos");
        adapter.addFragment(communityFragment, "Community");
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0 || position == 1) {
                    communityFragment.pausePlayer();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}