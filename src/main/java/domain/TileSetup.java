package domain;

import java.util.Optional;
import java.util.OptionalInt;

public final class TileSetup {
    private final Optional<ResourceCardType> resource;
    private final OptionalInt diceNumber;

    TileSetup(ResourceCardType resource, int diceNumber) {
        this.resource = Optional.of(resource);
        this.diceNumber = OptionalInt.of(diceNumber);
    }

    TileSetup() {
        resource = Optional.empty();
        diceNumber = OptionalInt.empty();
    }

    public Optional<ResourceCardType> getResource() {
        return resource;
    }

    public OptionalInt getDiceNumber() {
        return diceNumber;
    }
}