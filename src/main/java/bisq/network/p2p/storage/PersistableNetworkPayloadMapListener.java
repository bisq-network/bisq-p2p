package bisq.network.p2p.storage;

import bisq.network.p2p.storage.payload.PersistableNetworkPayload;

public interface PersistableNetworkPayloadMapListener {
    void onAdded(PersistableNetworkPayload payload);
}
