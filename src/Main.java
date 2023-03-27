import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void save(String path) {
        try {
            List<Person> people = new ArrayList<>();
            Person janusz = new Person("Janusz",
                    LocalDate.of(1975, 1, 1));
            people.add(janusz);

            Person grazyna = new Person("Grażyna",
                    LocalDate.of(1975, 1, 1));
            people.add(grazyna);

            Person krystyna = new Person("Krystyna",
                    LocalDate.of(1975, 1, 1));
            people.add(krystyna);

            Person seba = new Person("Seba",
                    LocalDate.of(1990, 1, 1),
                    janusz, grazyna);
            people.add(seba);

            Person edzio = new Person("Edzio",
                    LocalDate.of(1975, 1, 1));
            people.add(edzio);

            Person karyna = new Person("Karyna",
                    LocalDate.of(1995, 1, 1),
                    edzio, krystyna);
            people.add(karyna);

            Person brajan = new Person("Brajan",
                    LocalDate.of(2011, 1, 1),
                    seba, karyna);
            people.add(brajan);

            System.out.println(brajan);

            try {
                FileOutputStream stream = new FileOutputStream(path);
                ObjectOutputStream objectStream = new ObjectOutputStream(stream);
                objectStream.writeObject(people);
                objectStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IncestException e) {
            e.printStackTrace();
        }

    }

    public static void load(String path) {
        List<Person> people;
        try {
            FileInputStream file = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(file);

            people = (List<Person>) in.readObject();

            System.out.println(people);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        try {
            List<String> paths = new ArrayList<>();
            paths.add("test/test_rodzice/p1.txt");
            paths.add("test/test_rodzice/p2.txt");
            paths.add("test/test_rodzice/p3.txt");

            List<Person> result = Person.createPeople(paths);

            System.out.println(result.get(2));
        } catch (FileNotFoundException | ParentingAgeException | IncestException e) {
            throw new RuntimeException(e);
        } catch (AmbigiousPersonException error) {
            System.out.println(error.path1);
            System.out.println(error.path2);
        }

//        Person kamil = new Person("Kamil", LocalDate.of(1999,12,1),LocalDate.of(2012,1,2));
//        kamil.getAge();
//        System.out.println(kamil.getAge());
    }

}