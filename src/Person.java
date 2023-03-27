import javax.swing.text.DateFormatter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Person implements Serializable {
    private static String name;
    private static LocalDate birth;
    private static LocalDate death;
    private static Person[] parents = new Person[2];
    private static List<TemporaryPerson> temporaryPeople = new ArrayList<>();

    public static List<Person> people = new ArrayList<>();

    String path;

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
        parents[0] = parent1;
        parents[1] = parent2;

        checkForIncest();
    }

    public Person(String name, LocalDate birth, Person parent1, Person parent2) throws IncestException {
        this(name, birth, null, parent1, parent2);
    }


    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", birth=" + birth +
                ", death=" + death +
                ", parents=" + Arrays.toString(parents) +
                '}';
    }

    private void checkForIncest() throws IncestException {
        if(parents[0] == null || parents[1] == null)
            return;
        for(var leftSideParent : parents) {
            if (leftSideParent == null) continue;
            for (var rightSideParent : parents) {
                if (rightSideParent == null) continue;
                if (leftSideParent == rightSideParent)
                    throw new IncestException(leftSideParent, this);
            }
        }
    }
//    public static Person loadPerson(String filePath) throws FileNotFoundException, AmbigiousPersonException {
//        File file = new File(filePath);
//        Scanner s = new Scanner(file);
//        String name = s.nextLine();
//        LocalDate birth = LocalDate.parse(s.nextLine(), DateTimeFormatter.ofPattern("dd.MM.uuuu"));
//        LocalDate death = null;
//        if(s.hasNextLine()){
//            String deathText = s.nextLine();
//            if (deathText !="") {
//                death = LocalDate.parse(deathText, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
//            }
//        }
//
//        for(var i: temporaryPeople){
//            if(i.person.name.compareTo(name) == 0){
//                throw new AmbigiousPersonException(name, filePath, i.path);
//            }
//        }
//        Person result = new Person(name, birth, death);
//
//        temporaryPeople.add(new TemporaryPerson(result, filePath));
//
//        return result;
//    }
    public static Person getPersonByName(String name) {
        for (Person person : people) {
            if (Person.name.equals(name)) {
                return person;
            }
        }
        return null;
    }

    public void setPath (String path) {
        this.path = path;
    }


    public static Person CreateHuman(String WayToFile) throws FileNotFoundException, AmbigiousPersonException, IncestException {
        File file = new File(WayToFile);
        String name;
        Scanner scanner = new Scanner(file);
        name = scanner.nextLine();
        LocalDate DateBirth = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        LocalDate DateDeath = null;
        Person parent1 = null;
        Person parent2 = null;

        if(scanner.hasNextLine()){
            String line = scanner.nextLine();

            if(!line.equals("Rodzice:")) {
                DateDeath = LocalDate.parse(line, DateTimeFormatter.ofPattern("dd.MM.yyyy"));

                if(scanner.hasNextLine()) {
                    line = scanner.nextLine();
                }
            }

            if(line.equals("Rodzice:")) {
                parent1 = getPersonByName(scanner.nextLine());
                if(scanner.hasNextLine()){
                    parent2 = getPersonByName(scanner.nextLine());
                }
            }


        }

        for(var person : people){
            if(Person.name.equals(name)){
                throw new AmbigiousPersonException(name,WayToFile, person.path);
            }
        }
        // CHECK
        Person person1 = new Person(name,DateBirth,DateDeath, parent1, parent2);
        person1.setPath(WayToFile);
        people.add(person1);
        return person1;
    }

    public static List<Person> createPeople(List<String> paths) throws IncestException, FileNotFoundException, AmbigiousPersonException {
        List<Person> people = new ArrayList<>();

        for(String path : paths) {
            Person person = CreateHuman(path);
            people.add(person);
        }

        return people;
    }
}









