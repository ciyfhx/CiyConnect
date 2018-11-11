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

package com.ciyfhx.network.authenticate;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.ciyfhx.network.AESPipeLine;
import com.ciyfhx.network.NetworkConnection;
import com.ciyfhx.builder.PipeLineStreamBuilder;
import com.ciyfhx.network.PipeLineStream;
import com.ciyfhx.network.validator.SecretInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * RSAWithAESAuthentication start a three-way handshake for every new incoming connection.
 * An AESPipline will be automatically added to the connection once the three-way handshake succeed
 *  *</p>
 * @author  Peh Zi Heng
 * @version 1.0
 * @since   2018-11-11
 */
public class RSAWithAESAuthentication extends AuthenticationManager{

	private static final String ALGORITHM = "RSA";

	private KeyPair keyPair;
	private PublicKey pPublicKey;

	private byte[] keyValue;
	private byte[] iv;

	private int size = 2046;

	private boolean isAuthenticated = false;

	private Logger logger = LoggerFactory.getLogger(RSAWithAESAuthentication.class);

	@Override
	public boolean serverAuthenticate(NetworkConnection connection) {
		logger.info("Authenticating new connection of " + connection.getAddress());
		try {

			keyPair = generateRSAKeyPair();
			sendRSAPublicKey(connection);

			pPublicKey = readRSAPublicKey(connection);

			logger.debug("Getting AES Key");
			keyValue = decrypt(readBytes(connection), keyPair.getPrivate());
			logger.debug("AES Key: {}", new String(keyValue));

			logger.debug("Generating IV and encrypting IV");
			iv = randomSecureBytes(16);
			byte[] encryptedIV = encrypt(iv, pPublicKey);
			logger.debug("Sending AES IV");
			sendBytes(connection, encryptedIV);

			return true;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean clientAuthenticate(NetworkConnection connection) {
		logger.info("Authenticating new connection of " + connection.getAddress());
		try {
			pPublicKey = readRSAPublicKey(connection);

			keyPair = generateRSAKeyPair();
			sendRSAPublicKey(connection);

			logger.debug("Generating Key and encrypting Key");
			keyValue = randomKeyValue();
			byte[] encryptedKeyValue = encrypt(keyValue, pPublicKey);

			logger.debug("Sending AES Key");
			sendBytes(connection, encryptedKeyValue);

			logger.debug("Getting AES IV");
			iv = decrypt(readBytes(connection), keyPair.getPrivate());

			return true;

		} catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	private PublicKey readRSAPublicKey(NetworkConnection connection)
			throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		byte[] publicKeyEncoded;

		logger.debug("Getting RSA Public Key");
		publicKeyEncoded = readBytes(connection);

		return KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(publicKeyEncoded));
	}

	protected void sendBytes(NetworkConnection connection, byte[] data) throws IOException {
		connection.getDataOutputStream().writeInt(data.length);
		connection.getDataOutputStream().write(data);
		connection.getDataOutputStream().flush();
	}

	protected byte[] readBytes(NetworkConnection connection) throws IOException {
		byte[] data = new byte[connection.getDataInputStream().readInt()];
		connection.getDataInputStream().read(data);
		return data;
	}

	private void sendRSAPublicKey(NetworkConnection connection) throws IOException {
		logger.debug("Sending RSA-{} Public Key", size);
		byte[] publicKeyEncoded = keyPair.getPublic().getEncoded();
		sendBytes(connection, publicKeyEncoded);
	}

	private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(size);
		return keyGen.generateKeyPair();
	}

	@Override
	public void authenticationSuccess(NetworkConnection connection) {
		logger.info("Authentication Success");
		AESPipeLine aesPipeLine = new AESPipeLine();
		aesPipeLine.setKeyValue(keyValue);
		aesPipeLine.setIV(iv);
		PipeLineStream stream = connection.getPipeLineStream();

		if(stream==null)connection.setPipeLineStream(PipeLineStreamBuilder.newInstance().addPipeLine(aesPipeLine).build());
		else stream.addPipeLine(aesPipeLine);
	}

	@Override
	public void authenticationFailed(NetworkConnection connection) {
		logger.info("Authentication Failed");
	}

	@Override
	public void authenticationTimeOut(NetworkConnection connection) {
		logger.info("Authentication Timeout");
	}

	public static byte[] encrypt(byte[] data, PublicKey key) {
		byte[] cipherText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, key);
			cipherText = cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherText;
	}

	public static byte[] decrypt(byte[] data, PrivateKey key) {
		byte[] decryptedData = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);

			// decrypt the text using the private key
			cipher.init(Cipher.DECRYPT_MODE, key);
			decryptedData = cipher.doFinal(data);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return decryptedData;
	}

	public KeyPair getKeyPair() {
		return keyPair;
	}

	public static byte[] randomKeyValue() throws NoSuchAlgorithmException {
		KeyGenerator gen = KeyGenerator.getInstance("AES");
		gen.init(128); /* 128-bit AES */
		SecretKey secret = gen.generateKey();
		byte[] binary = secret.getEncoded();
		return binary;
	}

	public static byte[] randomSecureBytes(int size) throws NoSuchAlgorithmException {
		byte[] bytes = new byte[size];
		SecureRandom.getInstance("SHA1PRNG").nextBytes(bytes);
		return bytes;
	}

	/**
	 * Returns the sender RSA public key
	 * @return
	 */
	public PublicKey getSenderPublicKey() {
		return pPublicKey;
	}

	/**
	 * Return true if
	 * @return
	 */
	public boolean isAuthenticated() {
		return isAuthenticated;
	}
	public int getRSAKeySize() {
		return size;
	}

	/**
	 * Set the key size use to generate the RSA key
	 * @param size
	 * @return
	 */
	public void setRSAKeySize(int size){
		this.size = size;
	}

}
