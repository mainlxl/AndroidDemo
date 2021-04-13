# 针对EditText的Markdown链接式文本编辑
* **静态效果图:**
![](https://i.imgur.com/y2hmnZ5.png)
* **最终转换为文本Markdown格式上传服务器:**
`发一个帖子连接,带一个,点击[连接2](http://www.cmcm.com/zh-cn/)查看详情`

---




### 效果演示:
<video width="320" height="640" controls>
  <source src="效果.mp4" type="video/mp4">
</video>

### 1. 光标处插入

```java
public class LinkedEditText extends AppCompatEditText {
	...省略代码
	public void insertLinked(String name, String url) {
        insertMDLinked(getSelectionEnd(), name, url, convertMDLinked(name, url));
    }

    @NonNull
    private String convertMDLinked(String name, String url) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        stringBuilder.append(name);
        stringBuilder.append(']');
        stringBuilder.append('(');
        stringBuilder.append(url);
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    private void insertMDLinked(int where, String name, String url, String mdLinked) {
        Editable text = getText();
        LinkeSpan span = new LinkeSpan(getContext(),name, url, mdLinked);
        text.insert(where, name);
		/**
    	 * Spans of type SPAN_EXCLUSIVE_EXCLUSIVE do not expand
    	 * to include text inserted at either their starting or ending point.
    	 * They can never have a length of 0 and are automatically removed
    	 * from the buffer if all the text they cover is removed.
    	 * SPAN_EXCLUSIVE_EXCLUSIVE类型的跨度不会展开
    	 * 以包括在其起点或终点插入的文本。
    	 * 它们的长度永远不会为0，如果删除了所有文本，
    	 * 它们将自动从缓冲区中删除。
    	 */
        text.setSpan(span, where, where + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		//-----------------------下面这行后面介绍----------------------------------
        text.setSpan(new ReplacementSelectSpan(), where, where + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
	...省略代码
}

```

### 2. LinkeSpan实现

```java
private static class LinkeSpan extends ReplacementSpan implements Comparable<LinkeSpan> {
	...省略代码

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            return (int) (dp32 + dp4 + paint.measureText(urlName) + 0.5F);
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            sDrawableLiked.setBounds((int) (x + dp8), top + ((bottom - top - sDrawableLiked.getMinimumHeight()) >> 1), (int) (x + dp16 + dp8), bottom);
            sDrawableLiked.draw(canvas);
            int color = paint.getColor();
            paint.setColor(0xFF687FFF);
            canvas.drawText(this.urlName, x + dp32, (float) y, paint);
            paint.setColor(color);
        }
	...省略代码
    }

```

### 3. 转换为Markdown连接式文本

```java
public String toMDString() {
        Editable text = getText();
        StringBuffer stringBuffer = new StringBuffer();
		//获取LinkeSpan是按照加入顺序排序,并非文本中位置顺序
        LinkeSpan[] linkes = text.getSpans(0, text.length(), LinkeSpan.class);
        for (LinkeSpan linkeSpan : linkes) {
            linkeSpan.index = text.getSpanStart(linkeSpan);
        }
        Arrays.sort(linkes);//文本中位置顺序
        char[] tmp;
        int start = 0;
        for (LinkeSpan linke : linkes) {
            int charCount = linke.index - start;
            int linkeNameSize = linke.urlName.length();
            if (charCount > 0) {
                tmp = new char[charCount];
                text.getChars(start, linke.index, tmp, 0);
                stringBuffer.append(tmp);
                stringBuffer.append(linke.toString());
                start = linke.index + linkeNameSize;
            } else if (charCount == 0) {
                stringBuffer.append(linke.toString());
                start += linkeNameSize;
            }
        }


        //补足剩余字符
        int length = text.length();
        if (start < length) {
            tmp = new char[length - start];
            text.getChars(start, length, tmp, 0);
            stringBuffer.append(tmp);
        }
        return stringBuffer.toString();
    }
```

### 4. Markdown连接回转显示

```java
public class MarkDownURLMatcher {
    private final static String URL_NAME = "[\\w \\(\\)\\t#&%$@\\u4e00-\\u9fa5]*";
    private final static String HTTP = "(https?|ftp|file)://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
    private final static String MATCHER = "\\[(" + URL_NAME + ")\\]\\((" + HTTP + ")\\)";
    private final static Pattern MD_URL_MATCHER = Pattern.compile(MATCHER);
	...省略代码

	/**
     * 收集文本中markdown链接 转换为SpannerString
     * 格式:[xxx](xxxx)
     */
    private static final SpannableStringBuilder convertTextLinks(String text) {
        Matcher m = MD_URL_MATCHER.matcher(text);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        int lastIndex = 0;
        while (m.find()) {
            String name = m.group(1);
            String url = m.group(2);
            if (name == null) {
                name = "";
            }
            lastIndex = convertLinkedSpan(text, m, spannableStringBuilder, lastIndex, name, url);
        }
        if (lastIndex == 0) {
            spannableStringBuilder.append(text);
        } else if (text.length() > lastIndex) {
            appendEndText(spannableStringBuilder, text.substring(lastIndex, text.length()));
        } else {
            spannableStringBuilder.append(" ");//防止点击事件延长至整行
        }
        return spannableStringBuilder;
    }

	private static int convertLinkedSpan(String text, Matcher m, SpannableStringBuilder spannableStringBuilder, int lastIndex, String name, String url) {
        int start = m.start();
        String substring = text.substring(lastIndex, start);
        spannableStringBuilder.append(substring);
        int urlTextStart = spannableStringBuilder.length();
        spannableStringBuilder.append(name);
		//ClickLinkeSpan仿照LinkeSpan并添加Click实现
        spannableStringBuilder.setSpan(new ClickLinkeSpan(name, url), urlTextStart, urlTextStart + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        lastIndex = m.end();
        return lastIndex;
    }


	private static class ClickLinkeSpan extends ReplacementSpan implements SpanClickable, NoCopySpan {
        private String url;
        ...省略代码

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
                ...省略代码
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
              ...省略代码
        }

        @Override
        public void onClick(View widget) {
            Uri uri = Uri.parse(url);
            Context context = widget.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.w("LinkeSpan", "Actvity was not found for intent, " + intent.toString());
            }
        }
    }

}
```

### 5. 点击效果实现(没有使用TextView的setMovementMethod()添加MovementMethod实现)

```java
public interface SpanClickable {
        void onClick(View widget);
}

public static class ClickableSpanListener implements View.OnTouchListener {
        private static ClickableSpanListener instance = new ClickableSpanListener();
        public static ClickableSpanListener getInstantce() {
            return instance;
        }
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            boolean ret = false;
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                    CharSequence text = textView.getText();
                    if (text instanceof Spanned) {
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        x -= textView.getTotalPaddingLeft();
                        y -= textView.getTotalPaddingTop();
                        x += textView.getScrollX();
                        y += textView.getScrollY();
                        Layout layout = textView.getLayout();
                        int line = layout.getLineForVertical(y);
                        int off = layout.getOffsetForHorizontal(line, x);
                        Spanned spannable = (Spanned) text;
                        SpanClickable[] link = spannable.getSpans(off, off, SpanClickable.class);
                        if (link != null && link.length > 0 && link[0] != null) {
                            if (action == MotionEvent.ACTION_UP) {
                                link[0].onClick(textView);
                            }
                            ret = true;
                        }
                    }
                }
            }
            return ret;
        }
    }
```

### 6.开发中遇到的棘手问题

* **选择BUG**
###### BUG展示:

<video width="320" height="640" controls>
  <source src="选择BUG.mp4" type="video/mp4">
</video>

通过代码跟踪最终找到选择是通过`android.widget.Editor`的`private boolean selectCurrentWord()`方法

```java
private boolean selectCurrentWord() {
	...省略代码
        // If a URLSpan (web address, email, phone...) is found at that position, select it.
        URLSpan[] urlSpans = ((Spanned) mTextView.getText()).
                getSpans(minOffset, maxOffset, URLSpan.class);
        if (urlSpans.length >= 1) {//这里有限判断URLSpan如果有有限选中全部文本
            URLSpan urlSpan = urlSpans[0];
            selectionStart = ((Spanned) mTextView.getText()).getSpanStart(urlSpan);
            selectionEnd = ((Spanned) mTextView.getText()).getSpanEnd(urlSpan);
        } else {//选择单词 数字 词语
            // FIXME - We should check if there's a LocaleSpan in the text, this may be
            // something we should try handling or checking for.
            final WordIterator wordIterator = getWordIterator();
            wordIterator.setCharSequence(mTextView.getText(), minOffset, maxOffset);

            selectionStart = wordIterator.getBeginning(minOffset);
            selectionEnd = wordIterator.getEnd(maxOffset);

            if (selectionStart == BreakIterator.DONE || selectionEnd == BreakIterator.DONE ||
                    selectionStart == selectionEnd) {
                // Possible when the word iterator does not properly handle the text's language
                long range = getCharClusterRange(minOffset);
                selectionStart = TextUtils.unpackRangeStartFromLong(range);
                selectionEnd = TextUtils.unpackRangeEndFromLong(range);
            }
        }
		//设置选择文本
       Selection.setSelection((Spannable) mTextView.getText(), selectionStart, selectionEnd);
        return selectionEnd > selectionStart;
}




//URLSpan源码
public class URLSpan extends ClickableSpan implements ParcelablParcelableSpaneSpan {
	...省略代码
	/** @hide */
    public int getSpanTypeIdInternal() {
        return TextUtils.URL_SPAN;
    }
    @Override
    public void onClick(View widget) {
        Uri uri = Uri.parse(getURL());
        Context context = widget.getContext();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w("URLSpan", "Actvity was not found for intent, " + intent.toString());
        }
    }
}



...省略代码
private void insertMDLinked(int where, String name, String url, String mdLinked) {
        Editable text = getText();
        LinkeSpan span = new LinkeSpan(getContext(),name, url, mdLinked);
        text.insert(where, name);
        text.setSpan(span, where, where + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		//同一位置设置两种Span
        text.setSpan(new ReplacementSelectSpan(), where, where + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
...省略代码


```



* **复制BUG解决**
###### BUG展示:
<video width="320" height="640" controls>
  <source src="复制BUG解决.mp4" type="video/mp4">
</video>

前面通过`URLSpan`解决选择问题同时引入新问题`URLSpan`实现`ParcelableSpan`可悲序列化复制,并且复制结果不允许**自定义**

```java
/**
 * A special kind of Parcelable for objects that will serve as text spans.
 * This can only be used by code in the framework; it is not intended for
 * applications to implement their own Parcelable spans.
 * 对象的一种特殊的Parcelable，用作文本跨度。
 * 这只能由框架中的代码使用;, 它不适用于
 * 应用程序来实现自己的
 * Parcelable跨度。
 */
public interface ParcelableSpan extends Parcelable {
    /**
     * Return a special type identifier for this span class.
     * 返回此span类的特殊类型标识符。
     */
    int getSpanTypeId();

    /**
     * Internal implementation of {@link #getSpanTypeId()} that is not meant to
     * be overridden outside of the framework.
     * {@link #getSpanTypeId（）}的内部实现，并不意味着在框架之外被覆盖。
     *
     * @hide
     */
    int getSpanTypeIdInternal();

    /**
     * Internal implementation of {@link Parcelable#writeToParcel(Parcel, int)}
     * that is not meant to be overridden outside of the framework.
     * {@link Parcelable＃writeToParcel（Parcel，int）} 
     * 的内部实现，并不意味着在框架之外被覆盖。
     *
     * @hide
     */
    void writeToParcelInternal(Parcel dest, int flags);
}
```
最终通过查看系统其Span实现找到NoCopySpan接口进行实现避免被**复制**
代码如下:

```java
 private static class ReplacementSelectSpan extends URLSpan implements NoCopySpan {
        public ReplacementSelectSpan() {
            super("");
        }

        @Override
        public void updateDrawState(TextPaint ds) {
        }

        @Override
        public void onClick(View widget) {
        }
    }
```

# 源码:[https://github.com/Android-Mainli/AndroidDemo2018](https://github.com/Android-Mainli/AndroidDemo2018)