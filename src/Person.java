import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Scanner;
import java.util.Set;

public class Person implements Serializable {
    private String name;
    private LocalDate birth, death;
    private Person parents[] = new Person[2];

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
    public static Person loadPerson(String path) {
        File file = new File(path);
       try {
           Scanner scanner = new Scanner(file);
           String name = scanner.nextLine();
           DateTimeFormatter format =  DateTimeFormatter.ofPattern("dd.MM.yyyy");
           LocalDate birth  = LocalDate.parse(scanner.nextLine(), format);
           LocalDate death = null;
           Person loadedPerson;
           if (scanner.hasNextLine()) {
               String tmp = scanner.nextLine();
                if (tmp !="") {
                    death = LocalDate.parse(tmp,format);
                }
                loadedPerson = new Person(name,birth,death);
           } else {
               loadedPerson = new Person(name,birth,null);
           }

           return loadedPerson;
       } catch (Exception e){
            return null;
        }
    }
}