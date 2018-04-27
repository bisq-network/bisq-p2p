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

import bisq.common.proto.persistable.PersistablePayload;
import bisq.common.storage.FileUtil;
import bisq.common.storage.Storage;

import com.google.inject.name.Named;

import javax.inject.Inject;

import java.nio.file.Paths;

import java.io.File;
import java.io.IOException;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * Not used anymore. We still need the class for supporting the transfer of th old data structure to the new.
 * Can be removed at the next hard fork.
 */
@Deprecated
@Slf4j
public final class PersistableNetworkPayloadMapService extends BaseMapStorageService<PersistableNetworkPayloadList, PersistableNetworkPayload> {
    public static final String FILE_NAME = "PersistableNetworkPayloadMap";


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    public PersistableNetworkPayloadMapService(@Named(Storage.STORAGE_DIR) File storageDir,
                                               Storage<PersistableNetworkPayloadList> persistableNetworkPayloadMapStorage) {
        super(storageDir, persistableNetworkPayloadMapStorage);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void removeFile() {
        final File file = new File(Paths.get(absolutePathOfStorageDir, getFileName()).toString());
        if (file.exists())
            log.info("Remove deprecated file " + file.getAbsolutePath());
        try {
            FileUtil.deleteFileIfExists(file);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.toString());
        }
    }

    @Override
    public PersistableNetworkPayload putIfAbsent(P2PDataStorage.ByteArray hash, PersistableNetworkPayload payload) {
        throw new RuntimeException("putIfAbsent must not be called on deprecated PersistableNetworkPayloadMapService");
    }

    @Override
    protected void persist() {
        throw new RuntimeException("persist must not be called on deprecated PersistableNetworkPayloadMapService");
    }

    @Override
    public PersistableNetworkPayload remove(P2PDataStorage.ByteArray hash) {
        throw new RuntimeException("remove must not be called on deprecated PersistableNetworkPayloadMapService");
    }

    @Override
    public String getFileName() {
        return FILE_NAME;
    }

    @Override
    public Map<P2PDataStorage.ByteArray, PersistableNetworkPayload> getMap() {
        return envelope.getMap();
    }

    @Override
    public boolean isMyPayload(PersistablePayload payload) {
        return payload instanceof PersistableNetworkPayload;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Protected
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected PersistableNetworkPayloadList createEnvelope() {
        return new PersistableNetworkPayloadList();
    }
}
