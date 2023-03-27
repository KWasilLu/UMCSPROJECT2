import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Person implements Serializable {
    private String name;
    private static LocalDate birth;
    private static LocalDate death;
    private Person parents[] = new Person[2];
    public static List<Person> people = new ArrayList<>();
    public String path;


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

    public void setPath(String path) {
        this.path = path;
    }

    void checkForIncest() throws IncestException {
        if(parents[0] == null || parents[1] == null)
            return;
        for(var leftSideParent : parents[0].parents) {
            if (leftSideParent == null) continue;
            for (var rightSideParent : parents[1].parents) {
                if (rightSideParent == null) continue;
                if (leftSideParent == rightSideParent)
                    throw new IncestException(leftSideParent, this);
            }
        }
    }
     long getAge() {
        return ChronoUnit.YEARS.between(birth,LocalDate.now());
    }

    public static Person getPersonByName(String name) {
        for(Person person : people) {
            if(person.name.equals(name)) {
                return person;
            }
        }

        return null;
    }

    public static Person CreateHuman(String WayToFile) throws FileNotFoundException, AmbigiousPersonException, IncestException, ParentingAgeException {
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
                if (parent1.getAge() < 15 || parent1.getAge() > 50 || DateDeath != null) throw new ParentingAgeException("Dziwny wiek");
                parent2 = null;
                if(scanner.hasNextLine()){
                    parent2 = getPersonByName(scanner.nextLine());
                    if (parent2.getAge() < 15 || parent2.getAge() > 50 || DateDeath != null) throw new ParentingAgeException("Dziwny wiek2");
                }
            }


        }

        for(var person : people){
            if(person.name.equals(name)){
                throw new AmbigiousPersonException(name,WayToFile, person.path);
            }
        }
        // CHECK
        Person person1 = new Person(name,DateBirth,DateDeath, parent1, parent2);
        person1.setPath(WayToFile);
        people.add(person1);
        return person1;
    }

    public static List<Person> createPeople(List<String> paths) throws IncestException, FileNotFoundException, AmbigiousPersonException, ParentingAgeException {
        List<Person> people = new ArrayList<>();

        for(String path : paths) {
            Person person = CreateHuman(path);
            people.add(person);
        }

        return people;
    }
}









