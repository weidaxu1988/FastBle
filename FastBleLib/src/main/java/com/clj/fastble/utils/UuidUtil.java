package com.clj.fastble.utils;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by dawei on 9/22/16.
 */

public class UuidUtil {
    /**
     * 将广播字节数组转换为uuid列表
     */
    public static List<UUID> parseUuids(byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<>();

        ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
        while (buffer.remaining() > 2) {
            byte length = buffer.get();
            if (length == 0) break;

            byte type = buffer.get();
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (length >= 2) {
                        uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
                        length -= 2;
                    }
                    break;

                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                    while (length >= 16) {
                        long lsb = buffer.getLong();
                        long msb = buffer.getLong();
                        uuids.add(new UUID(msb, lsb));
                        length -= 16;
                    }
                    break;

                default:
                    buffer.position(buffer.position() + length - 1);
                    break;
            }
        }

        return uuids;
    }

    /**
     * 比较是否有需要的uuid列表
     */
    public static boolean hasWantedUuid(List<UUID> serviceUuids, List<UUID> scanUuids) {

        for (UUID uuid : scanUuids) {
            if (serviceUuids.contains(uuid)) {
                return true;
            }
        }
        return false;
    }
}
