package com.mainli.glide;

import androidx.annotation.NonNull;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.request.BaseRequestOptions;

/**
 * Created by lixiaoliang on 2018-4-11.
 * Glide Generated API 可在 Application 和 Library 中被扩展。扩展使用被注解的静态方法来添加新的选项、修改现有选项、甚至添加额外的类型支持。
 *
 * @GlideExtension 注解用于标识一个扩展 Glide API 的类。任何扩展 Glide API 的类都必须使用这个注解来标记，否则其中被注解的方法就会被忽略。
 * <p>
 * 被 @GlideExtension 注解的类应以工具类的思维编写。这种类应该有一个私有的、空的构造方法，应为 final 类型，并且仅包含静态方法。被注解的类可以含有静态变量，可以引用其他的类或对象。
 * 被 @GlideExtention 注解的类有两种扩展方式：
 * <p>
 * GlideOption - 为 RequestOptions 添加一个自定义的选项。
 * GlideType - 添加对新的资源类型的支持(GIF，SVG 等等)。
 * <p>
 * GlideOption
 * 用 @GlideOption 注解的静态方法用于扩展 RequestOptions 。GlideOption 可以：
 * <p>
 * 定义一个在 Application 模块中频繁使用的选项集合。
 * 创建新的选项，通常与 Glide 的 Option 类一起使用。
 */
@GlideExtension
public final class GlobalGlideExtension {
    private GlobalGlideExtension() {
    }

    @GlideOption
    @NonNull
    public static BaseRequestOptions<?> blurByRenderScript(@NonNull BaseRequestOptions<?> options, float radius) {
        return options.transform(new BlurByRenderScriptTransformation(radius));
    }
}
