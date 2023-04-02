import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
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

    public static void main(String[] args){
        try {
            Person[] people = Person.loadFromPath("test/test_dzieci_rodzice");
            for(Person person : people) {
                System.out.println(person);
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (AmbigiousPersonException | ParentingAgeException | ParentChildInconsistencyException e) {
            e.printStackTrace();
        }
    }
}