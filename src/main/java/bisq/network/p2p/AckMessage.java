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

package bisq.network.p2p;

import bisq.network.p2p.storage.payload.CapabilityRequiringPayload;
import bisq.network.p2p.storage.payload.ExpirablePayload;

import bisq.common.app.Capabilities;
import bisq.common.app.Version;
import bisq.common.proto.ProtoUtil;
import bisq.common.proto.network.NetworkEnvelope;

import io.bisq.generated.protobuffer.PB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.EqualsAndHashCode;
import lombok.Value;

import javax.annotation.Nullable;

@EqualsAndHashCode(callSuper = true)
@Value
public final class AckMessage extends NetworkEnvelope implements MailboxMessage, ExpirablePayload, CapabilityRequiringPayload {
    private final String uid;
    private final NodeAddress senderNodeAddress;
    private final AckMessageSourceType sourceType;       //e.g. TradeMessage, DisputeMessage,...
    private final String sourceMsgClassName;
    private final String sourceUid;     // uid of source (TradeMessage)
    private final String sourceId;      // id of source (tradeId, disputeId)
    private final boolean result;       // true if source message was processed successfully
    @Nullable
    private final String errorMessage;  // optional error message if source message processing failed

    public AckMessage(NodeAddress senderNodeAddress,
                      AckMessageSourceType sourceType,
                      String sourceMsgClassName,
                      String sourceUid,
                      String sourceId,
                      boolean result,
                      String errorMessage) {
        this(UUID.randomUUID().toString(),
                senderNodeAddress,
                sourceType,
                sourceMsgClassName,
                sourceUid,
                sourceId,
                result,
                errorMessage,
                Version.getP2PMessageVersion());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////

    private AckMessage(String uid,
                       NodeAddress senderNodeAddress,
                       AckMessageSourceType sourceType,
                       String sourceMsgClassName,
                       String sourceUid,
                       String sourceId,
                       boolean result,
                       @Nullable String errorMessage,
                       int messageVersion) {
        super(messageVersion);
        this.uid = uid;
        this.senderNodeAddress = senderNodeAddress;
        this.sourceType = sourceType;
        this.sourceMsgClassName = sourceMsgClassName;
        this.sourceUid = sourceUid;
        this.sourceId = sourceId;
        this.result = result;
        this.errorMessage = errorMessage;
    }


    @Override
    public PB.NetworkEnvelope toProtoNetworkEnvelope() {
        PB.AckMessage.Builder builder = PB.AckMessage.newBuilder()
                .setUid(uid)
                .setSenderNodeAddress(senderNodeAddress.toProtoMessage())
                .setSourceType(sourceType.name())
                .setSourceMsgClassName(sourceMsgClassName)
                .setSourceUid(sourceUid)
                .setSourceId(sourceId)
                .setResult(result);
        Optional.ofNullable(errorMessage).ifPresent(builder::setErrorMessage);
        return getNetworkEnvelopeBuilder().setAckMessage(builder).build();
    }

    public static AckMessage fromProto(PB.AckMessage proto, int messageVersion) {
        AckMessageSourceType sourceType = ProtoUtil.enumFromProto(AckMessageSourceType.class, proto.getSourceType());
        return new AckMessage(proto.getUid(),
                NodeAddress.fromProto(proto.getSenderNodeAddress()),
                sourceType,
                proto.getSourceMsgClassName(),
                proto.getSourceUid(),
                proto.getSourceId(),
                proto.getResult(),
                proto.getErrorMessage().isEmpty() ? null : proto.getErrorMessage(),
                messageVersion);
    }

    @Override
    public List<Integer> getRequiredCapabilities() {
        return new ArrayList<>(Collections.singletonList(
                Capabilities.Capability.ACK_MSG.ordinal()
        ));
    }


    @Override
    public String toString() {
        return "AckMessage{" +
                "\n     uid='" + uid + '\'' +
                ",\n     senderNodeAddress=" + senderNodeAddress +
                ",\n     sourceType=" + sourceType +
                ",\n     sourceMsgClassName='" + sourceMsgClassName + '\'' +
                ",\n     sourceUid='" + sourceUid + '\'' +
                ",\n     sourceId='" + sourceId + '\'' +
                ",\n     result=" + result +
                ",\n     errorMessage='" + errorMessage + '\'' +
                "\n} " + super.toString();
    }

    @Override
    public long getTTL() {
        return TimeUnit.DAYS.toMillis(10);
    }
}
