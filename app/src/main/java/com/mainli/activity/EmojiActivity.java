package com.mainli.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mainli.view.EmojisView;
import com.seekting.demo_lib.Demo;

/**
 * Created by lixiaoliang on 2018-4-26.
 */
@Demo(title = "Emoji表情", group = {"View"})
public class EmojiActivity extends AppCompatActivity implements EmojisView.OnEmojiListener {
    private String[][] emojis1 = {//
            {"\ud83d\ude04", "\ud83d\ude02", "\ud83d\ude43", "\ud83d\ude18", "\ud83d\ude0d", "\ud83d\ude1b", "\ud83e\udd11"}, //
            {"\ud83d\ude0e", "\ud83d\ude0f", "\ud83d\ude12", "\ud83d\ude21", "\ud83e\udd24", "\ud83d\ude24", "\ud83e\udd21"}, //
            {"\ud83d\ude36", "\ud83d\ude31", "\ud83d\ude33", "\ud83d\ude30", "\ud83d\ude2d", "\ud83e\udd22", null}};//ud83eudd10 null为删除占位符
    private String[][] emojis2 = {//
            {"\ud83d\ude44", "\ud83e\udd25", "\ud83e\udd12", "\ud83d\ude34", "\ud83d\ude07", "\ud83e\udd14", "\ud83d\udc7b"}, //
            {"\ud83c\udf1a", "\ud83c\udf1d", "\ud83d\ude08", "\ud83e\udd37", "\ud83d\udc4d", "\u270c", "\ud83d\udcaa"},//
            {"\ud83d\udc4f", "\ud83d\ude4f", "\ud83e\udd1d", "\ud83d\udc79", "\ud83d\udca9", "\ud83d\ude48", "\ud83d\udc12"}};
    private String[][] emojis3 = {//
            {"\u2620\ufe0f", "\ud83d\ude40", "\ud83d\udc7d", "\ud83c\udf83", "\ud83d\udc32", "\ud83d\udeeb", "\ud83d\ude80"}, //
            {"\ud83d\udea6", "\u274c", "\u2b55", "\ud83d\udca2", "\ud83d\udcaf", "\ud83d\udc8b", "\ud83c\udf7b"},//
            {"\ud83c\udfc5", "\ud83c\udf08", "\ud83d\udc8d", "\ud83c\udfb0", "\ud83d\udc94", "\ud83d\udd25", "\ud83c\udf82"}};
    private EditText mEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final EmojisView emojiView1 = new EmojisView(this);
        emojiView1.setOnSelectEmoji(this);
        emojiView1.setEmojis(emojis1);
        final EmojisView emojiView2 = new EmojisView(this);
        emojiView2.setOnSelectEmoji(this);
        emojiView2.setEmojis(emojis2);
        final EmojisView emojiView3 = new EmojisView(this);
        emojiView3.setOnSelectEmoji(this);
        emojiView3.setEmojis(emojis3);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                switch (position) {
                    case 0:
                        container.addView(emojiView1);
                        return emojiView1;
                    case 1:
                        container.addView(emojiView2);
                        return emojiView2;
                    case 2:
                        container.addView(emojiView3);
                        return emojiView3;
                }
                return null;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
        });
        int widthPixels = getResources().getDisplayMetrics().widthPixels;

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        mEditText = new EditText(this);
        mEditText.setMaxLines(1);
        mEditText.setSingleLine();
        linearLayout.addView(mEditText);
        linearLayout.addView(viewPager);
        setContentView(linearLayout, new FrameLayout.LayoutParams(widthPixels, widthPixels >> 1));
    }

    @Override
    public void onInsertEmoji(String emojiUnicode) {
        int selectionStart = mEditText.getSelectionStart();
        mEditText.getText().insert(selectionStart, emojiUnicode);
    }

    @Override
    public void onDeleteTheEmojiBefore() {
        int keyCode = KeyEvent.KEYCODE_DEL;
        KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
        mEditText.onKeyDown(keyCode, keyEventDown);
        mEditText.onKeyUp(keyCode, keyEventUp);
    }
}
