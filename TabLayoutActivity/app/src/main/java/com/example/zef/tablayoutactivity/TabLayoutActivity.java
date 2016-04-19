package com.example.zef.tablayoutactivity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.example.zef.iq.R;

/**
 * Created by zef on 4/4/16.
 */
public class TabLayoutActivity extends AppCompatActivity {

    ViewPager pager;

    static final int pageCount = 2;

    DummyView[] views = new DummyView[pageCount];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pager = (ViewPager) findViewById(R.id.pager);

        for(int i=0; i<pageCount;i++){
            DummyView view = new DummyView(this);
            view.setText("page no " + Integer.toString(i+1));
            views[i] = view;
        }
        TabAdapter mAdpter = new TabAdapter();
        pager.setAdapter(mAdpter);
    }

    private  class TabAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {;
            View v = views[position];
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "P=" + Integer.toString(position+1);
        }
    }
}
