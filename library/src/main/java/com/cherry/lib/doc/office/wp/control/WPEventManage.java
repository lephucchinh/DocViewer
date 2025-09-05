package com.cherry.lib.doc.office.wp.control;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cherry.lib.doc.office.common.hyperlink.Hyperlink;
import com.cherry.lib.doc.office.common.picture.PictureKit;
import com.cherry.lib.doc.office.constant.EventConstant;
import com.cherry.lib.doc.office.constant.wp.WPViewConstant;
import com.cherry.lib.doc.office.java.awt.Rectangle;
import com.cherry.lib.doc.office.simpletext.model.AttrManage;
import com.cherry.lib.doc.office.simpletext.model.IElement;
import com.cherry.lib.doc.office.system.IControl;
import com.cherry.lib.doc.office.system.beans.AEventManage;

public class WPEventManage extends AEventManage {

    protected Word word;
    private int oldX = 0;
    private int oldY = 0;
    private static final String TAG = "WPEventManage";

    public WPEventManage(Word word, IControl control) {
        super(word.getContext(), control);
        this.word = word;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            super.onTouch(v, event);
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    PictureKit.instance().setDrawPictrue(true);
                    break;
                case MotionEvent.ACTION_UP:
                    if (zoomChange) {
                        zoomChange = false;
                        // Reset scroller để scroll mượt sau zoom
                        if (mScroller != null && !mScroller.isFinished()) {
                            mScroller.abortAnimation();
                        }

                        Rectangle r = word.getVisibleRect();
                        oldX = r.x;
                        oldY = r.y;

                        if (word.getCurrentRootType() == WPViewConstant.PAGE_ROOT) {
                            control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
                        }
                        if (control.getMainFrame().isZoomAfterLayoutForWord()) {
                            control.actionEvent(EventConstant.WP_LAYOUT_NORMAL_VIEW, null);
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.d(TAG, "onTouch: " + e.getMessage());
            control.getSysKit().getErrorKit().writerLog(e);
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (word.getStatus().isSelectTextStatus()) return true;

        super.onScroll(e1, e2, distanceX, distanceY);

        Rectangle r = word.getVisibleRect();
        float zoom = word.getZoom();
        int wW = (int) (word.getWordWidth() * zoom);
        int wH = (int) (word.getWordHeight() * zoom);

        int sX = r.x;
        int sY = r.y;

        boolean isScrollX = Math.abs(distanceX) > Math.abs(distanceY);

        if (isScrollX) {
            sX += distanceX;
            if (sX < 0) sX = 0;
            if (sX + r.width > wW) sX = wW - r.width;
        } else {
            sY += distanceY;
            if (sY < 0) sY = 0;
            if (sY + r.height > wH) sY = wH - r.height;
        }

        word.scrollTo(sX, sY);
        return true;
    }

    @Override
    public void fling(int velocityX, int velocityY) {
        Rectangle r = word.getVisibleRect();
        float zoom = word.getZoom();
        int wW = (int) (word.getWordWidth() * zoom);
        int wH = (int) (word.getWordHeight() * zoom);

        oldX = r.x;
        oldY = r.y;

        if (Math.abs(velocityX) > Math.abs(velocityY)) {
            mScroller.fling(r.x, r.y, velocityX, 0, 0, wW - r.width, r.y, r.y);
        } else {
            mScroller.fling(r.x, r.y, 0, velocityY, r.x, r.x, 0, wH - r.height);
        }

        word.postInvalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            int sX = mScroller.getCurrX();
            int sY = mScroller.getCurrY();
            word.scrollTo(sX, sY);
            word.postInvalidate();
        }
    }

    protected int convertCoorForX(float x) {
        return (int) ((x + word.getScrollX()) / word.getZoom());
    }

    protected int convertCoorForY(float y) {
        return (int) ((y + word.getScrollY()) / word.getZoom());
    }

    public void dispose() {
        super.dispose();
        word = null;
    }
}
