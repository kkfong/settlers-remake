package networklib.client.time;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.NetworkConstants.Client;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;
import networklib.channel.ping.IRoundTripTimeSupplier;
import networklib.server.packets.TimeSyncPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TimeSynchronizationListener extends PacketChannelListener<TimeSyncPacket> {

	private final IRoundTripTimeSupplier rttSupplier;
	private final ISynchronizableClock clock;

	public TimeSynchronizationListener(IRoundTripTimeSupplier rttSupplier, ISynchronizableClock clock) {
		super(NetworkConstants.Keys.TIME_SYNC, new GenericDeserializer<TimeSyncPacket>(TimeSyncPacket.class));
		this.rttSupplier = rttSupplier;
		this.clock = clock;
	}

	@Override
	protected void receivePacket(int key, TimeSyncPacket packet) throws IOException {
		int expectedRemoteTime = packet.getTime() + rttSupplier.getRoundTripTime().getRtt() / 2;
		int localTime = clock.getTime();

		int deltaTime = localTime - expectedRemoteTime;

		if (deltaTime > Client.TIME_SYNC_TOLERATED_DIFFERENCE) {
			clock.stopClockFor((int) (deltaTime * Client.TIME_SYNC_APPROACH_FACTOR));
		}
	}

}
