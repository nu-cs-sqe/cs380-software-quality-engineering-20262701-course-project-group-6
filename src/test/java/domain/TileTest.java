package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class TileTest {
    private Node n1;
    private Node n2;
    private Node n3;
    private Node n4;
    private Node n5;
    private Node n6;

    @BeforeEach
    void setUp() {
        n1 = new Node(0, HarborType.NONE);
        n2 = new Node(1, HarborType.NONE);
        n3 = new Node(2, HarborType.NONE);
        n4 = new Node(3, HarborType.NONE);
        n5 = new Node(4, HarborType.NONE);
        n6 = new Node(5, HarborType.NONE);
    }

    @Test
    void Tile_ValidInputsMinIdAndDiceNumber_InitializesCorrectly() {
        Tile tile = new Tile(0, ResourceCardType.ORE, 2, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6)));

        assertEquals(0, tile.getId());
        assertEquals(Optional.of(ResourceCardType.ORE), tile.getResourceProduced());
        assertEquals(2, tile.getDiceNumber());
        assertEquals(6, tile.getCornerNodes().size());
    }

    @Test
    public void Tile_ValidInputsMaxIdAndDiceNumber_InitializesCorrectly() {
        Tile tile = new Tile(18, ResourceCardType.BRICK, 12, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6)));

        assertEquals(18, tile.getId());
        assertEquals(Optional.of(ResourceCardType.BRICK), tile.getResourceProduced());
        assertEquals(12, tile.getDiceNumber());
        assertEquals(6, tile.getCornerNodes().size());
    }

    @Test
    public void Tile_IdMinus1_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(-1, ResourceCardType.LUMBER, 6, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6)));
        });
        assertEquals("ID must be between 0 and 18", exception.getMessage());
    }

    @Test
    public void Tile_Id19_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(19, ResourceCardType.LUMBER, 6, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6)));
        });
        assertEquals("ID must be between 0 and 18", exception.getMessage());
    }

    @Test
    public void Tile_NullResourceProduced_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, null, 6, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6)));
        });
        assertEquals("resourceProduced cannot be null", exception.getMessage());
    }

    @Test
    public void Tile_DiceNumber1_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, ResourceCardType.ORE, 1, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6)));
        });
        assertEquals("diceNumber must be between 2 and 12, excluding 7", exception.getMessage());
    }

    @Test
    public void Tile_DiceNumber7_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, ResourceCardType.GRAIN, 7, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6)));
        });
        assertEquals("diceNumber must be between 2 and 12, excluding 7", exception.getMessage());
    }

    @Test
    public void Tile_DiceNumber13_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, ResourceCardType.WOOL, 13, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6)));
        });
        assertEquals("diceNumber must be between 2 and 12, excluding 7", exception.getMessage());
    }

    @Test
    public void Tile_NullCornerNode_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, ResourceCardType.LUMBER, 6, new ArrayList<>(Arrays.asList(n1, n2, null, n4, n5, n6)));
        });
        assertEquals("nodes cannot be null", exception.getMessage());
    }

    @Test
    public void Tile_DuplicateCornerNodes_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, ResourceCardType.LUMBER, 6, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n2, n6)));
        });
        assertEquals("nodes must be distinct", exception.getMessage());
    }

    @Test
    public void Tile_DesertId0_InitializesCorrectly() {
        Tile tile = new Tile(0, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6)));

        assertEquals(0, tile.getId());
        assertEquals(Optional.empty(), tile.getResourceProduced());
        assertEquals(7, tile.getDiceNumber());
        assertEquals(6, tile.getCornerNodes().size());
    }

    @Test
    public void Tile_DesertId18_InitializesCorrectly() {
        Tile tile = new Tile(18, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6)));

        assertEquals(18, tile.getId());
        assertEquals(Optional.empty(), tile.getResourceProduced());
        assertEquals(7, tile.getDiceNumber());
        assertEquals(6, tile.getCornerNodes().size());
    }

    @Test
    public void Tile_DesertIdMinus1_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(-1, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6)));
        });
        assertEquals("ID must be between 0 and 18", exception.getMessage());
    }

    @Test
    public void Tile_DesertId19_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(19, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6)));
        });
        assertEquals("ID must be between 0 and 18", exception.getMessage());
    }

    @Test
    public void Tile_NullDesertCornerNode_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(9, new ArrayList<>(Arrays.asList(null, n2, n3, n4, n5, n6)));
        });
        assertEquals("nodes cannot be null", exception.getMessage());
    }

    @Test
    public void Tile_DesertDuplicateCornerNodes_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(9, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n4)));
        });
        assertEquals("nodes must be distinct", exception.getMessage());
    }

    @Test
    public void getCornerNodes_AttemptToModify_ThrowsUnsupportedOperationException() {
        Tile tile = new Tile(1, ResourceCardType.ORE, 6, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6)));
        List<Node> retrievedNodes = tile.getCornerNodes();

        assertThrows(UnsupportedOperationException.class, () -> retrievedNodes.clear());
    }

    @Test
    public void Tile_5CornerNodes_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, ResourceCardType.LUMBER, 6, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5)));
        });
        assertEquals("cornerNodes must have exactly 6 corner nodes", exception.getMessage());
    }

    @Test
    public void Tile_7CornerNodes_ThrowsIllegalArgumentException() {
        Node n7 = new Node(6, HarborType.NONE);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, ResourceCardType.LUMBER, 6, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6, n7)));
        });
        assertEquals("cornerNodes must have exactly 6 corner nodes", exception.getMessage());
    }

    @Test
    public void Tile_NullCornerNodes_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, ResourceCardType.LUMBER, 6, null);
        });
        assertEquals("cornerNodes must have exactly 6 corner nodes", exception.getMessage());
    }

    @Test
    public void Tile_Desert5CornerNodes_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5)));
        });
        assertEquals("cornerNodes must have exactly 6 corner nodes", exception.getMessage());
    }

    @Test
    public void Tile_Desert7CornerNodes_ThrowsIllegalArgumentException() {
        Node n7 = new Node(6, HarborType.NONE);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, new ArrayList<>(Arrays.asList(n1, n2, n3, n4, n5, n6, n7)));
        });
        assertEquals("cornerNodes must have exactly 6 corner nodes", exception.getMessage());
    }
}
