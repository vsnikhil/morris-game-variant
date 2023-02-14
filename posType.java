/**
 * An enum to store the type of position
 */
public enum posType {
    x('x'),
    W('W'),
    B('B');

    final char val;

    posType(char b) {
        this.val = b;
    }
}
