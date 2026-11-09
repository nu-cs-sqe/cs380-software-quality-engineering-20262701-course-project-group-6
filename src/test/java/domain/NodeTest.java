package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertFalse(node.isOccupied());
    }

    @Test
    public void Node_Id53HarborGeneric_InitializesCorrectly() {
        Node node = new Node(53, HarborType.GENERIC_THREE_FOR_ONE);

        assertEquals(53, node.getId());
        assertEquals(HarborType.GENERIC_THREE_FOR_ONE, node.getHarborType());
    }
}
