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
        for(var leftSideParent : parents[0].parents) {
            if (leftSideParent == null) continue;
            for (var rightSideParent : parents[1].parents) {
                if (rightSideParent == null) continue;
                if (leftSideParent == rightSideParent)
                    throw new IncestException(leftSideParent, this);
            }
        }
    }
    public static Person loadPerson(String filePath) throws FileNotFoundException, AmbigiousPersonException {
        File file = new File(filePath);
        Scanner s = new Scanner(file);
        String name = s.nextLine();
        LocalDate birth = LocalDate.parse(s.nextLine(), DateTimeFormatter.ofPattern("dd.MM.uuuu"));
        LocalDate death = null;
        if(s.hasNextLine()){
            String deathText = s.nextLine();
            if (deathText !="") {
                death = LocalDate.parse(deathText, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
            }
        }

        for(var i: temporaryPeople){
            if(i.person.name.compareTo(name) == 0){
                throw new AmbigiousPersonException(name, filePath, i.path);
            }
        }
        Person result = new Person(name, birth, death);

        temporaryPeople.add(new TemporaryPerson(result, filePath));

        return result;
    }
    public static List<Person> relations(String[] filePaths) throws IncestException {
        List<Person> relatedPerson = new ArrayList<>();

        for (String filePath : filePaths) {
            try (Scanner scanner = new Scanner(new File(filePath))) {
                String name = scanner.nextLine();
                String[] parents = new String[2];
                LocalDate death = null;
                LocalDate birth = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd.MM.uuuu"));

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.equals("Rodzice:")) {
                        if (scanner.hasNextLine()) {
                            parents[0] = scanner.nextLine();
                        }
                        if (scanner.hasNextLine()) {
                            parents[1] = scanner.nextLine();
                        }
                    } else {
                        death = LocalDate.parse(line, DateTimeFormatter.ofPattern("dd.MM.uuuu"));
                    }
                }

                relatedPerson.add(new Person(name, birth, death, parents[0], parents[1]));
            } catch (FileNotFoundException e) {
                System.out.println("Plik " + filePath + " nie został odnaleziony.");
            } catch (DateTimeParseException e) {
                System.out.println("Błąd parsowania daty w pliku " + filePath);
            }
        }

        // Sprawdzenie powiązań rodzicielskich
//        for (int i = 0; i < relatedPerson.size(); i++) {
//            Person parent1 = relatedPerson.get(i);
//            for (int j = i + 1; j < relatedPerson.size(); j++) {
//                Person parent2 = relatedPerson.get(j);
//                if (parent1.hasCommonChildWith(parent2)) {
//                    throw new IncestException("Wykryto powiązanie rodzicielskie między " + parent1.getFullName() + " a " + parent2.getFullName());
//                }
//            }
//        }

        return relatedPerson;
    }



}
