package enumerator;

public enum UrlPart {
    URL(0),
    QUERY_STRING(1);
    private final int index;

    UrlPart(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
