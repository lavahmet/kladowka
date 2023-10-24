package ku.epta.service;

import org.springframework.stereotype.Service;

public interface UserActivationService {
    boolean activation(String cryptoUserId);
}
