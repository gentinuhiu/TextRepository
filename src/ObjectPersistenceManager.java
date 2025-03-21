import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class ObjectPersistenceManager<T> {
    protected Class<T> objectType;

    protected ObjectPersistenceManager(Class<T> objectType) {
        try {
            Field[] fields = objectType.getDeclaredFields();

            if (fields.length == 0) {
                throw new EntityInitializationException("The entity has no attributes.");
            }

            Field firstField = fields[0];
            if (!firstField.getType().equals(Long.class) && !firstField.getType().equals(long.class)) {
                throw new EntityInitializationException("The first attribute is NOT of type Long.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        this.objectType = objectType;
    }


    public Long firstAttributeValue(T obj) {
        Class<?> clazz = obj.getClass(); // Get the class of the object
        Field[] fields = clazz.getDeclaredFields(); // Get all declared fields
        Long value = -1L;
        if (fields.length > 0) {
            Field firstField = fields[0];  // Get the first attribute
            firstField.setAccessible(true); // Allow access to private fields

            try {
                value = (Long) firstField.get(obj); // Get the value of the first field
                System.out.println("First attribute value: " + value);
            } catch (IllegalAccessException e) {
                System.out.println("Cannot access field value: " + e.getMessage());
            }
        } else {
            System.out.println("No attributes found.");
        }
        return value;
    }

    public static <T> T mapToClass(List<String> data, Class<T> clazz) throws Exception {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data list cannot be empty");
        }

        // First element: Single value attributes (ID, name, etc.)
        String[] singleValues = data.get(0).split(",");
        T instance = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        int index = 1;

        for (Field field : fields) {
            field.setAccessible(true);

            if (List.class.isAssignableFrom(field.getType())) {
                continue; // Skip lists for now (multi-value attributes)
            }

            if (!field.getType().isPrimitive() && !field.getType().equals(String.class) &&
                    !Number.class.isAssignableFrom(field.getType())) {
                // If the field is another class object, store only the ID and class name
                Long objectId = Long.parseLong(singleValues[index]);
                Repository<?> repository = new Repository<>(field.getType());
                T object = (T) repository.findById(objectId);
                field.set(instance, object);
            } else if (index < singleValues.length) {
                Object value = convertValue(singleValues[index], field.getType());
                field.set(instance, value);
            }
            index++;
        }

        // Process List fields (Multi-value attributes)
        for (int i = 1; i < data.size(); i++) {
            String[] multiValues = data.get(i).split(",");
            for (Field field : fields) {
                if (List.class.isAssignableFrom(field.getType())) { // It's a List
                    Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        Class<?> listType = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];

                        if (!listType.isPrimitive() && !listType.equals(String.class) &&
                                !Number.class.isAssignableFrom(listType)) {
                            // If the list contains objects, store only IDs and class name

                            List<Object> objects = new ArrayList<>();
                            Repository<?> repository = new Repository<>(field.getType());

                            for(String str: multiValues){
                                objects.add((T) repository.findById(Long.parseLong(str)));
                            }

                            field.setAccessible(true);
                            field.set(instance, objects);
                        } else {
                            // Convert basic types normally
                            List<Object> listValues = Arrays.stream(multiValues)
                                    .map(value -> convertValue(value, listType))
                                    .collect(Collectors.toList());

                            field.setAccessible(true);
                            field.set(instance, listValues);
                        }
                    }
                }
            }
        }

        return instance;
    }

//
//    protected String objectToString(T object, boolean enterId, Long id) {
//        StringBuilder sb = new StringBuilder("***");
//        boolean setId = true;
//
//        try {
//            Field[] fields = object.getClass().getDeclaredFields();
//            for (Field field : fields) {
//                field.setAccessible(true);
//                Object value = field.get(object);
//
//                if (enterId && setId) {
//                    sb.append(",").append(id);
//                    setId = false;
//                } else if (value instanceof List<?>) {
//                    List<?> list = (List<?>) value;
//                    sb.append("\n");
//                    if (list.isEmpty()) {
//                        sb.append("<empty>");
//                    } else {
//                        // If the list contains primitives or wrapper types (Integer, Double, etc.), convert them directly
//                        if (isPrimitiveList(list)) {
//                            sb.append(list.stream().map(String::valueOf).collect(Collectors.joining(",")));
//                        } else {
//                            sb.append(list.stream()
//                                    .map(this::getFirstAttribute) // Extract first attribute for each object in list
//                                    .collect(Collectors.joining(",")));
//                        }
//                    }
//                } else if (isCustomObject(value)) {
//                    // If it's a custom object, get its first attribute
//                    sb.append(",").append(getFirstAttribute(value));
//                } else {
//                    sb.append(",").append(value);
//                }
//            }
//        } catch (IllegalAccessException e) {
//            return "Error in objectToString(): " + e.getMessage();
//        }
//
//        sb.append("\n");
//        return sb.toString();
//    }
//protected String objectToString(T object, boolean enterId, Long id) {
//    StringBuilder sb = new StringBuilder("***");
//    boolean setId = true;
//    StringBuilder listSb = new StringBuilder(); // Separate StringBuilder for list attributes
//
//    try {
//        Field[] fields = object.getClass().getDeclaredFields();
//        for (Field field : fields) {
//            field.setAccessible(true);
//            Object value = field.get(object);
//
//            if (enterId && setId) {
//                sb.append(",").append(id);
//                setId = false;
//                continue;
//            }
//
//            if (value instanceof List<?>) {
//                List<?> list = (List<?>) value;
//                listSb.append("\n"); // New line for list
//
//                if (list == null) { // ✅ If list is null, print "null"
//                    listSb.append("null");
//                } else if (list.isEmpty()) {
//                    listSb.append("<empty>");
//                } else {
//                    if (isPrimitiveList(list)) {
//                        listSb.append(list.stream().map(String::valueOf).collect(Collectors.joining(",")));
//                    } else {
//                        listSb.append(list.stream()
//                                .map(this::getFirstAttribute)
//                                .collect(Collectors.joining(",")));
//                    }
//                }
//            } else if (isCustomObject(value)) {
//                sb.append(",").append(getFirstAttribute(value));
//            } else {
//                sb.append(",").append(value == null ? "null" : value); // ✅ Handles simpler attributes
//            }
//        }
//    } catch (IllegalAccessException e) {
//        return "Error in objectToString(): " + e.getMessage();
//    }
//
//    sb.append(listSb).append("\n"); // Append the list elements after the first line
//    return sb.toString();
//}

    protected String objectToString(T object, boolean enterId, Long id) {
        StringBuilder sb = new StringBuilder("***");
        boolean setId = true;
        StringBuilder listSb = new StringBuilder(); // Separate StringBuilder for list attributes

        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(object);

                if (enterId && setId) {
                    sb.append(",").append(id);
                    setId = false;
                    continue;
                }

                // ✅ Instead of checking `value instanceof List<?>`, check the field type itself
                if (List.class.isAssignableFrom(field.getType())) {
                    listSb.append("\n"); // New line for lists

                    if (value == null) {
                        listSb.append("null"); // ✅ Print "null" for null lists
                    } else {
                        List<?> list = (List<?>) value;
                        if (list.isEmpty()) {
                            listSb.append("<empty>");
                        } else {
                            if (isPrimitiveList(list)) {
                                listSb.append(list.stream().map(String::valueOf).collect(Collectors.joining(",")));
                            } else {
                                listSb.append(list.stream()
                                        .map(this::getFirstAttribute)
                                        .collect(Collectors.joining(",")));
                            }
                        }
                    }
                } else if (isCustomObject(value)) {
                    sb.append(",").append(getFirstAttribute(value));
                } else {
                    sb.append(",").append(value == null ? "null" : value); // ✅ Handles simple attributes properly
                }
            }
        } catch (IllegalAccessException e) {
            return "Error in objectToString(): " + e.getMessage();
        }

        sb.append(listSb).append("\n"); // Append the list elements after the first line
        return sb.toString();
    }

    private String getFirstAttribute(Object obj) {
        if (obj == null) return "null";

        Field[] fields = obj.getClass().getDeclaredFields();
        if (fields.length == 0) return "null";

        fields[0].setAccessible(true);
        try {
            Object firstValue = fields[0].get(obj);
            return String.valueOf(firstValue);
        } catch (IllegalAccessException e) {
            return "Error: " + e.getMessage();
        }
    }
    private static Object convertValue(String value, Class<?> type) {
        if (type == Integer.class || type == int.class) {
            return Integer.parseInt(value);
        } else if (type == Long.class || type == long.class) {
            return Long.parseLong(value);
        } else if (type == Double.class || type == double.class) {
            return Double.parseDouble(value);
        } else if (type == Boolean.class || type == boolean.class) {
            return Boolean.parseBoolean(value);
        } else {
            return value; // Default to String
        }
    }
    private boolean isCustomObject(Object obj) {
        return obj != null && !(obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj.getClass().isPrimitive());
    }

    // Check if the list contains only primitive wrapper types (like Integer, Double, etc.)
    private boolean isPrimitiveList(List<?> list) {
        if (list.isEmpty()) return false;
        return list.get(0) instanceof Number || list.get(0) instanceof Boolean || list.get(0) instanceof Character;
    }
}
