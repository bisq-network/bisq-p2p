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

package bisq.network.p2p.peers.peerexchange;

import bisq.network.p2p.NodeAddress;

import bisq.common.proto.network.NetworkPayload;
import bisq.common.proto.persistable.PersistablePayload;

import io.bisq.generated.protobuffer.PB;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(exclude = {"date"})
@ToString
public final class Peer implements NetworkPayload, PersistablePayload {
    private static final int MAX_FAILED_CONNECTION_ATTEMPTS = 5;

    private final NodeAddress nodeAddress;
    private final long date;
    @Setter
    private int failedConnectionAttempts = 0;

    public Peer(NodeAddress nodeAddress) {
        this(nodeAddress, new Date().getTime());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private Peer(NodeAddress nodeAddress, long date) {
        this.nodeAddress = nodeAddress;
        this.date = date;
    }

    @Override
    public PB.Peer toProtoMessage() {
        return PB.Peer.newBuilder()
                .setNodeAddress(nodeAddress.toProtoMessage())
                .setDate(date)
                .build();
    }

    public static Peer fromProto(PB.Peer peer) {
        return new Peer(NodeAddress.fromProto(peer.getNodeAddress()),
                peer.getDate());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void increaseFailedConnectionAttempts() {
        this.failedConnectionAttempts++;
    }

    public boolean tooManyFailedConnectionAttempts() {
        return failedConnectionAttempts >= MAX_FAILED_CONNECTION_ATTEMPTS;
    }

    public Date getDate() {
        return new Date(date);
    }
}
