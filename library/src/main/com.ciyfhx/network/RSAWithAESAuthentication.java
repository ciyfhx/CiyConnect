package com.ciyfhx.network;

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

import com.ciyfhx.builder.PipeLineStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSAWithAESAuthentication extends AuthenticationManager {

	private static final String ALGORITHM = "RSA";

	private KeyPair keyPair;
	private PublicKey pPublicKey;

	private byte[] keyValue;
	private byte[] iv;

	private Logger logger = LoggerFactory.getLogger(RSAWithAESAuthentication.class);

	@Override
	public boolean serverAuthenticate(NetworkConnection connection) {
		logger.info("Authenicating new connection of " + connection.getAddress());
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
		logger.info("Authenicating new connection of " + connection.getAddress());
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

	private void sendBytes(NetworkConnection connection, byte[] data) throws IOException {
		connection.getDataOutputStream().writeInt(data.length);
		connection.getDataOutputStream().write(data);
		connection.getDataOutputStream().flush();
	}

	private byte[] readBytes(NetworkConnection connection) throws IOException {
		byte[] data = new byte[connection.getDataInputStream().readInt()];
		connection.getDataInputStream().read(data);
		return data;
	}

	private void sendRSAPublicKey(NetworkConnection connection) throws IOException {
		logger.debug("Sending RSA-2048 Public Key");
		byte[] publicKeyEncoded = keyPair.getPublic().getEncoded();
		sendBytes(connection, publicKeyEncoded);
	}

	private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		return keyGen.generateKeyPair();
	}

	@Override
	public void authenticationSuccess(NetworkConnection connection) {
		logger.info("Authenication Success");
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
		byte[] dectyptedData = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);

			// decrypt the text using the private key
			cipher.init(Cipher.DECRYPT_MODE, key);
			dectyptedData = cipher.doFinal(data);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return dectyptedData;
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
}
