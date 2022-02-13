package brianrangel.employeeapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmployeeAPIController {

    @Autowired
    Database database = new Database();

    @RequestMapping("/employee-api")
    public String index(@RequestParam (required = false) Integer id,
                        @RequestParam (required = false) String firstName,
                        @RequestParam (required = false) String lastName,
                        @RequestParam (required = false) String email,
                        @RequestParam (required = false) String phone,
                        @RequestParam (required = false) String address,
                        @RequestParam (required = false) String hireDate,
                        @RequestParam (required = false) String department,
                        @RequestParam (required = false) String salaryOpt,
                        @RequestParam (required = false) Integer salary,
                        Model model) {

        // Display all employees from the database
        if (id == null && firstName == null && lastName == null && email == null && phone == null &&
                address == null && hireDate == null && department == null && salary == null) {
            model.addAttribute("employees", database.selectAll());
        }
        // Filter through the database to only get the employees we want
        else {
            model.addAttribute("employees", database.selectWhere(id, firstName, lastName, email, phone,
                    address, hireDate, department, salaryOpt, salary));
        }
        return "index";
    }

    @RequestMapping("/employee-api/add")
    public String addEmployee(@RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String email,
                              @RequestParam String phone,
                              @RequestParam String address,
                              @RequestParam String hireDate,
                              @RequestParam String department,
                              @RequestParam Integer salary,
                              Model model) {

        // Create an employee with the following data
        database.addEmployee(firstName, lastName, email, phone, address, hireDate, department, salary);

        return "redirect:/employee-api";
    }

    @RequestMapping("/employee-api/delete/{id}")
    public String deleteEmployee(@PathVariable Integer id) {

        // Delete the employee with the specified ID
        database.deleteEmployee(id);
        return "redirect:/employee-api";
    }
}
