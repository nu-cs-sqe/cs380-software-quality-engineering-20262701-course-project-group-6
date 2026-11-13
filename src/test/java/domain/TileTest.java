package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.Optional;

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
        Tile tile = new Tile(0, ResourceCardType.ORE, 2, n1, n2, n3, n4, n5, n6);

        assertEquals(0, tile.getId());
        assertEquals(Optional.of(ResourceCardType.ORE), tile.getResourceProduced());
        assertEquals(2, tile.getDiceNumber());
        assertEquals(6, tile.getCornerNodes().size());
    }

    @Test
    public void Tile_ValidInputsMaxIdAndDiceNumber_InitializesCorrectly() {
        Tile tile = new Tile(18, ResourceCardType.BRICK, 12, n1, n2, n3, n4, n5, n6);

        assertEquals(18, tile.getId());
        assertEquals(Optional.of(ResourceCardType.BRICK), tile.getResourceProduced());
        assertEquals(12, tile.getDiceNumber());
        assertEquals(6, tile.getCornerNodes().size());
    }

    @Test
    public void Tile_IdMinus1_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(-1, ResourceCardType.LUMBER, 6, n1, n2, n3, n4, n5, n6);
        });
        assertEquals("ID must be between 0 and 18", exception.getMessage());
    }

    @Test
    public void Tile_Id19_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(19, ResourceCardType.LUMBER, 6, n1, n2, n3, n4, n5, n6);
        });
        assertEquals("ID must be between 0 and 18", exception.getMessage());
    }

    @Test
    public void Tile_NullResourceProduced_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, null, 6, n1, n2, n3, n4, n5, n6);
        });
        assertEquals("resourceProduced cannot be null", exception.getMessage());
    }

    @Test
    public void Tile_DiceNumber1_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, ResourceCardType.ORE, 1, n1, n2, n3, n4, n5, n6);
        });
        assertEquals("diceNumber must be between 2 and 12, excluding 7", exception.getMessage());
    }

    @Test
    public void Tile_DiceNumber7_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, ResourceCardType.GRAIN, 7, n1, n2, n3, n4, n5, n6);
        });
        assertEquals("diceNumber must be between 2 and 12, excluding 7", exception.getMessage());
    }

    @Test
    public void Tile_DiceNumber13_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Tile(1, ResourceCardType.WOOL, 13, n1, n2, n3, n4, n5, n6);
        });
        assertEquals("diceNumber must be between 2 and 12, excluding 7", exception.getMessage());
    }
}
