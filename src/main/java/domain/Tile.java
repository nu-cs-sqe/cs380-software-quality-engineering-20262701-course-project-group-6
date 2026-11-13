package domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.HashSet;

public class Tile {
    private final int id;
    private final ResourceCardType resourceProduced;
    private final int diceNumber;
    private final List<Node> cornerNodes;

    public Tile(int id, ResourceCardType resourceProduced, int diceNumber,
                Node node1, Node node2, Node node3, Node node4, Node node5, Node node6) {
        if (id >= 0 && id <= 18) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID must be between 0 and 18");
        }

        if (resourceProduced != null) {
            this.resourceProduced = resourceProduced;
        } else {
            throw new IllegalArgumentException("resourceProduced cannot be null");
        }

        if (diceNumber >= 2 && diceNumber != 7 && diceNumber <= 12) {
            this.diceNumber = diceNumber;
        } else {
            throw new IllegalArgumentException("diceNumber must be between 2 and 12, excluding 7");
        }

        if (node1 != null && node2 != null && node3 != null && node4 != null && node5 != null && node6 != null) {
            this.cornerNodes = new ArrayList<>(List.of(node1, node2, node3, node4, node5, node6));
        } else {
            throw new IllegalArgumentException("node cannot be null");
        }
        if (new HashSet<>(this.cornerNodes).size() != 6) {
            throw new IllegalArgumentException("nodes must be distinct");
        }
    }

    // Desert tile constructor
    public Tile(int id, Node node1, Node node2, Node node3, Node node4, Node node5, Node node6) {
        if (id >= 0 && id <= 18) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID must be between 0 and 18");
        }

        if (node1 != null && node2 != null && node3 != null && node4 != null && node5 != null && node6 != null) {
            this.cornerNodes = new ArrayList<>(List.of(node1, node2, node3, node4, node5, node6));
        } else {
            throw new IllegalArgumentException("node cannot be null");
        }

        resourceProduced = null;
        diceNumber = 7;
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
        return cornerNodes;
    }
}
