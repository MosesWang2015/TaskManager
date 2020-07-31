/*
 *
 * Copyright (C) 2020 iQIYI (www.iqiyi.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.qiyi.basecore.taskmanager.callable;

import androidx.annotation.NonNull;

import org.qiyi.basecore.taskmanager.callable.iface.CallEachKV;
import org.qiyi.basecore.taskmanager.callable.iface.IAfterCall;
import org.qiyi.basecore.taskmanager.callable.iface.IPreCall;
import org.qiyi.basecore.taskmanager.callable.iface.ShiftCallKV;

import java.util.HashMap;
import java.util.Map;

public final class MapEachCall<K, V> extends ShiftKV<K, V> {

    private Map<K, V> mMap;

    public MapEachCall(Map<K, V> map) {
        mMap = map;
    }

    MapEachCall() {
    }

    @Override
    protected <RK, RV> void shiftEach(ShiftKV<RK, RV> chain, @NonNull ShiftCallKV<K, V, ? extends ShiftKV<RK, RV>> each) {
        if (mMap != null && each != null) {
            for (Map.Entry<K, V> var : mMap.entrySet()) {
                chain.addNext(each.call(var.getKey(), var.getValue()));
                buildPreCall(var, mPreCall);
                buildAfterCall(var, mAfterCall);
            }
            mAfterCall = null;
            mPreCall = null;
        }
    }

    @Override
    protected <T> void shiftEach(ShiftT<T> chain, @NonNull ShiftCallKV<K, V, ? extends ShiftT<T>> each) {
        if (mMap == null || each == null) return;

        for (Map.Entry<K, V> var : mMap.entrySet()) {
            chain.addNext(each.call(var.getKey(), var.getValue()));
            buildPreCall(var, mPreCall);
            buildAfterCall(var, mAfterCall);
        }

        mAfterCall = null;
        mPreCall = null;
    }


    @Override
    protected void callEach(CallEachKV<K, V> each) {
        if (mMap != null) {
            for (Map.Entry<K, V> var : mMap.entrySet()) {

                buildPreCall(var, mPreCall);
                buildAfterCall(var, mAfterCall);

                if (each != null) {
                    each.call(var.getKey(), var.getValue());
                }
            }

            mAfterCall = null;
            mPreCall = null;
        }
    }

    @Override
    protected MapEachCall<K, V> copy() {
        return ShiftFactory.create(mMap);
    }

    private void buildPreCall(Map.Entry<K, V> entry, IPreCall<HashMap.Entry<K, V>> callback) {
        if (callback != null) {
            PreCall<Map.Entry<K, V>> preCall = new PreCall<>(entry, callback);
            addPreCall(preCall);
        }
    }

    private void buildAfterCall(Map.Entry<K, V> var, IAfterCall<HashMap.Entry<K, V>> callback) {
        if (callback != null) {
            AfterCall<Map.Entry<K, V>> afterCall = new AfterCall<>(var, callback);
            addAfterCall(afterCall);
        }
    }

}