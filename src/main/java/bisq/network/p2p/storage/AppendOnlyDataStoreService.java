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

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppendOnlyDataStoreService {

    private final PersistableNetworkPayloadList persistableNetworkPayloadList = new PersistableNetworkPayloadList();
    private List<PersistableNetworkPayloadMapService> services = new ArrayList<>();


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    public AppendOnlyDataStoreService(PersistableNetworkPayloadMapService persistableNetworkPayloadMapService) {
        // We add our default service
        addService(persistableNetworkPayloadMapService);
    }

    public void addService(PersistableNetworkPayloadMapService service) {
        services.add(service);
    }

    void readFromResources(String postFix) {
        services.forEach(service -> service.readFromResources(postFix));
    }

    PersistableNetworkPayloadList getPersistableNetworkPayloadMap() {
        Map<P2PDataStorage.ByteArray, PersistableNetworkPayload> map = getMap();
        services.stream()
                .flatMap(service -> service.getMap().entrySet().stream())
                .forEach(entry -> map.put(entry.getKey(), entry.getValue()));
        return persistableNetworkPayloadList;
    }

    public Map<P2PDataStorage.ByteArray, PersistableNetworkPayload> getMap() {
        return persistableNetworkPayloadList.getMap();
    }

    public void put(P2PDataStorage.ByteArray hashAsByteArray, PersistableNetworkPayload payload) {
        services.stream()
                /* .filter(service -> !service.getFileName().equals(PersistableNetworkPayloadMapService.FILE_NAME))*/
                .filter(service -> service.isMyPayload(payload))
                .forEach(service -> {
                    service.putIfAbsent(hashAsByteArray, payload);
                })
        ;
    }
}
