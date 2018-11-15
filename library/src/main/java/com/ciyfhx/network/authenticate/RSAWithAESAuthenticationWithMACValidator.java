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

import com.ciyfhx.network.NetworkConnection;
import com.ciyfhx.validator.MACValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

public class RSAWithAESAuthenticationWithMACValidator extends RSAWithAESAuthentication {

    private Logger logger = LoggerFactory.getLogger(RSAWithAESAuthenticationWithMACValidator.class);

    private MACValidator validator = new MACValidator();

    @Override
    public boolean serverAuthenticate(NetworkConnection connection) {
        boolean success = super.serverAuthenticate(connection);
        if(success){
            byte[] salt = generateSaltAndSend(connection);
            validator.setSecret(ByteBuffer.wrap(salt));
            logger.debug("Sent validator salt");

            byte[] senderSalt = receiveSalt(connection);
            validator.setSenderSecret(ByteBuffer.wrap(senderSalt));
            logger.debug("Done receiving validator salt");

            return true;
        }else return false;
    }

    @Override
    public boolean clientAuthenticate(NetworkConnection connection) {
        boolean success = super.clientAuthenticate(connection);
        if(success){
            byte[] senderSalt = receiveSalt(connection);
            validator.setSecret(ByteBuffer.wrap(senderSalt));
            logger.debug("Done receiving validator salt");

            byte[] salt = generateSaltAndSend(connection);
            validator.setSenderSecret(ByteBuffer.wrap(salt));
            logger.debug("Sent validator salt");

            return true;
        }else return false;
    }

    protected byte[] generateSaltAndSend(NetworkConnection connection) {
        try {
            //Generate salt and send
            byte[] salt = super.randomSecureBytes(64);
            byte[] encryptedSalt = encrypt(salt, super.getSenderPublicKey());
            super.sendBytes(connection, encryptedSalt);
            return salt;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("Not able to generate salt");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Not able to send salt");
        }
        return null;
    }

    protected byte[] receiveSalt(NetworkConnection connection) {
        //Receive salt
        try {
            byte[] receiveSalt = decrypt(super.readBytes(connection), super.getKeyPair().getPrivate());
            return receiveSalt;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Not able to receive salt");

        }
        return null;
    }

    @Override
    public void authenticationSuccess(NetworkConnection connection) {
        super.authenticationSuccess(connection);
        connection.getPipeLineStream().addPipeLine(validator);
    }

    public MACValidator getValidator() {
        return validator;
    }
}