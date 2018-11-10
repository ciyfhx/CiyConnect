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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MACValidator implements PipeLine {

    private String salt;
    private String secret;

    @Override
    public ByteBuffer read(ByteBuffer data) throws Exception {

        //data.
    }

    @Override
    public ByteBuffer write(ByteBuffer data) throws Exception {
        ByteBuffer hashed = getSHA512SecurePassword(data, salt, secret);
        return data.put(hashed);
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

}