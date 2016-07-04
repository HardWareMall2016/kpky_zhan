package com.zhan.kykp.userCenter;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import com.zhan.kykp.R;

public class IndexActivity extends Activity implements View.OnClickListener {

    private ViewFlipper flipper;
    private float startX;
    private boolean canFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        initView();
        getActionBar().hide();
        setTranslucentStatus(true);
    }

    private void initView() {
        flipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        ImageView i1 = new ImageView(this);
        i1.setScaleType(ImageView.ScaleType.FIT_XY);
        i1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        i1.setImageResource(R.drawable.index1);
        ImageView i2 = new ImageView(this);
        i2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        i2.setImageResource(R.drawable.index2);
        i2.setScaleType(ImageView.ScaleType.FIT_XY);

        View inflate = View.inflate(this, R.layout.indexpage, null);
        inflate.findViewById(R.id.clickview).setOnClickListener(this);

        flipper.addView(i1);
        flipper.addView(i2);
        flipper.addView(inflate);
        flipper.setDisplayedChild(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (event.getX() > startX) { // 向右滑动
                    if (flipper.getDisplayedChild() != 0) {
                        flipper.setInAnimation(this, R.anim.slide_in_left);
                        flipper.setOutAnimation(this, R.anim.slide_out_right);
//                        flipper.showNext();
                        flipper.setDisplayedChild(flipper.getDisplayedChild() - 1);
                    }
                } else if (event.getX() < startX) { // 向左滑动
                    if (flipper.getDisplayedChild() != 2) {
                        flipper.setInAnimation(this, R.anim.slide_in_right);
                        flipper.setOutAnimation(this, R.anim.slide_out_left);
//                        flipper.showPrevious();
                        flipper.setDisplayedChild(flipper.getDisplayedChild() + 1);
                    }
                }
                break;
        }
            return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    /**
     *
     * @param on
     */
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |=  bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
