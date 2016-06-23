package com.cefy.cefy.activities;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.cefy.cefy.R;
import com.cefy.cefy.fragments.StartFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * @author Anurag
 */
public class StartActivity extends AppCompatActivity {

    private static final int PAGER_TRANSITION_DURATION_MS = 400;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int page;
    private ArgbEvaluator evaluator;

    @BindView(R.id.container) ViewPager viewPager;
    @BindView(R.id.intro_btn_finish) Button finishBtn;
    @BindView(R.id.btn_terms) Button termsBtn;
    @BindView(R.id.btn_privacy) Button privacyBtn;
    @BindView(R.id.intro_btn_next) ImageButton nextBtn;
    @BindView(R.id.intro_indicator_0) ImageView indicator0;
    @BindView(R.id.intro_indicator_1) ImageView indicator1;
    @BindView(R.id.intro_indicator_2) ImageView indicator2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fabric.with(this, new Crashlytics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mSectionsPagerAdapter);
        setUpViewPager();
    }

    private void setUpViewPager() {
        final int color1 = ContextCompat.getColor(this, R.color.cyan);
        final int color2 = ContextCompat.getColor(this, R.color.orange);
        final int color3 = ContextCompat.getColor(this, R.color.green);
        final int[] colorList = new int[]{color1, color2, color3};
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                evaluator = new ArgbEvaluator();
                int colorUpdate = (Integer) evaluator.evaluate(positionOffset, colorList[position], colorList[position == 2 ? position : position + 1]);
                viewPager.setBackgroundColor(colorUpdate);
            }
            @Override
            public void onPageSelected(int position) {
                page = position;
                updateIndicators(page);
                switch (position) {
                    case 0:
                        viewPager.setBackgroundColor(color1);
                        break;
                    case 1:
                        viewPager.setBackgroundColor(color2);
                        break;
                    case 2:
                        viewPager.setBackgroundColor(color3);
                        break;
                }
                nextBtn.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
                finishBtn.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animatePagerTransition(true);
            }
        });
    }

    private void updateIndicators(int position) {
        ImageView[] indicators = { indicator0, indicator1, indicator2 };
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return StartFragment.newInstance(R.drawable.ic_credit_cards, getString(R.string.desc1));
                case 1:
                    return StartFragment.newInstance(R.drawable.ic_wallet, getString(R.string.desc2));
                case 2:
                    return StartFragment.newInstance(R.drawable.ic_time_is_money, getString(R.string.desc3));
            }
            return StartFragment.newInstance(R.drawable.ic_time_is_money, getString(R.string.desc3));
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private void animatePagerTransition(final boolean forward) {

        ValueAnimator animator = ValueAnimator.ofInt(0, viewPager.getWidth());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                viewPager.endFakeDrag();
                page += 1;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                viewPager.endFakeDrag();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private int oldDragPosition = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int dragPosition = (Integer) animation.getAnimatedValue();
                int dragOffset = dragPosition - oldDragPosition;
                oldDragPosition = dragPosition;
                viewPager.fakeDragBy(dragOffset * (forward ? -1 : 1));
            }
        });

        animator.setDuration(PAGER_TRANSITION_DURATION_MS);
        viewPager.beginFakeDrag();
        animator.start();
    }

    @OnClick(R.id.btn_terms)
    public void openTermsOfUse(View view) {
        openURL("http://cefy.in/tos");
    }

    @OnClick(R.id.btn_privacy)
    public void openPrivacyPolicy(View view) {
        openURL("http://cefy.in/privacy");
    }

    private void openURL(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

}
