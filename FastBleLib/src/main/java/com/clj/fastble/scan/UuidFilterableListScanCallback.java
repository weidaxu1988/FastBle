package com.clj.fastble.scan;

import android.bluetooth.BluetoothDevice;

import com.clj.fastble.utils.UuidUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by 陈利健 on 2016/9/2.
 * 一段限制时间内搜索所有设备
 */
public abstract class UuidFilterableListScanCallback extends ListScanCallback {

    /**
     * Service Uuid 过滤集合
     */
    private List<UUID> uuidList = new ArrayList<>();

    public UuidFilterableListScanCallback(UUID[] uuids, long timeoutMillis) {
        super(timeoutMillis);
        if (uuids == null) {
            throw new IllegalArgumentException("start scan, uuids can not be null!");
        } else {
            uuidList = Arrays.asList(uuids);
        }
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device == null)
            return;

        if (!deviceList.contains(device)) {
            // 是否符合目标service uuid
            List<UUID> scanUuids = UuidUtil.parseUuids(scanRecord);
            if (UuidUtil.hasWantedUuid(uuidList, scanUuids)) {
                deviceList.add(device);
            }
        }
    }

    @Override
    public void onScanTimeout() {
        BluetoothDevice[] devices = new BluetoothDevice[deviceList.size()];
        for (int i = 0; i < devices.length; i++) {
            devices[i] = deviceList.get(i);
        }
        onDeviceFound(devices);
    }

    public abstract void onDeviceFound(BluetoothDevice[] devices);
}
