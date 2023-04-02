public class ChildNotReferencingParentException extends ParentChildInconsistencyException{
    public ChildNotReferencingParentException(String path, String name) {
        super(path, name);
    }

    @Override
    public String toString() {
        return "ChildNotReferencingParentException{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}