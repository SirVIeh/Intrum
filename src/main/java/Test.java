import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;

public class Test {
    final static String url="http://intrumhomework.mocklab.io/v1/contact";
    public static void main(String[] args) throws IOException {
        List<String[]> csvData = createCsvDataSimple();

        String path = "";
        try
        {
            File temp = File.createTempFile("test", ".csv");
            System.out.println("Temp file created : " + temp.getAbsolutePath());
            path = temp.getAbsolutePath();
            //temp.deleteOnExit();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try (ICSVWriter writer = new CSVWriterBuilder(
                new FileWriter(path))
                .withSeparator('|')
                .build()) {
            writer.writeAll(csvData);
        }

        BufferedReader reader = new BufferedReader(new FileReader(path));
        List<String> lines = new ArrayList<>();
        String line = null;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        for(int i = 1; i < 6; i++) {
            String[] itemList = lines.get(i).split("\\|");

            URL url = new URL("http://intrumhomework.mocklab.io/v1/contact");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("Content-Type", "application/json");

            String data = "{\n\"id\": " + itemList[0].replaceAll("\"", "") + ",\n\"firstName\": \"" + itemList[1].replaceAll("\"", "") + "\",\n\"lastName\": \"" + itemList[2].replaceAll("\"", "") + "\",\n\"email\": \"" + itemList[3].replaceAll("\"", "") + "\",\n\"companyId\": " + itemList[4].replaceAll("\"", "") + "\n}";
            System.out.println(data);
            byte[] out = data.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = http.getOutputStream();
            stream.write(out);

            System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
            Assert.assertThat("Response was not correct", http.getResponseCode(), Matchers.equalTo(201));
            http.disconnect();
        }
    }
    private static List<String[]> createCsvDataSimple() {
        String[] header = {"id", "firstName", "lastName", "email", "companyId"};
        String[] record1 = {"1", "first_name", "last_name", "test1@gmail.com", "11111"};
        String[] record2 = {"2", "second_name", "last_name", "test2@gmail.com", "22222"};
        String[] record3 = {"3", "third_name", "last_name", "test3@gmail.com", "33333"};
        String[] record4 = {"4", "fourth_name", "last_name", "test4@gmail.com", "44444"};
        String[] record5 = {"5", "fifth_name", "last_name", "test5@gmail.com", "55555"};

        List<String[]> list = new ArrayList<>();
        list.add(header);
        list.add(record1);
        list.add(record2);
        list.add(record3);
        list.add(record4);
        list.add(record5);

        return list;
    }
}
