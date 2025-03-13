import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Repository<T> extends ObjectPersistenceManager<T>{
    private String path;

    public Repository(Class<T> objectType) {
        super(objectType);
        path = objectType.getSimpleName() + "Repository.txt";
    }
    private void init(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, false))) {
            writer.write("");
            System.out.println("Successfully saved to: " + path);
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    private Long generateId(){
        String path = objectType.getSimpleName() + "ID.txt";
        Long result = -1L;
        File file = new File(path);

        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("1");
                result = 1L;
                System.out.println("ID file created and initialized with 1.");
            } catch (IOException e) {
                System.out.println("Error creating ID file: " + e.getMessage());
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String content = reader.readLine();
                result = Long.parseLong(content) + 1;
                System.out.println("ID file content after increment: " + content);
            } catch (IOException e) {
                System.out.println("Error reading ID file: " + e.getMessage());
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(result.toString());
                System.out.println("ID file saved: " + result);
            } catch (IOException e) {
                System.out.println("Error creating ID file: " + e.getMessage());
            }
        }
        return result;
    }
    private void enter(T object) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            String result = objectToString(object, false, null);
            writer.write(result);
            System.out.println("Successfully saved to: " + path);
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }
    private void enterAll(List<T> objects){
        objects.forEach(this::enter);
    }
    public void save(T object) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, true))) {
            String result = objectToString(object, true, generateId());
            writer.write(result);
            System.out.println("Successfully saved to: " + path);
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }
    }

    public void saveAll(List<T> objects){
        objects.forEach(this::save);
    }

    public void update(T object) throws FileNotFoundException {
        List<T> objects = readAll();
        objects.removeIf(o -> o.equals(object));
        objects.add(object);
        init();
        enterAll(objects);
    }

    public void delete(T object) throws FileNotFoundException {
        List<T> objects = readAll();
        objects.removeIf(o -> o.equals(object));
        init();
        enterAll(objects);
    }

    public void deleteAll(List<T> objects) throws FileNotFoundException {
        List<T> storedObjects = readAll();
        for(T storedObject : storedObjects){
            for (T object : objects){
                if(storedObject.equals(object)){
                    storedObjects.remove(storedObject);
                }
            }
        }
        init();
        enterAll(storedObjects);
    }

    public void deleteAll(){
        init();
    }
    public T findById(Long id) throws FileNotFoundException {
        List<T> objects = readAll();
        for(T object: objects){
            if(firstAttributeValue(object).equals(id)){
                return object;
            }
        }
        System.out.println("Object with id " + id + " not found.");
        return null;
    }
    public List<T> readAll() throws FileNotFoundException {
        List<T> objects = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))){
            System.out.println("Reading from: " + path);
            String line;

            boolean flag = false;
            line = reader.readLine();
            while(line != null){
                List<String> list = new ArrayList<>();
                list.add(line);

                for(;;){
                    line = reader.readLine();
                    if(line == null){
                        flag = true;
                        break;
                    }

                    if(line.startsWith("***,"))
                        break;

                    list.add(line);
                }

                objects.add(mapToClass(list, objectType));

                if(flag)
                    break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return objects;
    }

//    public List<T> readAll() {
//        List<T> objects = new ArrayList<>();
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
//            String line;
//            System.out.println("Reading from: " + path);
//
//            while ((line = reader.readLine()) != null) {
//                String[] rows = line.split(",");
//                List<String> attributes = new ArrayList<>();
//                for(int i = 1; i < rows.length; i++) {
//                    attributes.add(rows[i]);
//                }
//
//                T result = getObject(attributes);
//                if(result != null)
//                    objects.add(result);
//                else
//                    System.out.println("Error in readAll(): Object not found");
//            }
//        } catch (FileNotFoundException e) {
//            System.out.println("File not found. Creating a new file: " + path);
//            init();
//        } catch (IOException e) {
//            System.out.println("Error reading: " + e.getMessage());
//        }
//        return objects;
//    }
}
