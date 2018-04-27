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

import bisq.network.p2p.storage.payload.ProtectedStorageEntry;

import bisq.common.storage.Storage;

import com.google.inject.name.Named;

import javax.inject.Inject;

import java.io.File;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersistedEntryMapService extends BaseMapStorageService<PersistedEntryMap> {
    private static final String PERSISTED_ENTRY_MAP_FILE_NAME = "PersistedEntryMap";

    private final Set<PersistedEntryMapListener> persistedEntryMapListeners = new CopyOnWriteArraySet<>();


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    public PersistedEntryMapService(@Named(Storage.STORAGE_DIR) File storageDir,
                                    Storage<PersistedEntryMap> persistedEntryMapStorage) {
        super(storageDir, persistedEntryMapStorage);
    }

    synchronized void readFromResources(String postFix) {
        super.readFromResources(PERSISTED_ENTRY_MAP_FILE_NAME, postFix);
    }

    Map<P2PDataStorage.ByteArray, ProtectedStorageEntry> getMap() {
        return persistableEnvelope.getMap();
    }

    void addListener(PersistedEntryMapListener listener) {
        persistedEntryMapListeners.add(listener);
    }

    void removeListener(PersistedEntryMapListener listener) {
        persistedEntryMapListeners.remove(listener);
    }

    @Override
    PersistedEntryMap getPersistableEnvelope() {
        return persistableEnvelope;
    }

    @Override
    protected void notifyListeners() {
        persistedEntryMapListeners.forEach(listener -> getMap().values()
                .forEach(listener::onAdded));
    }

    @Override
    protected PersistedEntryMap createPersistableEnvelope() {
        return new PersistedEntryMap();
    }
}
