package apache.avro.examples;

import example.avro.User;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void serialise(User user1,User user2, User user3, File file) throws IOException {
        DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
        DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter);
        dataFileWriter.create(user1.getSchema(), file);
        dataFileWriter.append(user1);
        dataFileWriter.append(user2);
        dataFileWriter.append(user3);
        dataFileWriter.close();
    }

    private static void deserialise(File file) throws IOException{
        DatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
        DataFileReader<User> dataFileReader = new DataFileReader<User>(file, userDatumReader);
        User user = null;
        while (dataFileReader.hasNext()) {
        // Reuse user object by passing it to next(). This saves us from
        // allocating and garbage collecting many objects for files with
        // many items.
        user = dataFileReader.next(user);
        System.out.println(user);
        }
    }


    public static List<GenericRecord> createUsersViaSchema(Schema schema) throws IOException {

        //Using this schema, let's create some users.

        GenericRecord user1 = new GenericData.Record(schema);
        user1.put("name", "Alyssa");
        user1.put("favorite_number", 256);
        // Leave favorite color null

        GenericRecord user2 = new GenericData.Record(schema);
        user2.put("name", "Ben");
        user2.put("favorite_number", 7);
        user2.put("favorite_color", "red");

        return Arrays.asList(user1,user2);
    }

    public static void serialiseViaGenericRecord(File file, Schema schema, List<GenericRecord> users )throws  IOException{
        // Serialize user1 and user2 to disk

        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(datumWriter);
        dataFileWriter.create(schema, file);
        dataFileWriter.append(users.get(0));
        dataFileWriter.append(users.get(1));
        dataFileWriter.close();
    }

    public static void main( String[] args ) throws IOException
    {

        User user1 = new User();
        user1.setName("Alyssa");
        user1.setFavoriteNumber(256);
        // Leave favorite color null

        // Alternate constructor
        User user2 = new User("Ben", 7, "red");

        // Construct via builder
        User user3 = User.newBuilder()
                     .setName("Charlie")
                     .setFavoriteColor("blue")
                     .setFavoriteNumber(null)
                     .build();


        File file  = new File("users.avro");
        // Serialize user1, user2 and user3 to disk
        serialise(user1,user2,user3,file);

        File avscFile =  new File("input/user.avsc");

        Schema schema = new Schema.Parser().parse(avscFile);

        List<GenericRecord> users = createUsersViaSchema(schema);

        deserialise(file);

        serialiseViaGenericRecord (file,schema,users);

        deserialiseViaGenericRecord(file,schema);
    }

    private static void deserialiseViaGenericRecord(File file,Schema schema)  throws IOException
    {
        // Deserialize users from disk
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>(schema);
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(file, datumReader);
        GenericRecord user = null;
        while (dataFileReader.hasNext()) {
            // Reuse user object by passing it to next(). This saves us from
            // allocating and garbage collecting many objects for files with
            // many items.
            user = dataFileReader.next(user);
            System.out.println(user);
        }
    }

}
