package com.getkeepsafe.relinker;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.hzy.lib7z.IExtractCallback;
import com.hzy.lib7z.Z7Extractor;

import java.io.File;

/**
 * @Synopsis 用于扩展7z解压assets中so
 * @Author Mainli
 * @Date 2019/12/9/009
 */
public class ApkAssets7ZLibraryInstaller extends ApkLibraryInstaller {
    @Override
    public void installLibrary(Context context, String[] abis, String mappedLibraryName, File destination, ReLinkerInstance instance) {
        //解压回调在当前线程
        AssetManager assets = context.getAssets();
        String path7z = obtain7zPath(assets, abis, mappedLibraryName);
        Log.d("Mainli", "installLibrary(path7z = [" + 9 + "]");
        if (!TextUtils.isEmpty(path7z) && Z7Extractor.extractAsset(assets, path7z, destination.getParent(), new SingleFileRenameExtractCallback(destination)) != 0) {
            super.installLibrary(context, abis, mappedLibraryName, destination, instance);
        }
    }

    private String obtain7zPath(AssetManager assets, String[] abis, String libraryName) {
        if (libraryName.length() > 3 && !libraryName.endsWith(".7z")) {
            try {
                String mappedLibraryName = libraryName.substring(0, libraryName.length() - 3);
                String[] libNames = assets.list("lib");
                for (int i = 0; i < abis.length; i++) {
                    String abi = abis[i];
                    for (int j = 0; j < libNames.length; j++) {
                        if (TextUtils.equals(abi, libNames[j])) {
                            return String.format("lib/%s/%s.7z", abi, mappedLibraryName);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 用于解压完成后添加版本号信息
     */
    private static class SingleFileRenameExtractCallback implements IExtractCallback {
        private File destination;
        private String tmpFileName;

        public SingleFileRenameExtractCallback(File destination) {
            this.destination = destination;
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onGetFileNum(int fileNum) {

        }

        @Override
        public void onProgress(String name, long size) {
            tmpFileName = name;
        }

        @Override
        public void onError(int errorCode, String message) {

        }

        @Override
        public void onSucceed() {
            String name = destination.getName();
            if (!TextUtils.equals(name, tmpFileName)) {
                File file = new File(destination.getParentFile(), tmpFileName);
                if (destination.exists()) {
                    destination.delete();
                }
                file.renameTo(destination);
            }
        }
    }

}
