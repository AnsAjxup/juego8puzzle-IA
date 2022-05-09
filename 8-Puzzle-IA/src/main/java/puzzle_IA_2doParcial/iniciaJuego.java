
package puzzle_IA_2doParcial;
import java.util.List;
/**
 *
 * @author ajxup
 */
public class iniciaJuego {

    public static void main(String[] args) {
        Puzzle p = new Puzzle();
        p.revolver(70);
        System.out.println("Puzzle inicial:");
        p.imprimir();
        List<Puzzle> solution;
        System.out.println("Puzzle final con A*:");
        solution = p.aEstrella();
        Puzzle.imprimirSolucion(solution);
    }
    
}
