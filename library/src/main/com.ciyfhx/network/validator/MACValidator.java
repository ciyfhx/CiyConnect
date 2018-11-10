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

import com.ciyfhx.network.AESPipeLine;
import com.ciyfhx.network.PipeLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * <p>
 * MAC - Message Authenticated Code
 * @see java.util.concurrent.ExecutorService
 *</p>
 * <p>
 * Check if the message receive is not tampered by computing the checksum of SHA512
 * <b>Formula:</b> <i>Digest = (SHA512(MESSAGE | SECRET) with salt)</i>
 * <b>Note:</b> The MAC's hashed function used by this class is susceptible to length extension attack
 * @see HMACValidator
 * </p>
 * @author  Peh Zi Heng
 * @version 1.0
 * @since   2018-10-10
 */
public class MACValidator implements PipeLine {

    private String salt = "";
    private String secret = "";

    private Logger logger = LoggerFactory.getLogger(MACValidator.class);

    @Override
    public ByteBuffer read(ByteBuffer data) throws Exception {
        data.clear();

        byte[] content = new byte[data.capacity()-64];
        data.get(content);
        byte[] hashed = new byte[64];
        data.get(hashed);

        ByteBuffer contentBB = ByteBuffer.wrap(content);

        ByteBuffer computedHash = getSHA512SecurePassword(contentBB, salt, secret);

        if(Arrays.equals(computedHash.array(), (hashed))) {
            return contentBB;
        }else throw new InvalidHashException("Computed hash and the hash receive is not the same");
    }

    @Override
    public ByteBuffer write(ByteBuffer data) throws Exception {
        ByteBuffer hashed = getSHA512SecurePassword(data, salt, secret);
        logger.trace("Computed hashed {}", hashed.capacity());
        data.clear();
        hashed.clear();
        return (ByteBuffer.allocate(data.capacity()+64).put(data).put(hashed));
    }

    private ByteBuffer getSHA512SecurePassword(ByteBuffer data, String salt, String secret){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            ByteBuffer tmp = ByteBuffer.wrap(secret.getBytes(StandardCharsets.UTF_8));
            ByteBuffer finalData = concat(tmp, data);
            byte[] bytes = md.digest(finalData.array());
            return ByteBuffer.wrap(bytes);
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }

    private ByteBuffer concat(ByteBuffer b1, ByteBuffer b2){
        ByteBuffer finalData = ByteBuffer.allocate(b1.limit() + b2.limit());
        finalData.put(b1);
        finalData.put(b2);
        return finalData;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }


    public class InvalidHashException extends Exception {

        public InvalidHashException(String msg){
            super(msg);
        }

    }

}