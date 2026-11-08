package domain;

final class ResourceArguments {

    private ResourceArguments() {
    }

    static void requireType(ResourceCardType type) {
        if (type == null) {
            throw new IllegalArgumentException("Resource type is required");
        }
    }

    static void requirePositiveAmount(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Resource amount must be at least 1 (was " + amount + ")");
        }
    }
}
