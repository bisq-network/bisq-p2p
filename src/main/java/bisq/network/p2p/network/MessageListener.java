package bisq.network.p2p.network;

import bisq.common.proto.network.NetworkEnvelope;

public interface MessageListener {
    void onMessage(NetworkEnvelope networkEnvelop, Connection connection);
}
