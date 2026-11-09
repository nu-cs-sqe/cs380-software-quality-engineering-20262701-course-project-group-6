package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Node {
    private final int id;
    private final HarborType harborType;
    private final List<Edge> incidentEdges;
    private BuildingType buildingType;
    private PlayerColor buildingColor;

    public Node(int id, HarborType harborType) {
        this.id = id;
        this.harborType = harborType;
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
}
