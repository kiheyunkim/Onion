package enumerator;

public enum RequestUrlPart {
    METHOD(0),
    URL_PART(1),
    PROTOCOL(2);
    private final int index;

    RequestUrlPart(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
