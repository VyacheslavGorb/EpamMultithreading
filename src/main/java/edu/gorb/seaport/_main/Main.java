package edu.gorb.seaport._main;

import edu.gorb.seaport.entity.Ship;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(10);
        List<Ship> ships = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ships.add(new Ship(Ship.ShipTask.LOAD));
        }

        for (int i = 0; i < 5; i++) {
            ships.add(new Ship(Ship.ShipTask.LOAD));
        }

        ships.forEach(service::execute);
        service.shutdown();
    }
}
