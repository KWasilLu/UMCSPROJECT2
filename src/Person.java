import javax.swing.text.DateFormatter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Person implements Serializable {


    private String name;
    private LocalDate birth, death;

    List<Person> parents = new ArrayList<>();
    List<Person> children = new ArrayList<>();


    public Person(String name, LocalDate birth) {
        this(name, birth, null);
    }

    public Person(String name, LocalDate birth, LocalDate death) {
        this.name = name;
        this.birth = birth;
        this.death = death;
        try {
            if (birth.isAfter(death)) {
                throw new NegativeLifespanException(birth, death, "Possible time-space loophole.");
            }
        } catch (NullPointerException e) {}
    }

    public Person(String name, LocalDate birth, LocalDate death, Person parent1, Person parent2) throws IncestException {
        this(name, birth, death);
        parents.add(parent1);
        parents.add(parent2);

        checkForIncest();
    }

    public Person(String name, LocalDate birth, Person parent1, Person parent2) throws IncestException {
        this(name, birth, null, parent1, parent2);
    }

    public String getName() {
        return name;
    }

    public LocalDate getDeath() {
        return death;
    }

    public LocalDate getBirth() {
        return birth;
    }

    @Override
    public String toString() {
        String str = "Person{" +
                "name='" + name + '\'' +
                ", birth=" + birth +
                ", death=" + death +
                ", parents={";
        for (var parent : parents)
            str += parent.name + ", ";
        str += "} children={";
        for (var child : children)
            str += child.name + ", ";
        str += "}}";

        return str;
    }

    static LocalDate parseDate(String str) throws DateTimeParseException, NullPointerException {
        return LocalDate.parse(str, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }


    static String parseDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private void checkForIncest() throws IncestException {
        if (parents.size()<2)
            return;
        for (var leftSideParent : parents.get(0).parents) {
            if (leftSideParent == null) continue;
            for (var rightSideParent : parents.get(1).parents) {
                if (rightSideParent == null) continue;
                if (leftSideParent == rightSideParent)
                    throw new IncestException(leftSideParent, this);
            }
        }
    }

    static public Person[] loadFromPath(String path) throws AmbigiousPersonException, ParentingAgeException, FileNotFoundException {
        Map<String, TemporaryPerson> temporaryMap = new HashMap<>();

        File[] files = new File(path).listFiles();
        for (File file : files) {
            TemporaryPerson temporaryPerson = TemporaryPerson.loadTemporaryPerson(file.getPath());
            if (temporaryMap.containsKey(temporaryPerson.person.getName()))
                throw new AmbigiousPersonException(temporaryPerson.person.name, temporaryPerson.getPath(),
                        temporaryMap.get(temporaryPerson.person.getName()).getPath());
            else
                temporaryMap.put(temporaryPerson.person.getName(), temporaryPerson);
        }
        for (Map.Entry<String, TemporaryPerson> pair : temporaryMap.entrySet()) {
            pair.getValue().convert(temporaryMap);
        }
        for (Map.Entry<String, TemporaryPerson> pair : temporaryMap.entrySet()) {
            pair.getValue().convertChildren(temporaryMap);
        }

        return temporaryMap.values().stream()
                .map(temporaryPerson -> temporaryPerson.person)
                .toArray(Person[]::new);
    }

}









