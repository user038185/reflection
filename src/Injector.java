import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.lang.reflect.Field;

public class Injector<T> {
    private Properties properties;

    void readPropertiesConfiguration() throws IOException {
        FileInputStream fileReader;
        this.properties = new Properties();
        fileReader = new FileInputStream("src/configuration.properties");
        properties.load(fileReader);
    }

    public T Inject(T object) throws IOException {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getAnnotation(AutoInjectable.class) != null) {
                String type = field.getType().toString().split(" ")[1];
                readPropertiesConfiguration();
                String newClassName = properties.getProperty(type);
                Class<?> fillClass = null;
                try {
                    fillClass = Class.forName(newClassName);
                } catch (ClassNotFoundException a) {
                    a.printStackTrace();
                }
                try {
                    assert fillClass != null;
                    field.setAccessible(true);
                    field.set(object, fillClass.newInstance());
                } catch (IllegalAccessException | InstantiationException a) {
                    a.printStackTrace();
                }
            }
        }
        return object;
    }
}