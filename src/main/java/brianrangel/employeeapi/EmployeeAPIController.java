package brianrangel.employeeapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmployeeAPIController {

    @Autowired
    Database database = new Database();

    @RequestMapping("/employee-api")
    public String index(Model model, @RequestParam (required = false) Integer id,
                        @RequestParam (required = false) String firstName,
                        @RequestParam (required = false) String lastName,
                        @RequestParam (required = false) String email,
                        @RequestParam (required = false) String phone,
                        @RequestParam (required = false) String address,
                        @RequestParam (required = false) String hireDate,
                        @RequestParam (required = false) String department,
                        @RequestParam (required = false) Integer salary) {

        if (id == null && firstName == null && lastName == null && email == null && phone == null &&
                address == null && hireDate == null && department == null && salary == null) {
            model.addAttribute("employees", database.listAll());
        } else {
            model.addAttribute("employees", database.listID(id));
        }
        return "index";
    }
}
