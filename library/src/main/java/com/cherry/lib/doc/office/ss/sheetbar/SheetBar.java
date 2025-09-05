package com.cherry.lib.doc.office.ss.sheetbar;

import java.util.Vector;

import com.cherry.lib.doc.office.constant.EventConstant;
import com.cherry.lib.doc.office.system.IControl;

import android.content.Context;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Custom SheetBar (không dùng Drawable)
 */
public class SheetBar extends HorizontalScrollView implements View.OnClickListener {

    private int minimumWidth;
    private int sheetbarHeight;
    private SheetButton currentSheet;
    private IControl control;
    private LinearLayout sheetbarFrame;

    public SheetBar(Context context) {
        super(context);
    }

    public SheetBar(Context context, IControl control, int minimumWidth) {
        super(context);
        this.control = control;
        this.setVerticalFadingEdgeEnabled(false);
        this.setFadingEdgeLength(0);

        if (minimumWidth == getResources().getDisplayMetrics().widthPixels) {
            this.minimumWidth = -1;
        } else {
            this.minimumWidth = minimumWidth;
        }
        init();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        sheetbarFrame.setMinimumWidth(minimumWidth == -1 ? getResources().getDisplayMetrics().widthPixels
                : minimumWidth);
    }

    private void init() {
        Context context = getContext();

        // Frame layout chứa toàn bộ sheetbar
        sheetbarFrame = new LinearLayout(context);
        sheetbarFrame.setGravity(Gravity.BOTTOM);
        sheetbarFrame.setOrientation(LinearLayout.HORIZONTAL);
        sheetbarFrame.setMinimumWidth(minimumWidth == -1 ? getResources().getDisplayMetrics().widthPixels : minimumWidth);

        // Sheetbar height
        sheetbarHeight = dpToPx(40);
        sheetbarFrame.setBackgroundColor(0xFFEEEEEE); // màu nền

        // Left shadow
        View left = new View(context);
        LinearLayout.LayoutParams shadowParams = new LinearLayout.LayoutParams(dpToPx(5), sheetbarHeight);
        left.setBackgroundColor(0x55000000); // shadow mờ
        sheetbarFrame.addView(left, shadowParams);

        // Sheet buttons
        @SuppressWarnings("unchecked")
        Vector<String> vec = (Vector<String>) control.getActionValue(EventConstant.SS_GET_ALL_SHEET_NAME, null);
        int count = vec.size();
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, sheetbarHeight);

        for (int i = 0; i < count; i++) {
            SheetButton sb = new SheetButton(context, vec.get(i), i);
            if (currentSheet == null) {
                currentSheet = sb;
                currentSheet.changeFocus(true);
            }
            sb.setOnClickListener(this);
            sheetbarFrame.addView(sb, btnParams);

            if (i < count - 1) {
                // Separator
                View separator = new View(context);
                separator.setBackgroundColor(0xFFCCCCCC);
                LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(dpToPx(1), sheetbarHeight);
                sheetbarFrame.addView(separator, sepParams);
            }
        }

        // Right shadow
        View right = new View(context);
        right.setBackgroundColor(0x55000000);
        sheetbarFrame.addView(right, shadowParams);

        // Add vào ScrollView
        addView(sheetbarFrame, new LayoutParams(LayoutParams.WRAP_CONTENT, sheetbarHeight));
    }

    @Override
    public void onClick(View v) {
        if (currentSheet != null) {
            currentSheet.changeFocus(false);
        }

        SheetButton sb = (SheetButton) v;
        sb.changeFocus(true);
        currentSheet = sb;

        control.actionEvent(EventConstant.SS_SHOW_SHEET, currentSheet.getSheetIndex());
    }

    public void setFocusSheetButton(int index) {
        if (currentSheet != null && currentSheet.getSheetIndex() == index) {
            return;
        }

        int count = sheetbarFrame.getChildCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            view = sheetbarFrame.getChildAt(i);
            if (view instanceof SheetButton && ((SheetButton) view).getSheetIndex() == index) {
                if (currentSheet != null) currentSheet.changeFocus(false);
                currentSheet = (SheetButton) view;
                currentSheet.changeFocus(true);
                break;
            }
        }

        // Scroll nếu button nằm ngoài màn hình
        int screenWidth = control.getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int barWidth = sheetbarFrame.getWidth();
        if (barWidth > screenWidth && view != null) {
            int left = view.getLeft();
            int right = view.getRight();
            int off = (screenWidth - (right - left)) / 2;
            off = left - off;
            if (off < 0) off = 0;
            else if (off + screenWidth > barWidth) off = barWidth - screenWidth;
            scrollTo(off, 0);
        }
    }

    public int getSheetbarHeight() {
        return sheetbarHeight;
    }

    public void dispose() {
        currentSheet = null;
        if (sheetbarFrame != null) {
            int count = sheetbarFrame.getChildCount();
            for (int i = 0; i < count; i++) {
                View v = sheetbarFrame.getChildAt(i);
                if (v instanceof SheetButton) {
                    ((SheetButton) v).dispose();
                }
            }
            sheetbarFrame = null;
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
