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
        if (id >= 0) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID must be between 0 and 18");
        }

        this.resourceProduced = resourceProduced;
        this.diceNumber = diceNumber;
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
