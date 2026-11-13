package domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

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

        if (diceNumber >= 2 && diceNumber != 7) {
            this.diceNumber = diceNumber;
        } else {
            throw new IllegalArgumentException("diceNumber must be between 2 and 12, excluding 7");
        }

        this.cornerNodes = new ArrayList<>(List.of(node1, node2, node3, node4, node5, node6));
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
