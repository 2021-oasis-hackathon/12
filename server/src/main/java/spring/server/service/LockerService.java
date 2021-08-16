package spring.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.server.domain.Locker;
import spring.server.domain.User;
import spring.server.dto.EntrustDTO;
import spring.server.repository.LockerRepository;
import spring.server.token.JwtToken;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LockerService {
    private final LockerRepository lockerRepository;

    public void create(Locker locker) {
        lockerRepository.save(locker);
    }

    public List<Locker> getLockers(Long userId) {
        return lockerRepository.findAllNotMine(userId);
    }

    public List<Locker> findAll() {
        return lockerRepository.findAll();
    }

    public List<Locker> findByUserId(Long userId) {
        return lockerRepository.findByUserId(userId);
    }

    public Optional<Locker> findById(Long id) {
        return lockerRepository.findById(id);
    }

    public Locker findByQrcode(String qrcode) {
        return lockerRepository.findByQrcode(qrcode);
    }
}
