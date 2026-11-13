package domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.HashSet;

public class Tile {
    private static final int MAX_NODE_ID = 18;
    private static final int MIN_DICE_NUM = 2;
    private static final int MAX_DICE_NUM = 12;
    private static final int DESERT_DICE_NUM = 7;
    private static final int NUM_CORNER_NODES = 6;
    private final int id;
    private final ResourceCardType resourceProduced;
    private final int diceNumber;
    private final List<Node> cornerNodes;

    public Tile(int id, ResourceCardType resourceProduced, int diceNumber, List<Node> cornerNodes) {
        if (id >= 0 && id <= MAX_NODE_ID) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID must be between 0 and 18");
        }

        if (resourceProduced != null) {
            this.resourceProduced = resourceProduced;
        } else {
            throw new IllegalArgumentException("resourceProduced cannot be null");
        }

        if (diceNumber >= MIN_DICE_NUM && diceNumber != DESERT_DICE_NUM && diceNumber <= MAX_DICE_NUM) {
            this.diceNumber = diceNumber;
        } else {
            throw new IllegalArgumentException("diceNumber must be between 2 and 12, excluding 7");
        }

        validateCornerNodes(cornerNodes);
        this.cornerNodes = List.copyOf(cornerNodes);
    }

    // Desert tile constructor
    public Tile(int id, List<Node> cornerNodes) {
        if (id >= 0 && id <= MAX_NODE_ID) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID must be between 0 and 18");
        }

        validateCornerNodes(cornerNodes);
        this.cornerNodes = List.copyOf(cornerNodes);

        resourceProduced = null;
        diceNumber = DESERT_DICE_NUM;
    }

    public int getId() {
        return id;
    }

    public Optional<ResourceCardType> getResourceProduced() {
        return  Optional.ofNullable(resourceProduced);
    }

    public int getDiceNumber() {
        return diceNumber;
    }

    public List<Node> getCornerNodes() {
        return List.copyOf(cornerNodes);
    }

    private void validateCornerNodes(List<Node> nodes) {
        if (nodes == null || nodes.size() != NUM_CORNER_NODES) {
            throw new IllegalArgumentException("cornerNodes must have exactly 6 corner nodes");
        }

        for (Node node : nodes) {
            if (node == null) {
                throw new IllegalArgumentException("nodes cannot be null");
            }
        }

        if (new HashSet<>(nodes).size() != NUM_CORNER_NODES) {
            throw new IllegalArgumentException("nodes must be distinct");
        }
    }

}
