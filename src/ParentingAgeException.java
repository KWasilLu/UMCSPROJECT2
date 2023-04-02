public class ParentingAgeException extends RuntimeException{
    private static int minAge = 15;
    private static int maxAge = 50;
    public final TemporaryPerson parent;
    public final TemporaryPerson child;

    private int age;
    public ParentingAgeException(int age, TemporaryPerson parent, TemporaryPerson child){
        this.parent=parent;
        this.child=child;
        this.age = age;
    }

    @Override
    public String toString() {
        return "ParentingAgeException{" +
                ", pathParent='" + parent + '\'' +
                ", pathChild='" + child + '\'' +
                '}';
    }

    public String getPathParent() {
        return parent.getPath();
    }
    public String getPathChild() {
        return child.getPath();
    }

    public static boolean checkAge(int age) {
        return minAge < age && age < maxAge;
    }

    public static void setMinAge(int minAge) {
        ParentingAgeException.minAge = minAge;
    }

    public static void setMaxAge(int maxAge) {
        ParentingAgeException.maxAge = maxAge;
    }

    public int getAge() {
        return age;
    }
}
