package bisq.network.p2p.storage;

import bisq.network.p2p.storage.payload.ProtectedStorageEntry;

public interface HashMapChangedListener {
    void onAdded(ProtectedStorageEntry data);

    @SuppressWarnings("UnusedParameters")
    void onRemoved(ProtectedStorageEntry data);
}
