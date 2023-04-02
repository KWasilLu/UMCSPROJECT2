public class ParentNotReferencingChildException extends ParentChildInconsistencyException{
    public ParentNotReferencingChildException(String path, String name) {
        super(path, name);
    }

    @Override
    public String toString() {
        return "ParentNotReferencingChildException{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}