package com.cherry.lib.doc.office.ss.sheetbar;

import android.content.Context;
import androidx.appcompat.widget.AppCompatButton;

/**
 * Custom SheetButton (không dùng Drawable)
 */
public class SheetButton extends AppCompatButton {

    private int sheetIndex;
    private boolean isFocused = false;

    public SheetButton(Context context, String name, int index) {
        super(context);
        this.sheetIndex = index;
        setText(name);
        setTextColor(0xFF000000);
        setBackgroundColor(0xFFFFFFFF);
        setPadding(0, 0, 0, 0);
    }

    public void changeFocus(boolean focus) {
        isFocused = focus;
        if (isFocused) {
            setBackgroundColor(0xFF3399FF); // màu khi focus
            setTextColor(0xFFFFFFFF);
        } else {
            setBackgroundColor(0xFFFFFFFF); // màu bình thường
            setTextColor(0xFF000000);
        }
    }

    public int getSheetIndex() {
        return sheetIndex;
    }

    public void dispose() {
        // Có thể clear resources nếu cần
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
