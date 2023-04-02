import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TemporaryPerson {
    private enum FileExpectation{ExpectsDeath, ExpectsParent, ExpectsChild}
    public Person person;
    private String path;
    private List<String> parentNames;
    private List<String> childrenNames;

    public TemporaryPerson(String name, LocalDate birth, LocalDate death, String path, List<String> parentNames, List<String> childrenNames) {
        this.person = new Person(name, birth, death);
        this.path = path;
        this.parentNames = parentNames;
        this.childrenNames = childrenNames;
    }

    public String getPath() {
        return path;

    }

    public List<String> getParentNames() {
        return parentNames;
    }

    public void convert(Map<String, TemporaryPerson> temporaryMap) {

        for (String parentName : parentNames) {
            try {
                TemporaryPerson parent = temporaryMap.get(parentName);
                if (!parent.childrenNames.contains(this.person.getName()))
                    throw new ParentNotReferencingChildException(path, person.getName());
                int age = person.getBirth().getYear() - parent.person.getBirth().getYear();
                if (!ParentingAgeException.checkAge(age))
                    throw new ParentingAgeException(age, this, parent);
            }
            catch(ParentingAgeException e) {
                Scanner scanner = new Scanner(System.in);
                System.out.println(String.format("Child %s was born when parent %s was %d years old. Please confirm [Y/N]:",
                        e.child.person.getName(), e.parent.person.getName(), e.getAge()));
                String response = scanner.nextLine();
                if(response == "Y")
                    this.person.parents.add(e.parent.person);
            }
        }
    }

    public void convertChildren(Map<String, TemporaryPerson> temporaryMap) {
        for (String childName : childrenNames) {
            TemporaryPerson child = temporaryMap.get(childName);
            if (!child.parentNames.contains(this.person.getName()))
                throw new ChildNotReferencingParentException(path, person.getName());
            this.person.children.add(child.person);
        }
    }

    public static TemporaryPerson loadTemporaryPerson(String path) {
        Scanner reader;
        String name = null;
        List<String> parentNames = new ArrayList<>();
        List<String> childrenNames = new ArrayList<>();
        LocalDate birth = null, death = null;
        try {
            reader = new Scanner(new File(path));
            name = reader.nextLine();
            birth = Person.parseDate(reader.nextLine());

            FileExpectation expectation = FileExpectation.ExpectsDeath;

            while(reader.hasNext()) {
                String line = reader.nextLine();
                if(line.isEmpty()) continue;
                switch (expectation) {
                    case ExpectsDeath:
                        if(line.equals("Rodzice:"))
                            expectation = FileExpectation.ExpectsParent;
                        else if (line.equals("Dzieci:"))
                            expectation = FileExpectation.ExpectsChild;
                        else
                            death = Person.parseDate(line);
                        break;
                    case ExpectsParent:
                        if (line.equals("Dzieci:"))
                            expectation = FileExpectation.ExpectsChild;
                        else
                            parentNames.add(line);
                        break;
                    case ExpectsChild:
                        childrenNames.add(line);
                        break;
                }
            }
        } catch (NullPointerException | FileNotFoundException e) {
        }
        return new TemporaryPerson(name, birth, death, path, parentNames,childrenNames);
    }
}
