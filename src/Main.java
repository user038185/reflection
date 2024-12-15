import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        (new Injector<SomeBean>()).Inject(new SomeBean()).foo();
    }
}