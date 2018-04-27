/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.network.p2p.storage;

import bisq.network.p2p.storage.payload.PersistableNetworkPayload;

import bisq.common.storage.Storage;

import com.google.inject.name.Named;

import javax.inject.Inject;

import java.io.File;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersistableNetworkPayloadMapService extends BaseMapStorageService<PersistableNetworkPayloadList> {
    private static final String PERSISTABLE_NETWORK_PAYLOAD_MAP_FILE_NAME = "PersistableNetworkPayloadMap";

    private final Set<PersistableNetworkPayloadMapListener> persistableNetworkPayloadMapListeners = new CopyOnWriteArraySet<>();


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    public PersistableNetworkPayloadMapService(@Named(Storage.STORAGE_DIR) File storageDir,
                                               Storage<PersistableNetworkPayloadList> persistableNetworkPayloadMapStorage) {
        super(storageDir, persistableNetworkPayloadMapStorage);
    }

    void readFromResources(String postFix) {
        super.readFromResources(PERSISTABLE_NETWORK_PAYLOAD_MAP_FILE_NAME, postFix);
    }

    Map<P2PDataStorage.ByteArray, PersistableNetworkPayload> getMap() {
        return persistableEnvelope.getMap();
    }

    void addListener(PersistableNetworkPayloadMapListener listener) {
        persistableNetworkPayloadMapListeners.add(listener);
    }

    void removeListener(PersistableNetworkPayloadMapListener listener) {
        persistableNetworkPayloadMapListeners.remove(listener);
    }

    @Override
    protected void notifyListeners() {
        persistableNetworkPayloadMapListeners.forEach(listener -> getMap().values()
                .forEach(listener::onAdded));
    }

    @Override
    PersistableNetworkPayloadList getPersistableEnvelope() {
        return persistableEnvelope;
    }

    @Override
    protected PersistableNetworkPayloadList createPersistableEnvelope() {
        return new PersistableNetworkPayloadList();
    }
}
