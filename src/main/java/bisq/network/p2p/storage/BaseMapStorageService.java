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

    protected final Storage<T> storage;
    private final String absolutePathOfStorageDir;

    protected T envelope;

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    public BaseMapStorageService(File storageDir,
                                 Storage<T> storage) {
        this.storage = storage;
        absolutePathOfStorageDir = storageDir.getAbsolutePath();

        storage.setNumMaxBackupFiles(1);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////////////////////

    void persist() {
        storage.queueUpForSave(envelope, 2000);
    }

    T getEnvelope() {
        return envelope;
    }

    abstract public String getFileName();


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Protected
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected void readFromResources(String postFix) {
        makeFileFromResourceFile(getFileName(), postFix);
        readPersistableEnvelope(getFileName());
    }

    protected void makeFileFromResourceFile(String storageFileName, String postFix) {
        String resourceFileName = storageFileName + postFix;
        File dbDir = new File(absolutePathOfStorageDir);
        if (!dbDir.exists() && !dbDir.mkdir())
            log.warn("make dir failed.\ndbDir=" + dbDir.getAbsolutePath());

        final File destinationFile = new File(Paths.get(absolutePathOfStorageDir, storageFileName).toString());
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
        envelope = storage.initAndGetPersistedWithFileName(storageFileName, 100);
        if (envelope != null) {
            log.info("size of {}: {} kb", storageFileName, envelope.toProtoMessage().toByteArray().length / 100D);
        } else {
            envelope = createEnvelope();
        }
    }

    abstract protected T createEnvelope();
}
