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

package com.ciyfhx.network;

import java.nio.ByteBuffer;
import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AESPipeLine implements PipeLine {

	private static final String ALGO = "AES/CBC/PKCS5PADDING";
	private byte[] keyValue;
	
	private IvParameterSpec iv;
	
	private Logger logger = LoggerFactory.getLogger(AESPipeLine.class);

	@Override
	public ByteBuffer read(ByteBuffer data) {
		//logger.trace("Before Decrypt: " + new String(data.array()));
		try {
			//logger.trace("After Decrypt: " + new String(decrypt(data.array())));
			return ByteBuffer.wrap(decrypt(data.array()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ByteBuffer write(ByteBuffer data) {
		//logger.trace("Before Encrypt: " + new String(data.array()));
		try {
			//logger.trace("After Encrypt: " + new String(encrypt(data.array())));
			return ByteBuffer.wrap(encrypt(data.array()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] encrypt(byte[] data) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] encVal = c.doFinal(data);
		return Base64.getMimeEncoder().encode(encVal);
	}

	public byte[] decrypt(byte[] encryptedData) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] decordedValue = Base64.getMimeDecoder().decode(encryptedData);
		byte[] decValue = c.doFinal(decordedValue);
		return decValue;
	}

	private Key generateKey() throws Exception {
		Key key = new SecretKeySpec(keyValue, "AES");
		return key;
	}
	
	public void setKeyValue(byte[] value){
		this.keyValue = value;
	}
	
	public void setIV(byte[] value){
		this.iv = new IvParameterSpec(value);
	}
	

}
