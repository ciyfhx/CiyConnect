/*
 * Copyright (c) 2018.
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package com.ciyfhx.network.validator;

import com.ciyfhx.network.PipeLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * <p>
 * HMAC - keyed-hash message authentication code or hash-based message authentication code
 * HMAC basically uses two keys instead of one
 *</p>
 * <p>
 * Check if the message receive is not tampered by computing the checksum of SHA512
 * <b>Formula:</b> <i>Digest = (SHA512(SHA512(MESSAGE | SECRET)))</i>
 * </p>
 * @author  Peh Zi Heng
 * @version 1.0
 * @since   2018-10-10
 */
public class HMACValidator extends MACValidator{

    private ByteBuffer secret;
    private ByteBuffer connectionSecret;

    private Logger logger = LoggerFactory.getLogger(MACValidator.class);

    @Override
    public ByteBuffer read(ByteBuffer data) throws Exception {
        data.clear();

        byte[] content = new byte[data.capacity()-64];
        data.get(content);
        byte[] hashed = new byte[64];
        data.get(hashed);

        ByteBuffer contentBB = ByteBuffer.wrap(content);
        if(secret==null)throw new RuntimeException("Secret/Salt is not set");
        ByteBuffer computedHash = super.getSHA512SecurePassword(contentBB, secret);

        if(Arrays.equals(computedHash.array(), (hashed))) {
            logger.trace("Computed hashed is correct");
            return contentBB;
        }else throw new InvalidHashException("Computed hash and the hash receive is not the same");
    }

    @Override
    public ByteBuffer write(ByteBuffer data) throws Exception {
        if(connectionSecret==null)throw new RuntimeException("Sender Secret/Salt is not set");

        ByteBuffer hashed = super.getSHA512SecurePassword(data, connectionSecret);
        logger.trace("Computed hashed {}", hashed.capacity());
        data.clear();
        hashed.clear();
        return (ByteBuffer.allocate(data.capacity()+64).put(data).put(hashed));
    }


}