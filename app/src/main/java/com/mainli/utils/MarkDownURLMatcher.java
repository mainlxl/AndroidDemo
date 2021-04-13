package com.mainli.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Browser;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.Layout;
import android.text.NoCopySpan;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.mainli.MyApplication;
import com.mainli.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mainli on 2018-3-29.
 */
public class MarkDownURLMatcher {
    private final static String URL_NAME = "[\\w \\(\\)\\t#&%$@\\u4e00-\\u9fa5]*";
    private final static String HTTP = "(https?|ftp|file)://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
    private final static String MATCHER = "\\[(" + URL_NAME + ")\\]\\((" + HTTP + ")\\)";
    private final static Pattern MD_URL_MATCHER = Pattern.compile(MATCHER);
    private final static Pattern URL_MATCHER = Pattern.compile(HTTP);
    private final static String FEEDS_TEXT_APPEND = "全文";
    private final static String APPEND_ELLIPSIS = "… ";

    //    icon_linked.xml
    private static int convertLinkedSpan(String text, Matcher m, SpannableStringBuilder spannableStringBuilder, int lastIndex, String name, String url) {
        int start = m.start();
        String substring = text.substring(lastIndex, start);
        spannableStringBuilder.append(substring);
        int urlTextStart = spannableStringBuilder.length();
        spannableStringBuilder.append(name);
        spannableStringBuilder.setSpan(new ClickLinkeSpan(name, url), urlTextStart, urlTextStart + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        lastIndex = m.end();
        return lastIndex;
    }


    /**
     * 收集文本中markdown链接 转换为SpannerString 限制最大长度超过追加小尾巴
     * <p>
     * 格式:[xxx](xxxx)
     */
    public static final SpannableStringBuilder convertTextLinksAtMost(String text, int maxCharSize) {
        Matcher m = MD_URL_MATCHER.matcher(text);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        int lastIndex = 0;
        while (m.find()) {
            String name = m.group(1);
            String url = m.group(2);
            if (name == null) {
                name = "";
            }
            if (spannableStringBuilder.length() + m.start() - lastIndex <= maxCharSize) {
                lastIndex = convertLinkedSpan(text, m, spannableStringBuilder, lastIndex, name, url);
            } else {//长度不够添加超链接
                spannableStringBuilder.append(text.substring(lastIndex, lastIndex + maxCharSize - spannableStringBuilder.length()));
                return appendUnfoldSpan(spannableStringBuilder);//添加展开尾巴
            }
        }
        if (lastIndex == 0) {//没有超链接
            if (text.length() <= maxCharSize) {
                spannableStringBuilder.append(text);
            } else {
                spannableStringBuilder.append(text.substring(0, maxCharSize));
                appendUnfoldSpan(spannableStringBuilder);//添加展开尾巴
            }
        } else if (maxCharSize > spannableStringBuilder.length()) {//有超链接且添加完长度还不到140
            if (text.length() - lastIndex > maxCharSize) {
                spannableStringBuilder.append(text.substring(lastIndex, maxCharSize));
                appendUnfoldSpan(spannableStringBuilder);//添加展开尾巴
            } else {
                appendEndText(spannableStringBuilder, text.substring(lastIndex, text.length()));
            }
        }
        return spannableStringBuilder;
    }

    private static void appendEndText(SpannableStringBuilder spannableStringBuilder, String substring) {
        if (TextUtils.isEmpty(substring)) {
            spannableStringBuilder.append("\t");//防止点击事件延长至正行
        } else {
            spannableStringBuilder.append(substring);
        }
    }

    private static SpannableStringBuilder appendUnfoldSpan(SpannableStringBuilder spannableStringBuilder) {
        spannableStringBuilder.append(APPEND_ELLIPSIS);
        int start = spannableStringBuilder.length();
        spannableStringBuilder.append(FEEDS_TEXT_APPEND);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(0xFF687FFF), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    private static final int MAX_CHAR_SIZE = 140;

    public static final SpannableStringBuilder convertTextLinks(String text, boolean isAtMost) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        if (isAtMost) {
            return convertTextLinksAtMost(text, MAX_CHAR_SIZE);
        }
        return convertTextLinks(text);
    }

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
            spannableStringBuilder.append("\t");//防止点击事件延长至正行
        }
        return spannableStringBuilder;
    }

    /**
     * 收集文本中markdown链接
     * 格式:[xxx](xxxx)
     */
    public static final ArrayList<LinkSpec> gatherLinks(String text) {
        Matcher m = MD_URL_MATCHER.matcher(text);
        ArrayList<LinkSpec> links = new ArrayList<LinkSpec>();
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            LinkSpec spec = makeLinkSpec(m, start, end);
            links.add(spec);
        }
        return links;
    }

    private static LinkSpec makeLinkSpec(Matcher group, int start, int end) {
        LinkSpec spec = new LinkSpec();
        spec.text = group.group(0);
        spec.url = group.group(2);
        String urlName = group.group(1);
        spec.urlName = TextUtils.isEmpty(urlName) ? spec.url : urlName;
        spec.start = start;
        spec.end = end;
        return spec;
    }

    public static class LinkSpec {
        String text;
        String url;
        String urlName;
        int start;
        int end;

        @Override
        public String toString() {
            return "text = " + text + '\n' + "url = " + url + '\n' + "urlName = " + urlName + '\n' + "start = " + start + " | end = " + end + "\n-------------------------------------------------------";
        }
    }

    private final static Drawable sDrawableLiked;
    private final static int dp4 = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, Resources.getSystem().getDisplayMetrics()) + 0.5F);
    private final static int dp8 = dp4 << 1;
    private final static int dp16 = dp8 << 1;
    private final static int dp32 = dp16 << 1;

    static {
        sDrawableLiked = ContextCompat.getDrawable(MyApplication.getAppContext(), R.drawable.icon_linked_ff687fff);
    }

    private static class ClickLinkeSpan extends ReplacementSpan implements SpanClickable,NoCopySpan {
        private String url;
        private String name;


        public ClickLinkeSpan(String name, String url) {
            this.name = name;
            this.url = url;
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            return (int) (dp32 + dp4 + paint.measureText(name) + 0.5F);
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            sDrawableLiked.setBounds((int) (x + dp8), top + ((bottom - top - sDrawableLiked.getMinimumHeight()) >> 1), (int) (x + dp16 + dp8), bottom);
            sDrawableLiked.draw(canvas);
            int color = paint.getColor();
            paint.setColor(0xFF687FFF);
            canvas.drawText(this.name, x + dp32, (float) y, paint);
            paint.setColor(color);
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

    public interface SpanClickable {
        void onClick(View widget);
    }

    /**
     * 防止textView.setMovementMethod(LinkMovementMethod.getInstance());之后文本没法正常在末尾加...变成可滑动
     */
    public static void attachTextViewOnTouchClickable(TextView textView) {
        textView.setOnTouchListener(ClickableSpanListener.getInstantce());
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


    /**
     * 校验url合法性
     *
     * @param url
     * @return
     */
    public static final boolean validateUrl(String url) {
        return URL_MATCHER.matcher(url).matches();
    }
}
