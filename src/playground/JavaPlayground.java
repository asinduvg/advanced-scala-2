package playground;

public class JavaPlayground {

    public static void main(String[] args) {

        // via calling static methods
        System.out.println(MathWork.isOddStatic(34));

        // via instantiating
        MathWork mw = new MathWork();
        System.out.println(mw.isOdd(34));

        HH hh = new HH() {
            @Override
            public void sample() {
                System.out.println("hi");
            }
        };

        hh.sample();

    }

}

class MathWork {
    public static boolean isOddStatic(Integer number) {
        return number % 2 != 0;
    }

    public boolean isOdd(Integer number) {
        return number % 2 != 0;
    }
}

abstract class HH {
    abstract void sample();
}