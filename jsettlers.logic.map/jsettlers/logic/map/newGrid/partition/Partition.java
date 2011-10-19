package jsettlers.logic.map.newGrid.partition;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.newGrid.partition.manager.PartitionManager;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;

/**
 * This class holds the metadata of a partition.
 * 
 * @author Andreas Eberle
 * 
 */
public class Partition {

	private int counter = 0;
	private final byte player;
	private final PartitionManager manager;

	public Partition(final byte player) {
		this.player = player;
		this.manager = new PartitionManager();
	}

	public void decrement() {
		counter--;
	}

	public void increment() {
		counter++;
	}

	public boolean isEmpty() {
		return counter <= 0;
	}

	public byte getPlayer() {
		return player;
	}

	public int getNumberOfElements() {
		return counter;
	}

	public boolean pushMaterial(ISPosition2D position, EMaterialType materialType) {
		return manager.addOffer(position, materialType);
	}

	public void addJobless(IManageableBearer manageable) {
		manager.addJobless(manageable);
	}

	public void request(ISPosition2D position, EMaterialType materialType, byte priority) {
		manager.request(position, materialType, priority);
	}

}
