package com.mainli.so7z

import org.gradle.api.Project;

/**
 * @Synopsis
 * @Author Mainli
 * @Date 2019/12/11/011
 */
public class So7zExtensions {
    Project project
    Set<String> abiFilters = null
    final Set<String> DEFAULT_ABI_FILTERS = ["armeabi", "armeabi-v7a", "arm64-v8a", "x86", "x86_64"]

    So7zExtensions(Project project) {
        this.project = project
    }

    String[] sos = []

    String[] excludeBuildTypes = []
    final SoPathConfig soPathConfig = new SoPathConfig("${project.buildDir}\\intermediates\\merged_native_libs", "out\\lib",
            "${project.buildDir}\\intermediates\\merged_assets", "out\\lib");
    /**
     * 默认路径中含有空格需要双引号包起防止执行cmd时因空格导致命令未找到
     */
    String path7Z = "\"C:/Program Files/7-Zip/7z.exe\""
    /**
     * 默认so 路径前缀build\intermediates\merged_native_libs\,后缀\out\lib
     * 默认assets 路径前缀build\intermediates\merged_assets\,后缀\out\lib
     * @param closure
     */
    void setSoPathConfig(Closure closure) {
        project.configure(soPathConfig, closure)
    }


}
