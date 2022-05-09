package puzzle_IA_2doParcial;

import java.util.*;

public class Puzzle {

    private class posicionFicha {

        public int x;
        public int y;

        public posicionFicha(int x, int y) {
            this.x = x;
            this.y = y;

        }
    }
    public final static int DIMS = 3;
    private int[][] tiles;
    private int display_width;
    private posicionFicha blank;

    public Puzzle() {
        tiles = new int[DIMS][DIMS];
        int cnt = 1;
        for (int i = 0; i < DIMS; i++) {
            for (int j = 0; j < DIMS; j++) {
                tiles[i][j] = cnt;
                cnt++;
            }
        }
        display_width = Integer.toString(cnt).length();
        blank = new posicionFicha(DIMS - 1, DIMS - 1);
        tiles[blank.x][blank.y] = 0;
    }
    public final static Puzzle SOLVED = new Puzzle();

    public Puzzle(Puzzle toClone) {
        this();
        for (posicionFicha p : todasPosFichas()) {
            tiles[p.x][p.y] = toClone.ficha(p);
        }
        blank = toClone.getBlank();
    }

    public List<posicionFicha> todasPosFichas() {
        ArrayList<posicionFicha> out = new ArrayList<posicionFicha>();
        for (int i = 0; i < DIMS; i++) {
            for (int j = 0; j < DIMS; j++) {
                out.add(new posicionFicha(i, j));
            }
        }
        return out;
    }

    public int ficha(posicionFicha p) {
        return tiles[p.x][p.y];
    }

    public posicionFicha getBlank() {
        return blank;
    }

    public posicionFicha whereIs(int x) {
        for (posicionFicha p : todasPosFichas()) {
            if (ficha(p) == x) {
                return p;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Puzzle) {
            for (posicionFicha p : todasPosFichas()) {
                if (this.ficha(p) != ((Puzzle) o).ficha(p)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int out = 0;
        for (posicionFicha p : todasPosFichas()) {
            out = (out * DIMS * DIMS) + this.ficha(p);
        }
        return out;
    }

    public void imprimir() {
        System.out.println("-------------");
        for (int i = 0; i < DIMS; i++) {
            System.out.print("| ");
            for (int j = 0; j < DIMS; j++) {
                int n = tiles[i][j];
                String s;
                if (n > 0) {
                    s = Integer.toString(n);
                } else {
                    s = "";
                }
                while (s.length() < display_width) {
                    s += " ";
                }
                System.out.print(s + "| ");
            }
            System.out.print("\n");
        }
        System.out.print("-------------\n\n");
    }

    public List<posicionFicha> todosMovVali() {
        ArrayList<posicionFicha> out = new ArrayList<posicionFicha>();
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                posicionFicha tp = new posicionFicha(blank.x + dx, blank.y + dy);
                if (movimientoValido(tp)) {
                    out.add(tp);
                }
            }
        }
        return out;
    }

    public boolean movimientoValido(posicionFicha p) {
        if ((p.x < 0) || (p.x >= DIMS)) {
            return false;
        }
        if ((p.y < 0) || (p.y >= DIMS)) {
            return false;
        }
        int dx = blank.x - p.x;
        int dy = blank.y - p.y;
        if ((Math.abs(dx) + Math.abs(dy) != 1) || (dx * dy != 0)) {
            return false;
        }
        return true;
    }

    public void mover(posicionFicha p) {
        if (!movimientoValido(p)) {
            throw new RuntimeException("Invalid move");
        }
        assert tiles[blank.x][blank.y] == 0;
        tiles[blank.x][blank.y] = tiles[p.x][p.y];
        tiles[p.x][p.y] = 0;
        blank = p;
    }

    public Puzzle movFicha(posicionFicha p) {
        Puzzle out = new Puzzle(this);
        out.mover(p);
        return out;
    }

    public void revolver(int howmany) {
        for (int i = 0; i < howmany; i++) {
            List<posicionFicha> possible = todosMovVali();
            int which = (int) (Math.random() * possible.size());
            posicionFicha move = possible.get(which);
            this.mover(move);
        }
    }

    public void mezclar() {
        revolver(DIMS * DIMS * DIMS * DIMS * DIMS);
    }

    public int numeroTableros() {
        int wrong = 0;
        for (int i = 0; i < DIMS; i++) {
            for (int j = 0; j < DIMS; j++) {
                if ((tiles[i][j] > 0) && (tiles[i][j] != SOLVED.tiles[i][j])) {
                    wrong++;
                }
            }
        }
        return wrong;
    }

    public boolean esResuelto() {
        return numeroTableros() == 0;
    }

    public int distManhattan() {
        int sum = 0;
        for (posicionFicha p : todasPosFichas()) {
            int val = ficha(p);
            if (val > 0) {
                posicionFicha correct = SOLVED.whereIs(val);
                sum += Math.abs(correct.x = p.x);
                sum += Math.abs(correct.y = p.y);
            }
        }
        return sum;
    }

    public int errorEsti() {
        return this.numeroTableros();
    }

    public List<Puzzle> movimientoAdyacen() {
        ArrayList<Puzzle> out = new ArrayList<Puzzle>();
        for (posicionFicha move : todosMovVali()) {
            out.add(movFicha(move));
        }
        return out;
    }

    public List<Puzzle> aEstrella() {
        HashMap<Puzzle, Puzzle> predecessor = new HashMap<Puzzle, Puzzle>();
        HashMap<Puzzle, Integer> depth = new HashMap<Puzzle, Integer>();
        final HashMap<Puzzle, Integer> score = new HashMap<Puzzle, Integer>();
        Comparator<Puzzle> comparator = new Comparator<Puzzle>() {
            @Override
            public int compare(Puzzle a, Puzzle b) {

                return score.get(a) - score.get(b);
            }
        };
        PriorityQueue<Puzzle> toVisit = new PriorityQueue<Puzzle>(10000, comparator);
        predecessor.put(this, null);
        depth.put(this, 0);
        score.put(this, this.errorEsti());
        toVisit.add(this);
        int cnt = 0;
        while (toVisit.size() > 0) {
            Puzzle candidate = toVisit.remove();
            cnt++;
            if (cnt % 10000 == 0) {
                System.out.printf("Considerado %,d posiciones. Cola = %,d\n", cnt,
                        toVisit.size());
            }
            if (candidate.esResuelto()) {
                System.out.printf("Solución del puzzle considerada con %d tableros\n", cnt);
                LinkedList<Puzzle> solution = new LinkedList<Puzzle>();
                Puzzle backtrace = candidate;
                while (backtrace != null) {
                    solution.addFirst(backtrace);
                    backtrace = predecessor.get(backtrace);
                }
                return solution;
            }
            for (Puzzle fp : candidate.movimientoAdyacen()) {
                if (!predecessor.containsKey(fp)) {
                    predecessor.put(fp, candidate);
                    depth.put(fp, depth.get(candidate) + 1);
                    int estimate = fp.errorEsti();
                    score.put(fp, depth.get(candidate) + 1 + estimate);
                    toVisit.add(fp);
                }
            }
        }
        return null;
    }

    public static void imprimirSolucion(List<Puzzle> solution) {
        if (solution != null) {
            System.out.printf("Puzzle resuelto en %d pasos:\n", solution.size());
            for (Puzzle sp : solution) {
                sp.imprimir();
            }
        } else {
            System.out.println("Puzzle no resuelto: (");
        }
    }
}
