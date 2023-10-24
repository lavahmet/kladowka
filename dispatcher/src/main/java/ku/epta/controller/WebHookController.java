package ku.epta.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebHookController {
    private final UpdateProcessor updateProcessor;

    public WebHookController(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/callback/update")
    public ResponseEntity<?> onUpdateReceived(@RequestBody Update update) {
        updateProcessor.processUpdate(update);
        return ResponseEntity.ok().build();
    }
}
