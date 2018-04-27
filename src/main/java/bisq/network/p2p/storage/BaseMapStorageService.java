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

import bisq.common.proto.persistable.PersistableEnvelope;
import bisq.common.storage.FileUtil;
import bisq.common.storage.ResourceNotFoundException;
import bisq.common.storage.Storage;

import java.nio.file.Paths;

import java.io.File;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseMapStorageService<T extends PersistableEnvelope> {

    private final File storageDir;
    protected final Storage<T> storage;

    protected T persistableEnvelope;

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    public BaseMapStorageService(File storageDir,
                                 Storage<T> storage) {
        this.storageDir = storageDir;
        this.storage = storage;

        storage.setNumMaxBackupFiles(1);
    }

    void persist() {
        storage.queueUpForSave(persistableEnvelope, 2000);
    }

    protected void readFromResources(String storageFileName, String postFix) {
        makeFileFromResourceFile(storageFileName, postFix, storageDir);
        readPersistableEnvelope(storageFileName);
    }

    protected void makeFileFromResourceFile(String storageFileName, String postFix, File storageDir) {
        String resourceFileName = storageFileName + postFix;
        File dbDir = new File(storageDir.getAbsolutePath());
        if (!dbDir.exists() && !dbDir.mkdir())
            log.warn("make dir failed.\ndbDir=" + dbDir.getAbsolutePath());

        final File destinationFile = new File(Paths.get(storageDir.getAbsolutePath(), storageFileName).toString());
        if (!destinationFile.exists()) {
            try {
                log.info("We copy resource to file: resourceFileName={}, destinationFile={}", resourceFileName, destinationFile);
                FileUtil.resourceToFile(resourceFileName, destinationFile);
            } catch (ResourceNotFoundException e) {
                log.info("Could not find resourceFile " + resourceFileName + ". That is expected if none is provided yet.");
            } catch (Throwable e) {
                log.error("Could not copy resourceFile " + resourceFileName + " to " +
                        destinationFile.getAbsolutePath() + ".\n" + e.getMessage());
                e.printStackTrace();
            }
        } else {
            log.debug(storageFileName + " file exists already.");
        }
    }


    protected void readPersistableEnvelope(String storageFileName) {
        persistableEnvelope = storage.initAndGetPersistedWithFileName(storageFileName, 100);
        if (persistableEnvelope != null) {
            log.info("size of {}: {} kb", storageFileName, persistableEnvelope.toProtoMessage().toByteArray().length / 100D);
            notifyListeners();
        } else {
            persistableEnvelope = createPersistableEnvelope();
        }
    }

    abstract protected void notifyListeners();

    abstract T getPersistableEnvelope();

    abstract protected T createPersistableEnvelope();
}
