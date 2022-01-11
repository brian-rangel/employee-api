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
import java.util.ArrayList;
import java.util.List;

@Component
public class Database implements CommandLineRunner {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... strings) {

        // Create the employee table
        jdbcTemplate.execute("DROP TABLE employee IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE employee ( PRIMARY KEY (id), id INT AUTO_INCREMENT, " +
                "first_name VARCHAR(255), last_name VARCHAR(255), email VARCHAR(255), phone VARCHAR(255), " +
                "address VARCHAR(255), hire_date VARCHAR(255), department VARCHAR(255), salary INT) ");

        // Fill the database with test data
        insertTestData("src/main/resources/employee_data.csv");
    }

    // Insert our test data into the database by parsing a CSV file
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

    // Query the database using an SQL statement passed through as a parameter
    public List<Employee> queryDatabase(String sql) {
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> new Employee(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getString("hire_date"),
                rs.getString("department"),
                rs.getInt("salary"))));
    }

    // List all data from the database
    public List<Employee> selectAll() {
        return queryDatabase("SELECT * FROM employee");
    }

    // Create an SQL statement using the WHERE clause to filter through the database
    public List<Employee> selectWhere(Integer id, String firstName, String lastName, String email, String phone,
                                 String address, String hireDate, String department, String salaryOpt, Integer salary) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT * FROM employee WHERE ");
        Boolean calledLast = false;

        // Check for all conditions. If it is not null, add it to the SQL statement
        if (id != null) { stringBuilder.append("id = " + id + " AND "); }
        if (!firstName.isEmpty()) { stringBuilder.append("first_name LIKE '%" + firstName + "%' AND "); }
        if (!lastName.isEmpty()) { stringBuilder.append("last_name LIKE '%" + lastName + "%' AND "); }
        if (!email.isEmpty()) { stringBuilder.append("email LIKE '%" + email + "%' AND "); }
        if (!phone.isEmpty()) { stringBuilder.append("phone LIKE '%" + phone + "%' AND "); }
        if (!address.isEmpty()) { stringBuilder.append("address LIKE '%" + address + "%' AND "); }
        if (!hireDate.isEmpty()) { stringBuilder.append("hire_date LIKE '%" + hireDate + "%' AND "); }
        if (department != null) { stringBuilder.append("department LIKE '%" + department + "%' AND "); }

        if (salary != null) {
            if (salaryOpt.equals("=")) {
                stringBuilder.append("salary = " + salary);
            }
            else if (salaryOpt.equals(">")) {
                stringBuilder.append("salary > " + salary);
            }
            else if (salaryOpt.equals("<")) {
                stringBuilder.append("salary < " + salary);
            }
            calledLast = true;
        }

        // If salary wasn't the last condition in the statement, remove the AND clause
        if (!calledLast) { stringBuilder.delete(stringBuilder.length() - 4, stringBuilder.length()); }

        // Return the SQL statement that we created
        return queryDatabase(stringBuilder.toString());
    }
}
