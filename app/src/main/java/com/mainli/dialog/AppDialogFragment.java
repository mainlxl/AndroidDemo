package com.mainli.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mainli.BuildConfig;
import com.mainli.R;

/**
 * Created by lixiaoliang on 2018-3-29.
 * 示例用法(kotlin) - simple
 * AppDialogFragment.Build(this)
 * .icon(R.mipmap.app_icon)
 * .title("nihao")
 * .contentText("啊是打算打算")
 * .addButton(R.string.crash_cancel)
 * .addButton("OK", object : View.OnClickListener {
 * override fun onClick(v: View?) {
 * ToastUtils.getInstance().showToast("hello")
 * }
 * }, AppDialogFragment.ButtonOperator { it.setBackgroundColor(Color.RED) })
 * .show(this, "aaa")
 * <p>
 * 示例用法(kotlin) - 自定义内容布局与按钮样式:
 * AppDialogFragment.Build(this)
 * .icon(R.mipmap.app_icon)
 * .title("nihao")
 * .setContentLayout(R.layout.dialog_zdy).setDialogOperator(object : AppDialogFragment.DialogOperator { //设置自定义布局,在DialogOperator中初始化
 * override fun operate(dialog: AppDialogFragment, rootView: View) {
 * rootView.findViewById<TextView>(R.id.tv1).setText("文本1")
 * rootView.findViewById<TextView>(R.id.tv2).setText("文本2")
 * rootView.findViewById<TextView>(R.id.tv3).setText("文本3")
 * }
 * })
 * .addButton("OK", object : View.OnClickListener {
 * override fun onClick(v: View?) {
 * ToastUtils.getInstance().showToast("hello")
 * }
 * }, AppDialogFragment.ButtonOperator { it.setBackgroundColor(Color.RED) })
 * .show(this, "aaa")
 */
public final class AppDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "AppDialog";

    private DialogData data;//保留创建时Context 初始化时设置主题会被包裹为ContextThemeWrapper

    private void setData(DialogData data) {
        this.data = data;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppDialog);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        if (data.contentView != null) {
            view = data.contentView;
        } else if (data.contentLayout != data.INVALID_LAYOUT) {
            view = inflater.inflate(data.contentLayout, container, true);
        } else {
            view = inflater.inflate(R.layout.dialog_app_default_content, container, true);
            initDefaultTitle(view);
            if (!TextUtils.isEmpty(data.message)) {
                TextView title = view.findViewById(R.id.dialog_message);
                title.setText(data.message);
                title.setVisibility(View.VISIBLE);
            }
            initDefaultButton(view);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        autoCancelable();
        if (data.operator != null) {
            data.operator.operate(this, view);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        //减少Windows.dispatchWindowAttributesChanged(attrs);调用次数
        if (data.verticalGravity != Gravity.CENTER) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
            attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setGravity(data.verticalGravity);
        } else {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (data.dismissListener != null) {
            data.dismissListener.onDismiss(this);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (data.dismissListener != null) {
            data.dismissListener.onDismiss(this);
        }
    }

    private void autoCancelable() {
        if (data.contentView == null && data.contentLayout == data.INVALID_LAYOUT//没有添加自定义布局切为添加button时
                && TextUtils.isEmpty(data.thirdButtonText)) {
            setCancelable(true);
        } else {
            setCancelable(data.cancelable);
        }
    }

    private void initDefaultTitle(View view) {
        if (!TextUtils.isEmpty(data.title) || data.icon != null) {
            View root = view.findViewById(R.id.dialog_title_root);
            root.setVisibility(View.VISIBLE);
            if (data.icon != null) {
                ImageView icon = root.findViewById(R.id.dialog_icon);
                icon.setImageDrawable(data.icon);
                icon.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(data.title)) {
                TextView titleView = root.findViewById(R.id.dialog_title);
                titleView.setText(data.title);
                titleView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initDefaultButton(View view) {
        if (data.firstButtonText != null || data.secondButtonText != null || data.thirdButtonText != null) {
            View root = view.findViewById(R.id.dialog_btn_root);
            root.setVisibility(View.VISIBLE);
            initDefaultButton(root, R.id.dialog_first_btn, data.firstButtonText, data.firstButtonOperator);
            initDefaultButton(root, R.id.dialog_second_btn, data.secondButtonText, data.secondButtonOperator);
            initDefaultButton(root, R.id.dialog_third_btn, data.thirdButtonText, data.thirdButtonOperator);
        }
    }

    private void initDefaultButton(View root, @IdRes int id, CharSequence text, ButtonOperator operator) {
        if (!TextUtils.isEmpty(text)) {
            Button thirdBtn = root.findViewById(id);
            thirdBtn.setVisibility(View.VISIBLE);
            thirdBtn.setOnClickListener(AppDialogFragment.this);
            thirdBtn.setText(text);
            if (operator != null) {
                operator.operate(thirdBtn);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_first_btn:
                if (data.firstButtonOnClick != null) {
                    data.firstButtonOnClick.onClick(v);
                }
                break;
            case R.id.dialog_second_btn:
                if (data.secondButtonOnClick != null) {
                    data.secondButtonOnClick.onClick(v);
                }
                break;
            case R.id.dialog_third_btn:
                if (data.thirdButtonOnClick != null) {
                    data.thirdButtonOnClick.onClick(v);
                }
                break;
        }
        dismiss();
    }


    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        if (data.cancelable != flag) {
            data.cancelable = flag;
        }
    }

    public static class DialogData {
        Context mContext;

        public DialogData(Context context) {
            mContext = context;
        }

        final int INVALID_LAYOUT = -99;
        //自定义布局
        @LayoutRes
        int contentLayout = INVALID_LAYOUT;
        View contentView = null;

        CharSequence title = null;
        CharSequence message = null;
        Drawable icon = null;
        CharSequence firstButtonText = null;
        CharSequence secondButtonText = null;
        CharSequence thirdButtonText = null;

        ButtonOperator firstButtonOperator = null;
        ButtonOperator secondButtonOperator = null;
        ButtonOperator thirdButtonOperator = null;

        View.OnClickListener firstButtonOnClick;
        View.OnClickListener secondButtonOnClick;
        View.OnClickListener thirdButtonOnClick;
        DialogDismissListener dismissListener;
        boolean cancelable = false;
        int verticalGravity = Gravity.CENTER;
        AppDialogFragment.DialogOperator operator = null;

    }


    public static class Build {

        private AppDialogFragment.DialogData data;

        public Build(Context context) {
            data = new AppDialogFragment.DialogData(context);
        }

        public AppDialogFragment.Build title(String title) {
            data.title = title;
            return this;
        }

        public AppDialogFragment.Build title(@StringRes int titleId) {
            data.title = data.mContext.getString(titleId);
            return this;
        }

        public AppDialogFragment.Build icon(Drawable icon) {
            data.icon = icon;
            return this;
        }

        public AppDialogFragment.Build icon(Bitmap bitmap) {
            data.icon = new BitmapDrawable(data.mContext.getResources(), bitmap);
            return this;
        }

        public AppDialogFragment.Build icon(@DrawableRes int icon) {
            data.icon = ContextCompat.getDrawable(data.mContext, icon);
            return this;
        }

        public AppDialogFragment.Build message(CharSequence message) {
            data.message = message;
            return this;
        }

        public AppDialogFragment.Build setDismissListener(DialogDismissListener dismissListener) {
            data.dismissListener = dismissListener;
            return this;
        }

        public AppDialogFragment.Build message(@StringRes int messageId) {
            data.message = data.mContext.getString(messageId);
            return this;
        }

        /**
         * Set the gravity of the window, as per the Gravity constants.  This
         * controls how the window manager is positioned in the overall window; it
         * is only useful when using WRAP_CONTENT for the layout width or height.
         * 宽度已设置为MATCH_PARENT
         *
         * @param verticalGravity 竖直方向 gravity.
         * @see Gravity
         */
        public AppDialogFragment.Build setVerticalGravity(int verticalGravity) {
            data.verticalGravity = verticalGravity;
            return this;
        }

        //---------------------自定义按钮区start--------------------------------------------------------------------------------------------------

        /**
         * 最多3
         *
         * @param buttonTextId
         * @return
         */
        public AppDialogFragment.Build addButton(@StringRes int buttonTextId) {
            return addButton(data.mContext.getText(buttonTextId), null, null);
        }


        public AppDialogFragment.Build addButton(@StringRes int buttonTextId, ButtonOperator operator) {
            return addButton(data.mContext.getText(buttonTextId), null, operator);
        }


        public AppDialogFragment.Build addButton(@StringRes int buttonTextId, View.OnClickListener listener) {
            return addButton(data.mContext.getText(buttonTextId), listener, null);
        }

        public AppDialogFragment.Build addButton(CharSequence buttonText) {
            return addButton(buttonText, null, null);
        }


        public AppDialogFragment.Build addButton(CharSequence buttonText, ButtonOperator operator) {
            return addButton(buttonText, null, operator);
        }


        public AppDialogFragment.Build addButton(CharSequence buttonText, View.OnClickListener listener) {
            return addButton(buttonText, listener, null);
        }


        public AppDialogFragment.Build addButton(CharSequence buttonText, View.OnClickListener listener, ButtonOperator operator) {
            if (TextUtils.isEmpty(data.thirdButtonText)) {
                return setDefaultButton(DefaultDialogButtonType.THIRD_BUTTON, buttonText, listener, operator);
            } else if (TextUtils.isEmpty(data.secondButtonText)) {
                return setDefaultButton(DefaultDialogButtonType.SECOND_BUTTON, buttonText, listener, operator);
            } else if (TextUtils.isEmpty(data.firstButtonText)) {
                return setDefaultButton(DefaultDialogButtonType.FIRST_BUTTON, buttonText, listener, operator);
            }
            throw new RuntimeException(TAG + " - 默认按钮最多三个,要使用多个按钮请调用setBottomLayout方法自定义布局");
        }

        /*@link DefaultButtonType}*/
        private AppDialogFragment.Build setDefaultButton(@DefaultDialogButtonType int buttonType, CharSequence buttonText, View.OnClickListener listener, ButtonOperator operator) {
            switch (buttonType) {
                case DefaultDialogButtonType.FIRST_BUTTON:
                    data.firstButtonText = buttonText;
                    data.firstButtonOnClick = listener;
                    data.firstButtonOperator = operator;
                    break;
                case DefaultDialogButtonType.SECOND_BUTTON:
                    data.secondButtonText = buttonText;
                    data.secondButtonOnClick = listener;
                    data.secondButtonOperator = operator;
                    break;
                case DefaultDialogButtonType.THIRD_BUTTON:
                    data.thirdButtonText = buttonText;
                    data.thirdButtonOnClick = listener;
                    data.thirdButtonOperator = operator;
                    break;
                default:
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "button-type: 未知,默认按钮添加失败");
                    }
            }
            return this;
        }

        //---------------------自定义按钮区end--------------------------------------------------------------------------------------------------
        public AppDialogFragment.Build cancelable(boolean cancelable) {
            data.cancelable = cancelable;
            return this;
        }


        /**
         * 自定义布局,请使用{@link DialogOperator}进行初始化操作
         *
         * @param contentLayout
         * @return
         */
        public AppDialogFragment.Build setContentLayout(int contentLayout) {
            data.contentLayout = contentLayout;
            return this;
        }

        public AppDialogFragment.Build setContentView(View contentView) {
            data.contentView = contentView;
            return this;
        }

        public AppDialogFragment.Build setDialogOperator(AppDialogFragment.DialogOperator operator) {
            data.operator = operator;
            return this;
        }

        public AppDialogFragment create() {
            AppDialogFragment AppDialogFragment = new AppDialogFragment();
            AppDialogFragment.setData(data);
            return AppDialogFragment;
        }

        public AppDialogFragment show(FragmentActivity activity, String tag) {
            AppDialogFragment AppDialogFragment = create();
            AppDialogFragment.show(activity.getSupportFragmentManager(), tag);
            return AppDialogFragment;
        }

        public AppDialogFragment show(Fragment fragment, String tag) {
            AppDialogFragment AppDialogFragment = create();
            AppDialogFragment.show(fragment.getChildFragmentManager(), tag);
            return AppDialogFragment;
        }

        public AppDialogFragment show(FragmentManager manager, String tag) {
            AppDialogFragment AppDialogFragment = create();
            AppDialogFragment.show(manager, tag);
            return AppDialogFragment;
        }

        public AppDialogFragment show(FragmentTransaction transaction, String tag) {
            AppDialogFragment AppDialogFragment = create();
            AppDialogFragment.show(transaction, tag);
            return AppDialogFragment;
        }


        public void activitySafetyShow(String tag) {
            if (data.mContext != null && data.mContext instanceof FragmentActivity) {
                FragmentActivity activity = (FragmentActivity) data.mContext;
                if (!activity.isFinishing()) {
                    AppDialogFragment AppDialogFragment = create();
                    AppDialogFragment.show(activity.getSupportFragmentManager(), tag);
                }
            }
        }

    }

    public interface DialogOperator {
        void operate(AppDialogFragment dialog, View rootView);
    }

    public interface ButtonOperator {
        void operate(Button button);
    }

    public interface DialogDismissListener {
        void onDismiss(AppDialogFragment fragment);
    }


}
