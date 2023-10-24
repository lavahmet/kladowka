package ku.epta.controller;

import ku.epta.service.UserActivationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class ActivationController {
    private final UserActivationService activationService;

    public ActivationController(UserActivationService activationService) {
        this.activationService = activationService;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id) {
        var res = activationService.activation(id);
        if (res) {
            return ResponseEntity.ok().body("Регистрация успешна завершена!");
        }
        return ResponseEntity.internalServerError().build();
    }
}
