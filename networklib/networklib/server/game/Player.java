package networklib.server.game;

import networklib.NetworkConstants;
import networklib.channel.Channel;
import networklib.channel.packet.Packet;
import networklib.client.exceptions.InvalidStateException;
import networklib.server.lockstep.TaskCollectingListener;
import networklib.server.packets.ChatMessagePacket;
import networklib.server.packets.PlayerInfoPacket;
import networklib.server.packets.TimeSyncPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class Player {
	private final PlayerInfoPacket playerInfo;
	private final Channel channel;

	private EPlayerState state = EPlayerState.LOGGED_IN;
	private Match match;

	public Player(PlayerInfoPacket playerInfo, Channel channel) {
		this.playerInfo = playerInfo;
		this.channel = channel;
	}

	public PlayerInfoPacket getPlayerInfo() {
		return playerInfo;
	}

	public String getId() {
		return playerInfo.getId();
	}

	public synchronized void leaveMatch() throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH, EPlayerState.IN_RUNNING_MATCH);

		if (match != null) {
			match.playerLeft(this);
			match = null;
		}
		state = EPlayerState.LOGGED_IN;
	}

	public synchronized void joinMatch(Match match) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.LOGGED_IN);

		this.match = match;
		match.join(this);
		state = EPlayerState.IN_MATCH;
	}

	public Channel getChannel() {
		return channel;
	}

	public void sendPacket(int key, Packet packet) {
		channel.sendPacket(key, packet);
	}

	public synchronized boolean isInMatch() {
		return state == EPlayerState.IN_MATCH || state == EPlayerState.IN_RUNNING_MATCH;
	}

	public void startMatch() throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH);

		match.startMatch();
	}

	void matchStarted(TaskCollectingListener taskListener) {
		state = EPlayerState.IN_RUNNING_MATCH;

		channel.registerListener(taskListener);
	}

	public void forwardChatMessage(ChatMessagePacket packet) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_MATCH, EPlayerState.IN_RUNNING_MATCH);

		match.sendMessage(NetworkConstants.Keys.CHAT_MESSAGE, packet);
	}

	public void distributeTimeSync(TimeSyncPacket packet) throws InvalidStateException {
		EPlayerState.assertState(state, EPlayerState.IN_RUNNING_MATCH);

		match.sendMessage(this, NetworkConstants.Keys.TIME_SYNC, packet);
	}
}
