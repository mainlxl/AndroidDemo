package com.mainli.so7z

import org.gradle.api.Action

import java.nio.channels.FileChannel

/**
 * @Synopsis
 * @Author Mainli
 * @Date 2019/12/11/011
 */
public class CompressionAction implements Action {
    private String path7z;
    private String[] sos;
    private String sourceDir;
    private String dstDir;
    private Set<String> abiFilters

    @Override
    void execute(Object o) {
        println "CompressionAction(path7z = [" + path7z + "], sos = [" + sos + "], sourceDir = [" + sourceDir + "], dstDir = [" + dstDir + "])"
        int count = 1;
        for (String name : sos) {
            println "transformSo2Assets--------------执行-> ${count++}.$name"
            transformSo(sourceDir, dstDir, name)
        }
    }

    CompressionAction(Set<String> abiFilters, String path7z, String[] sos, String sourceDir, String dstDir) {
        this.abiFilters = abiFilters
        this.path7z = path7z
        this.sos = sos
        this.sourceDir = sourceDir
        this.dstDir = dstDir
    }

    private void transformSo(String sourceDir, String dstDir, String name) {
        abiFilters.stream().map({ "$it/$name" }).forEach({ path ->
            def src = new File("$sourceDir/$path")
            if (src.exists()) {
                File dest = new File("${dstDir}/${path.replace(".so", ".7z")}")
                def parentFile = dest.getParentFile()
                if (!parentFile.exists()) {
                    parentFile.mkdirs()
                }
                if (dest.exists()) {
                    dest.delete()
                }
                String cmd = "$path7z a -t7z ${dest.getAbsolutePath()} ${src.getAbsolutePath()} -mx=9 -m0=LZMA2 -ms=10m -mf=on -mhc=on -mmt=on";
                println "cmd: $cmd"
                Runtime.getRuntime().exec(cmd).waitFor()
//            copyFileUsingFileChannels(src, file("$dstDir/$path"))
                src.delete()
            }
        }
        )
    };


    private void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }
}
