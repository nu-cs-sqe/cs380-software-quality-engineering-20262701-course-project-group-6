package domain;

import java.util.Optional;

public class Edge {
    private final int id;
    private final Node node1;
    private final Node node2;
    private PlayerColor roadColor;

    public Edge(int id, Node node1, Node node2) {
        if (id >= 0 && id <= 71) {
            this.id = id;
        }
        else {
            throw new IllegalArgumentException("ID must be between 0 and 71");
        }

        if (node1 != null && node2 != null) {
            this.node1 = node1;
            this.node2 = node2;
        }
        else {
            throw new IllegalArgumentException("Node cannot be null");
        }

        this.roadColor = null;
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

    public Optional<PlayerColor> getRoadColor() {
        return Optional.ofNullable(roadColor);
    }

    public boolean isOccupied() {
        return roadColor != null;
    }

    void setRoadColor(PlayerColor roadColor) {
        if  (roadColor != null) {
            this.roadColor = roadColor;
        }
        else {
            throw new IllegalArgumentException("roadColor cannot be null");
        }
    }
}
