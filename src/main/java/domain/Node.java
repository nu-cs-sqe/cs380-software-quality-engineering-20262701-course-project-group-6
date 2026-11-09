package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Node {
    private static final int MAX_NODE_ID = 53;
    private final int id;
    private final HarborType harborType;
    private final List<Edge> incidentEdges;
    private BuildingType buildingType;
    private PlayerColor buildingColor;

    public Node(int id, HarborType harborType) {
        if (id >= 0 && id <= MAX_NODE_ID) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("ID must be between 0 and 53");
        }

        if (harborType != null) {
            this.harborType = harborType;
        } else {
            throw new IllegalArgumentException("harborType cannot be null");
        }

        this.incidentEdges = new ArrayList<>();
        this.buildingType = BuildingType.NONE;
        this.buildingColor = null;
    }

    public int getId() {
        return id;
    }

    public HarborType getHarborType() {
        return harborType;
    }

    public List<Edge> getIncidentEdges() {
        return incidentEdges;
    }

    public Optional<PlayerColor> getBuildingColor() {
        return Optional.ofNullable(buildingColor);
    }

    public boolean isOccupied() {
        return buildingType != BuildingType.NONE;
    }

    void addIncidentEdge(Edge incidentEdge) {
        if (incidentEdges.contains(incidentEdge)) {
            throw new IllegalArgumentException("Edge cannot be duplicate, already added");
        }
        incidentEdges.add(incidentEdge);
    }
}
