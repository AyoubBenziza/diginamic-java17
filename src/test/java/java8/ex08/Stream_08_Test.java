package java8.ex08;

import java8.data.domain.Pizza;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Exercice 5 - Files
 */
public class Stream_08_Test {

    // Chemin vers un fichier de données des naissances
    private static final String NAISSANCES_DEPUIS_1900_CSV = "./naissances_depuis_1900.csv";

    private static final String DATA_DIR = "src/main/resources/pizza-data/";


    // Structure modélisant les informations d'une ligne du fichier
    class Naissance {
        String annee;
        String jour;
        Integer nombre;

        public Naissance(String annee, String jour, Integer nombre) {
            this.annee = annee;
            this.jour = jour;
            this.nombre = nombre;
        }

        public String getAnnee() {
            return annee;
        }

        public void setAnnee(String annee) {
            this.annee = annee;
        }

        public String getJour() {
            return jour;
        }

        public void setJour(String jour) {
            this.jour = jour;
        }

        public Integer getNombre() {
            return nombre;
        }

        public void setNombre(Integer nombre) {
            this.nombre = nombre;
        }
    }


    @Test
    public void test_group() throws IOException {

        // TODO utiliser la méthode java.nio.file.Files.lines pour créer un stream de lignes du fichier naissances_depuis_1900.csv
        // Le bloc try(...) permet de fermer (close()) le stream après utilisation
        try (Stream<String> lines = Files.lines(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(NAISSANCES_DEPUIS_1900_CSV)).getPath()))) {

            // TODO construire une MAP (clé = année de naissance, valeur = somme des nombres de naissance de l'année)
            Map<String, Integer> result = lines
                .skip(1)
                .map(line -> line.split(";"))
                .peek(data -> System.out.println(Arrays.toString(data))) // print out the data array
                .map(data -> new Naissance(data[1], data[2], Integer.valueOf(data[3])))
                .collect(groupingBy(Naissance::getAnnee, summingInt(Naissance::getNombre)));

            assertThat(result.get("2015"), is(8097));
            assertThat(result.get("1900"), is(5130));
        }
    }

    @Test
    public void test_max() throws IOException {

        // TODO utiliser la méthode java.nio.file.Files.lines pour créer un stream de lignes du fichier naissances_depuis_1900.csv
        // Le bloc try(...) permet de fermer (close()) le stream après utilisation
        try (Stream<String> lines = Files.lines(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(NAISSANCES_DEPUIS_1900_CSV)).getPath()))) {

            // TODO trouver l'année où il va eu le plus de nombre de naissance
            Optional<Naissance> result = lines
                .skip(1)
                .map(line -> line.split(";"))
                .map(data -> new Naissance(data[1], data[2], Integer.valueOf(data[3])))
                .max(Comparator.comparing(Naissance::getNombre));


            assertThat(result.get().getNombre(), is(48));
            assertThat(result.get().getJour(), is("19640228"));
            assertThat(result.get().getAnnee(), is("1964"));
        }
    }

    @Test
    public void test_collectingAndThen() throws IOException {
        // TODO utiliser la méthode java.nio.file.Files.lines pour créer un stream de lignes du fichier naissances_depuis_1900.csv
        // Le bloc try(...) permet de fermer (close()) le stream après utilisation
        try (Stream<String> lines = Files.lines(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(NAISSANCES_DEPUIS_1900_CSV)).getPath()))) {

            // TODO construire une MAP (clé = année de naissance, valeur = maximum de nombre de naissances)
            // TODO utiliser la méthode "collectingAndThen" à la suite d'un "grouping"
            Map<String, Naissance> result = lines
                .skip(1)
                .map(line -> line.split(";"))
                .map(data -> new Naissance(data[1], data[2], Integer.valueOf(data[3])))
                .collect(groupingBy(Naissance::getAnnee,
                    collectingAndThen(maxBy(Comparator.comparing(Naissance::getNombre)), Optional::get)));

            assertThat(result.get("2015").getNombre(), is(38));
            assertThat(result.get("2015").getJour(), is("20150909"));
            assertThat(result.get("2015").getAnnee(), is("2015"));

            assertThat(result.get("1900").getNombre(), is(31));
            assertThat(result.get("1900").getJour(), is("19000123"));
            assertThat(result.get("1900").getAnnee(), is("1900"));
        }
    }

    // Des données figurent dans le répertoire pizza-data
    // TODO explorer les fichiers pour voir leur forme
    // TODO compléter le test

    @Test
    public void test_pizzaData() throws IOException {
        // TODO utiliser la méthode java.nio.file.Files.list pour parcourir un répertoire
        Data data = new Data();
        List<Pizza> pizzas = data.getPizzas();

        // TODO Optionel
        // TODO Créer un test qui exporte des données new Data().getPizzas() dans des fichiers
        // TODO 1 fichier par pizza
        // TODO le nom du fichier est de la forme ID.txt (ex. 1.txt, 2.txt)
        try{
            for (Pizza pizza : pizzas) {
                Path filePath = Paths.get(DATA_DIR + pizza.getId() + ".txt");
                String pizzaDetails = pizza.toString(); // Assuming Pizza has a suitable toString method

                Files.write(filePath, pizzaDetails.getBytes(), StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


        try(Stream<Path> paths = Files.list(Paths.get(DATA_DIR))) {
            // TODO trouver la pizza la moins chère
            String pizzaNamePriceMin1 = paths
                    .map(path -> {
                        try {
                            List<String> lines = Files.readAllLines(path);
                            return new Pizza(
                                    Integer.valueOf(lines.get(0).replace("Id: ","")),
                                    lines.get(1).replace("Name: ",""),
                                    Integer.valueOf(lines.get(2).replace("Price: ","")));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    })
                    .min(Comparator.comparing(Pizza::getPrice))
                    .map(Pizza::getName)
                    .orElse(null);

            assertThat(pizzaNamePriceMin1, is("L'indienne"));
        }

    }


}