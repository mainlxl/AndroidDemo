package com.mainli.so7z;

/**
 * @Synopsis
 * @Author xiaoliang.li@holaverse.com
 * @Date 2019/12/12/012
 */
public class SoPathConfig {
    String soPathPrefix;
    String soPathSuffix;
    String assetsPathPrefix;
    String assetsPathSuffix;

    SoPathConfig(String soPathPrefix, String soPathSuffix, String assetsPathPrefix, String assetsPathSuffix) {
        this.soPathPrefix = soPathPrefix
        this.soPathSuffix = soPathSuffix
        this.assetsPathPrefix = assetsPathPrefix
        this.assetsPathSuffix = assetsPathSuffix
    }

    String buildSoPath(String buildTypes) {
        return "${soPathPrefix}${File.separatorChar}${buildTypes}${File.separatorChar}$soPathSuffix"
    }

    String buildAssetsPath(String buildTypes) {
        return "${assetsPathPrefix}${File.separatorChar}${buildTypes}${File.separatorChar}$assetsPathSuffix"
    }

    boolean checkAvailable() {
        return soPathPrefix != null && soPathSuffix != null && assetsPathPrefix != null && assetsPathSuffix != null
    }
}
