package org.web3j.quorum.tx;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.quorum.Quorum;
import org.web3j.quorum.methods.request.PrivateTransaction;
import org.web3j.tx.TransactionManager;

/**
 * TransactionManager implementation for using a Quorum node to transact.
 */
public class ClientTransactionManager extends TransactionManager {

    private static final int SLEEP_DURATION = 1000;
    private static final int ATTEMPTS = 20;

    private final Quorum quorum;
    private final String fromAddress;
    private List<String> privateFor;

    public ClientTransactionManager(
            Web3j web3j, String fromAddress, List<String> privateFor,
            int attempts, int sleepDuration) {
        super(web3j, attempts, sleepDuration, fromAddress);
        if (!(web3j instanceof Quorum)) {
            throw new UnsupportedOperationException("Quorum quorum instance must be used");
        }
        this.quorum = (Quorum) web3j;
        this.fromAddress = fromAddress;
        this.privateFor = privateFor;
    }

    public ClientTransactionManager(
            Web3j web3j, String fromAddress, List<String> privateFor) {
        this(web3j, fromAddress, privateFor, ATTEMPTS, SLEEP_DURATION);
    }

    public List<String> getPrivateFor() {
        return privateFor;
    }

    public void setPrivateFor(List<String> privateFor) {
        this.privateFor = privateFor;
    }

    @Override
    public EthSendTransaction sendTransaction(
            BigInteger gasPrice, BigInteger gasLimit, String to,
            String data, BigInteger value)
            throws IOException {

        PrivateTransaction transaction = new PrivateTransaction(
                fromAddress, null, gasLimit, to, value, data, privateFor);

        return quorum.ethSendTransaction(transaction).send();
    }
}
