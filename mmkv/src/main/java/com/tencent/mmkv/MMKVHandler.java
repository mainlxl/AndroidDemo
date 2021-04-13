/*
 * Tencent is pleased to support the open source community by making
 * MMKV available.
 *
 * Copyright (C) 2018 THL A29 Limited, a Tencent company.
 * All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *       https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.mmkv;

/**
 * callback is called on the operating thread of the MMKV instance
 * 在MMKV实例的操作线程上调用回调
 */
public interface MMKVHandler {

    /**
     * by default MMKV will discard all data on crc32-check failure
     * return `OnErrorRecover` to recover any data on the file
     * 默认情况下，MMKV将丢弃crc32-check failure上的所有数据
     * 返回`OnErrorRecover`以恢复文件中的任何数据
     *
     * @param mmapID
     * @return
     */
    MMKVRecoverStrategic onMMKVCRCCheckFail(String mmapID);

    /**
     * by default MMKV will discard all data on file length mismatch
     * return `OnErrorRecover` to recover any data on the file
     * 默认情况下，MMKV将丢弃文件长度不匹配的所有数据
     * 返回`OnErrorRecover`以恢复文件上的任何数据
     *
     * @param mmapID
     * @return
     */
    MMKVRecoverStrategic onMMKVFileLengthError(String mmapID);

    /**
     * return false if you don't want log redirecting
     *
     * @return 如果您不想要日志重定向，则返回false
     */
    boolean wantLogRedirecting();

    // log redirecting
    void mmkvLog(MMKVLogLevel level, String file, int line, String function, String message);
}
