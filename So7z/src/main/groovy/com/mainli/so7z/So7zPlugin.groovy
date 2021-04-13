package com.mainli.so7z

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

import java.util.stream.Collectors

/**
 *
 */
public class So7zPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create('so7z', So7zExtensions, project)
        def android = project.extensions.getByType(com.android.build.gradle.AppExtension.class)
        def buildTypes = android.getBuildTypes().stream().map({
            it.name
        }).collect(Collectors.toList())
        project.afterEvaluate {
            Set<String> abiFilters = project.so7z.abiFilters;
            if (abiFilters == null || project.so7z.isEmpty()) {
                abiFilters = android.getDefaultConfig().getNdk().getAbiFilters();
                abiFilters = abiFilters.isEmpty() ? project.so7z.DEFAULT_ABI_FILTERS : abiFilters
            }
            final String[] excludeBuildTypes = project.so7z.excludeBuildTypes
            if (excludeBuildTypes.length > 0) {
                def iterator = buildTypes.iterator()
                while (iterator.hasNext()) {
                    def next = iterator.next()
                    for (String item : excludeBuildTypes) {
                        if (next.equalsIgnoreCase(item)) {
                            iterator.remove()
                        }
                    }
                }
            }
            if (buildTypes.isEmpty()) {
                println "没有buildTypes需要压缩so"
                return
            }

            String path7Z = project.so7z.path7Z
            if (path7Z == null || path7Z.length() <= 0) {
                Properties properties = new Properties()
                def file = new File(project.getRootDir(), 'local.properties')
                properties.load(new FileInputStream(file))
                path7Z = properties.getProperty("cmd.7z")
            }
            if (!new File(path7Z.replace("\"", "")).exists()) {
                println "未找到7z可执行文件,请配置 [path7Z]"
                return;
            }
            SoPathConfig config = project.so7z.soPathConfig
            if (!config.checkAvailable()) {
                println "请配置soPathConfig"
                return;
            }
            String[] sos = project.so7z.sos
            if (sos.length <= 0) {
                println "未添加任何so文件"
                return;
            }
            buildTypes.forEach({
                def upperCaseName = upperCase(it)
                String src = config.buildSoPath(it)
                String dst = config.buildAssetsPath(it)
                println "src:" + src
                println "dst:" + dst
                def compressionAction = new CompressionAction(abiFilters, path7Z, sos, src, dst)
                project.tasks.forEach({ Task theTask ->
                    if (theTask.name == "merge${upperCaseName}NativeLibs") {
                        theTask.doLast(compressionAction)
                        return
                    }
                })
            })
        }
    }

    private String upperCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
