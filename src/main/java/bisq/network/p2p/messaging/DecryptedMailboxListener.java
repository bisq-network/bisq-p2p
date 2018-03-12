package bisq.network.p2p.messaging;


import bisq.network.p2p.DecryptedMessageWithPubKey;
import bisq.network.p2p.NodeAddress;
import bisq.network.p2p.DecryptedMessageWithPubKey;
import bisq.network.p2p.NodeAddress;

public interface DecryptedMailboxListener {

    void onMailboxMessageAdded(DecryptedMessageWithPubKey decryptedMessageWithPubKey, @SuppressWarnings("UnusedParameters") NodeAddress senderNodeAddress);
}
