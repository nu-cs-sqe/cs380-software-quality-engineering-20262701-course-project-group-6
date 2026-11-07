package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.util.Optional;

public class EdgeTest {

    @Test
    public void Edge_ValidInputs_InitializesCorrectly() {
        Node node1 = new Node();
        Node node2 = new Node();
        Edge edge = new Edge(0, node1, node2);

        assertEquals(0, edge.getId());
        assertEquals(node1, edge.getNode1());
        assertEquals(node2, edge.getNode2());
        assertEquals(Optional.empty(), edge.getOwnerColor());
        assertFalse(edge.isOccupied());
    }

    @Test
    public void Edge_Id71_InitializesCorrectly() {
        Node node1 = new Node();
        Node node2 = new Node();
        Edge edge = new Edge(71, node1, node2);

        assertEquals(71, edge.getId());
    }

    @Test
    public void Edge_IdMinusOne_ThrowsIllegalArgumentException() {
        Node node1 = new Node();
        Node node2 = new Node();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Edge(-1, node1, node2));

        assertEquals("ID must be between 0 and 71", exception.getMessage());
    }

    @Test
    public void Edge_Id72_ThrowsIllegalArgumentException() {
        Node node1 = new Node();
        Node node2 = new Node();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Edge(72, node1, node2));

        assertEquals("ID must be between 0 and 71", exception.getMessage());
    }

    @Test
    public void Edge_Node1Null_ThrowsIllegalArgumentException() {
        Node node2 = new Node();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Edge(0, null, node2));

        assertEquals("Node cannot be null", exception.getMessage());
    }

    @Test
    public void Edge_Node2Null_ThrowsIllegalArgumentException() {
        Node node1 = new Node();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Edge(0, node1, null));

        assertEquals("Node cannot be null", exception.getMessage());
    }

    @Test
    public void setOwnerColor_ValidColor_UpdatesOwner() {
        Edge edge = new Edge(0, new Node(), new Node());

        edge.setOwnerColor(PlayerColor.BLUE);

        assertEquals(Optional.of(PlayerColor.BLUE), edge.getOwnerColor());
    }

    @Test
    public void setOwnerColor_null_ThrowsIllegalArgumentException() {
        Edge edge = new Edge(0, new Node(), new Node());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> edge.setOwnerColor(null));

        assertEquals("ownerColor cannot be null", exception.getMessage());
    }

    @Test
    public void isOccupied_OwnerNull_ReturnsFalse() {
        Edge edge = new Edge(0, new Node(), new Node());

        assertFalse(edge.isOccupied());
    }

    @Test
    public void isOccupied_OwnerAssigned_ReturnsTrue() {
        Edge edge = new Edge(0, new Node(), new Node());
        edge.setOwnerColor(PlayerColor.BLUE);

        assertTrue(edge.isOccupied());
    }
}
