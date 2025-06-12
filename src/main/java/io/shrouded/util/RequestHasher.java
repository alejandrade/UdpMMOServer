package io.shrouded.util;

import io.shrouded.recievers.BaseRequest;
import net.openhft.hashing.LongHashFunction;

public class RequestHasher {

    private static final LongHashFunction HASH_FUNCTION = LongHashFunction.murmur_3();

    /**
     * Generate a 64-bit hash of the request. This can serve as a very fast requestId.
     */
    public static String hashRequest(byte[] data) {
        return Long.toHexString(HASH_FUNCTION.hashBytes(data));
    }
}
