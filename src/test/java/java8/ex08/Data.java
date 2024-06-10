package java8.ex08;

import java8.data.domain.Pizza;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private List<Pizza> pizzas = new ArrayList<>();

    public Data() {
        pizzas.add(new Pizza(1, "4 fromages", 10));
        pizzas.add(new Pizza(2, "hawaienne", 10));
        pizzas.add(new Pizza(3, "reine", 11));
        pizzas.add(new Pizza(4, "L'indienne", 8));
        pizzas.add(new Pizza(5, "cannibale", 13));
    }

    public List<Pizza> getPizzas() {
        return pizzas;
    }

    public void setPizzas(List<Pizza> pizzas) {
        this.pizzas = pizzas;
    }
}
