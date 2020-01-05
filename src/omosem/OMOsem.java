/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package omosem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mici
 */
public class OMOsem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Person p = new Person();
        Equipment e = new Equipment();
        List<Unit> l = new ArrayList<>();
        l.add(p);
        l.add(e);
        for(Unit u : l) {
        System.out.println(""+u.cost);
        }
    }
    
}
