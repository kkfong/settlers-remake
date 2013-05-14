package networklib.channel.ping;

import networklib.NetworkConstants;
import networklib.channel.Channel;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;

/**
 * {@link PacketChannelListener} to receive and send {@link PingPacket}s.
 * 
 * @author Andreas Eberle
 * 
 */
public class PingPacketListener extends PacketChannelListener<PingPacket> implements IRoundTripTimeSupplier {

	private final Channel channel;
	private RoundTripTime currRtt;

	public PingPacketListener(Channel channel) {
		super(NetworkConstants.Keys.PING, new GenericDeserializer<PingPacket>(PingPacket.class));

		this.channel = channel;
		channel.registerListener(this);
	}

	@Override
	protected void receivePacket(int key, PingPacket receivedPing) {
		long now = System.currentTimeMillis();
		int rtt = (int) (now - receivedPing.getReceiverTime());
		currRtt = new RoundTripTime(now, rtt);

		sendPing(receivedPing.getSenderTime());
	}

	private void sendPing(long receiverTime) {
		channel.sendPacket(NetworkConstants.Keys.PING, new PingPacket(System.currentTimeMillis(), receiverTime));
	}

	/**
	 * Gets the round trip time of this {@link Channel}.
	 * 
	 * @return Returns the current {@link RoundTripTime}.
	 */
	public RoundTripTime getRoundTripTime() {
		return currRtt;
	}

	/**
	 * Initialize the pinging by sending a first {@link PingPacket}.
	 */
	public void initPinging() {
		sendPing(0);
	}
}
