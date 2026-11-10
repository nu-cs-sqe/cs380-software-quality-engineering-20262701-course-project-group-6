package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class NodeTest {

    @Test
    public void Node_Id0HarborNone_InitializesCorrectly() {
        Node node = new Node(0, HarborType.NONE);

        assertEquals(0, node.getId());
        assertEquals(HarborType.NONE, node.getHarborType());
        assertTrue(node.getIncidentEdges().isEmpty());
        assertEquals(Optional.empty(), node.getBuildingColor());
        assertEquals(BuildingType.NONE, node.getBuildingType());
        assertFalse(node.isOccupied());
    }

    @Test
    public void Node_Id53HarborGeneric_InitializesCorrectly() {
        Node node = new Node(53, HarborType.GENERIC_THREE_FOR_ONE);

        assertEquals(53, node.getId());
        assertEquals(HarborType.GENERIC_THREE_FOR_ONE, node.getHarborType());
    }

    @Test
    public void Node_IdMinus1_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Node(-1, HarborType.NONE));
        assertEquals("ID must be between 0 and 53", exception.getMessage());
    }

    @Test
    public void Node_Id54_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Node(54, HarborType.NONE));
        assertEquals("ID must be between 0 and 53", exception.getMessage());
    }

    @Test
    public void Node_HarborNull_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Node(0, null));
        assertTrue(exception.getMessage().contains("harborType cannot be null"));
    }

    @Test
    public void addIncidentEdge_ValidNewEdge_AddsEdgeToList() {
        Node node1 = new Node(0, HarborType.NONE);
        Node node2 = new Node(1, HarborType.NONE);
        Edge edge = new Edge(0, node1, node2);

        node1.addIncidentEdge(edge);

        List<Edge> edges = node1.getIncidentEdges();
        assertEquals(1, edges.size());
        assertEquals(edge, edges.get(0));
    }

    @Test
    public void addIncidentEdge_DuplicateEdge_ThrowsIllegalArgumentException() {
        Node node1 = new Node(0, HarborType.NONE);
        Node node2 = new Node(1, HarborType.NONE);
        Edge edge = new Edge(0, node1, node2);

        node1.addIncidentEdge(edge);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> node1.addIncidentEdge(edge));
        assertTrue(exception.getMessage().contains("Edge cannot be duplicate, already added"));
    }

    @Test
    public void addIncidentEdge_NullEdge_ThrowsIllegalArgumentException() {
        Node node = new Node(0, HarborType.NONE);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> node.addIncidentEdge(null));
        assertTrue(exception.getMessage().contains("Edge cannot be null"));
        assertTrue(node.getIncidentEdges().isEmpty());
    }

    @Test
    public void placeBuilding_ValidInputs_UpdatesState() {
        Node node = new Node(0, HarborType.NONE);

        node.placeBuilding(BuildingType.SETTLEMENT, PlayerColor.BLUE);

        assertEquals(BuildingType.SETTLEMENT, node.getBuildingType());
        assertEquals(Optional.of(PlayerColor.BLUE), node.getBuildingColor());
        assertTrue(node.isOccupied());
    }

    @Test
    public void placeBuilding_NullBuildingColor_ThrowsIllegalArgumentException() {
        Node node = new Node(0, HarborType.NONE);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> node.placeBuilding(BuildingType.SETTLEMENT, null));
        assertEquals("buildingColor cannot be null", exception.getMessage());

        assertEquals(BuildingType.NONE, node.getBuildingType());
        assertEquals(Optional.empty(), node.getBuildingColor());
    }

    @Test
    public void placeBuilding_NullBuildingType_ThrowsIllegalArgumentException() {
        Node node = new Node(0, HarborType.NONE);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> node.placeBuilding(null, PlayerColor.BLUE));
        assertEquals("buildingType cannot be null", exception.getMessage());

        assertEquals(BuildingType.NONE, node.getBuildingType());
        assertEquals(Optional.empty(), node.getBuildingColor());
    }

    @Test
    public void getIncidentEdges_AttemptToModify_ThrowsUnsupportedOperationException() {
        Node node = new Node(0, HarborType.NONE);
        Edge edge1 = new Edge(0, node, new Node(1, HarborType.NONE));
        node.addIncidentEdge(edge1);

        List<Edge> retrievedEdges = node.getIncidentEdges();

        Exception exception = assertThrows(UnsupportedOperationException.class, () -> retrievedEdges.clear());
    }

    @Test
    public void getIncidentEdges_InternalStateChanges_DoesNotAffectPreviousCopy() {
        Node node = new Node(0, HarborType.NONE);
        Edge edge1 = new Edge(0, node, new Node(1, HarborType.NONE));
        node.addIncidentEdge(edge1);

        List<Edge> snapshot = node.getIncidentEdges();
        assertEquals(1, snapshot.size());

        Edge edge2 = new Edge(1, node, new Node(2, HarborType.NONE));
        node.addIncidentEdge(edge2);

        assertEquals(1, snapshot.size());
        assertEquals(2, node.getIncidentEdges().size());
    }
}
