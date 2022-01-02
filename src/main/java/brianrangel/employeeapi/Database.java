package brianrangel.employeeapi;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

@Component
public class Database implements CommandLineRunner {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... strings) {

        // Create the employee table
        jdbcTemplate.execute("DROP TABLE employee IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE employee ( PRIMARY KEY (id), id INT, first_name VARCHAR(255), " +
                "last_name VARCHAR(255), email VARCHAR(255), phone VARCHAR(255), address VARCHAR(255), " +
                "hire_date VARCHAR(255), department VARCHAR(255), salary INT) ");

        // Insert our test data into the database by parsing a CSV file
        insertTestData("employee_data.csv");

        // Print out the contents of the employee table
        jdbcTemplate.query("SELECT * FROM employee", (rs, rowNum) -> new Employee(
                rs.getInt("id"), rs.getString("first_name"),
                rs.getString("last_name"), rs.getString("email"),
                rs.getString("phone"), rs.getString("address"),
                rs.getString("hire_date"), rs.getString("department"),
                rs.getInt("salary"))).forEach(employee -> System.out.println(employee.toString()));
    }

    public void insertTestData(String path) {
        try {
            Reader file = new FileReader(path);
            CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(file);

            for (CSVRecord record : parser) {
                jdbcTemplate.update("INSERT INTO employee(id, first_name, last_name, email, phone, " +
                                "address, hire_date, department, salary) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        record.get("id"), record.get("first_name"), record.get("last_name"),
                        record.get("email"), record.get("phone"), record.get("address"),
                        record.get("hire_date"), record.get("department"), record.get("salary"));
            }
        }
        catch (IOException e) {
            System.out.println("ERROR: " + e);
        }
    }
}
