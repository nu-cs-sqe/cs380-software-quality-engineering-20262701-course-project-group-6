package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

        assertEquals("ID must be between 0 and 71",  exception.getMessage());
    }

    @Test
    public void Edge_Id72_ThrowsIllegalArgumentException() {
        Node node1 = new Node();
        Node node2 = new Node();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Edge(72, node1, node2));

        assertEquals("ID must be between 0 and 71",  exception.getMessage());
    }
}
