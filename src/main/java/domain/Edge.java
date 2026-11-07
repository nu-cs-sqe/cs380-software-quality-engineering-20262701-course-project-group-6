package domain;

import java.util.Optional;

public class Edge {
    private final int id;
    private final Node node1;
    private final Node node2;
    private PlayerColor ownerColor;

    public Edge(int id, Node node1, Node node2) {
        if (id >= 0 && id <= 71) {
            this.id = id;
        }
        else {
            throw new IllegalArgumentException("ID must be between 0 and 71");
        }
        this.node1 = node1;
        this.node2 = node2;
        this.ownerColor = null;
    }

    public int getId() {
        return id;
    }

    public Node getNode1() {
        return node1;
    }

    public Node getNode2() {
        return node2;
    }

    public Optional<PlayerColor> getOwnerColor() {
        return Optional.ofNullable(ownerColor);
    }

    public boolean isOccupied() {
        return ownerColor != null;
    }
}
